package com.seenappstech.seenappscan.ui.screens.qrcode.component

import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.seenappstech.seenappscan.ui.screens.utils.BarcodeAnalyzer

@Composable
fun CameraSection(
    onBarcodeDetect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var zoomValue by remember { mutableFloatStateOf(2f) }
    var camera: Camera? by remember { mutableStateOf(null) }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // Only run once when entering the screen
    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder().build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(context),
            BarcodeAnalyzer(
                context = context,
                onBarcodeDetected = {
                    onBarcodeDetect(it)
                    cameraProvider.unbindAll()
                    camera = null
                }
            )
        )

        try {
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // UI
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color.Transparent,
        targetValue = Color.Yellow,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = ""
    )

    Column(modifier = modifier) {
        Box(modifier = Modifier, contentAlignment = Alignment.Center) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { previewView }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight(0.4f)
                    .border(
                        BorderStroke(width = 0.5.dp, color = Color.White),
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = animatedColor
                )
            }

            Slider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                value = zoomValue,
                onValueChange = {
                    zoomValue = it
                    camera?.cameraControl?.setZoomRatio(it)
                },
                valueRange = 1f..4f
            )
        }
    }
}
