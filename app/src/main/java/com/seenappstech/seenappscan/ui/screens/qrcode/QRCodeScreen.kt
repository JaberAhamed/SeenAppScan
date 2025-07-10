package com.seenappstech.seenappscan.ui.screens.qrcode

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.seenappstech.seenappscan.ui.screens.qrcode.component.CameraSection
import com.seenappstech.seenappscan.ui.screens.qrcode.component.ShowBarCode
import com.seenappstech.seenappscan.ui.theme.SeenappscanTheme

@Composable
fun QRCodeScreen() {
    FeatureThatRequiresCameraPermission()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun FeatureThatRequiresCameraPermission() {
    val cameraPermissionState =
        rememberPermissionState(
            android.Manifest.permission.CAMERA
        )
    var barcodeValue by remember { mutableStateOf("") }

    if (barcodeValue.isNotEmpty()) {
        ShowBarCode(
            barcodeValue = barcodeValue
        ) {
            barcodeValue = ""
        }
    } else {
        if (cameraPermissionState.status.isGranted) {
            CameraSection(
                modifier = Modifier,
                //  cameraProvider = cameraProviderFuture,
                onBarcodeDetect = {
                    barcodeValue = it
                }
            )
        } else {
            Column {
                if (cameraPermissionState.status.shouldShowRationale) {
                    Text("The camera is important for this app. Please grant the permission.")
                } else {
                    SideEffect {
                        cameraPermissionState.run { launchPermissionRequest() }
                    }
                    Text("No Camera Permission")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QRCodeScreenPreview() {
    SeenappscanTheme {
        QRCodeScreen()
    }
}
