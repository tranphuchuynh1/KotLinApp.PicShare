package com.example.fontendpicshare

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.layout.ContentScale
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainForm(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var camera: Camera? by remember { mutableStateOf(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var flashEnabled by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // State for BottomSheetScaffold
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(hasCameraPermission) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun bindCameraUseCases(cameraProvider: ProcessCameraProvider, view: PreviewView?) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(view?.surfaceProvider)
        }
        imageCapture = ImageCapture.Builder().setFlashMode(ImageCapture.FLASH_MODE_OFF).build()

        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    var newImageUrl by remember { mutableStateOf<String?>(null) }

    fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            context.filesDir,
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("MainForm", "Photo saved: ${photoFile.absolutePath}")

                    // Hiển thị ảnh ngay lập tức trong danh sách
                    newImageUrl = Uri.fromFile(photoFile).toString()

                    // Tải ảnh lên Firebase Storage trong background
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val photoRef = FirebaseStorage.getInstance().reference.child("images/${photoFile.name}")
                            photoRef.putFile(Uri.fromFile(photoFile)).await()

                            val downloadUrl = photoRef.downloadUrl.await()
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            val firestore = FirebaseFirestore.getInstance()

                            // Lưu thông tin ảnh vào subcollection của người dùng
                            userId?.let { id ->
                                val imageData = hashMapOf(
                                    "url" to downloadUrl.toString(),
                                    "timestamp" to System.currentTimeMillis()
                                )
                                firestore.collection("users").document(id).collection("images").add(imageData).await()

                                // Mở BottomSheet để hiển thị ảnh mới
                                coroutineScope.launch(Dispatchers.Main) {
                                    Toast.makeText(context, "Ảnh đã lưu và tải lên", Toast.LENGTH_LONG).show()
                                    bottomSheetScaffoldState.bottomSheetState.expand()
                                }
                            }
                        } catch (e: Exception) {
                            coroutineScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Lỗi lưu ảnh: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(context, "Lỗi lưu ảnh: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        )
    }




    @Composable
    fun ImageHistory(newImageUrl: String?) {
        val context = LocalContext.current
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        var images by remember { mutableStateOf<List<String>>(emptyList()) }
        val coroutineScope = rememberCoroutineScope()

        fun fetchImages() {
            userId?.let { id ->
                coroutineScope.launch {
                    firestore.collection("users").document(id).collection("images")
                        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { result ->
                            images = result.mapNotNull { document ->
                                document.getString("url")
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Lỗi tải danh sách ảnh", Toast.LENGTH_LONG).show()
                        }
                }
            }
        }

        LaunchedEffect(newImageUrl) {
            fetchImages()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            newImageUrl?.let {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.DarkGray)
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = it),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ảnh mới nhất",
                                color = Color.White,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(images) { imageUrl ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.DarkGray)
                        .padding(16.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ảnh cũ hơn",
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }








    if (hasCameraPermission) {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray
                ) {
                    ImageHistory(newImageUrl) // Truyền vào URL ảnh mới chụp
                }
            },
            sheetPeekHeight = 0.dp // Điều chỉnh chiều cao nếu cần
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black)
            ) {
                Box(
                    modifier = Modifier.align(Alignment.TopCenter)
                        .padding(top = 80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .size(width = 350.dp, height = 450.dp)
                        .background(Color.Gray)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { view ->
                        previewView = view
                        coroutineScope.launch(Dispatchers.Main) {
                            val cameraProvider = cameraProviderFuture.get()
                            bindCameraUseCases(cameraProvider, view)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate("settingUser")
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "User Profile",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { navController.navigate("groupScreen") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.group), // Use group.png
                            contentDescription = "Group",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { /* Handle chat action */ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.message),
                            contentDescription = "Chat",
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            tint = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                flashEnabled = !flashEnabled
                                camera?.cameraControl?.enableTorch(flashEnabled)
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (flashEnabled) R.drawable.flashon else R.drawable.flash
                                ),
                                contentDescription = "Flash",
                                tint = Color.White
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(
                                onClick = {
                                    takePhoto()
                                },
                                modifier = Modifier.size(80.dp).background(Color.White, CircleShape)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(70.dp).background(Color(0xFFFFC107), CircleShape)
                                ) {
                                    Box(
                                        modifier = Modifier.size(60.dp).background(Color.White, CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(
                                    text = "Lịch Sử",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.clickable {
                                        coroutineScope.launch {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    }
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                                val cameraProvider = cameraProviderFuture.get()
                                bindCameraUseCases(cameraProvider, previewView)
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.camera),
                                contentDescription = "Switch Camera",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
