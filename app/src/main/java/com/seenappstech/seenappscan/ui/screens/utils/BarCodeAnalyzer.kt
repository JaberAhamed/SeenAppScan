package com.seenappstech.seenappscan.ui.screens.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val context: Context,
    private val onBarcodeDetected: (String) -> Unit,
) : ImageAnalysis.Analyzer {
    private val options =
        BarcodeScannerOptions
            .Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image
            ?.let { image ->
                scanner
                    .process(
                        InputImage.fromMediaImage(
                            image,
                            imageProxy.imageInfo.rotationDegrees,
                        ),
                    ).addOnSuccessListener { barcode ->
                        barcode
                            ?.takeIf { it.isNotEmpty() }
                            ?.mapNotNull { it.rawValue }
                            ?.joinToString(",")
                            ?.let {
                                onBarcodeDetected(it)
                            }
                    }.addOnCompleteListener {
                        imageProxy.close()
                    }
            }
    }
}
