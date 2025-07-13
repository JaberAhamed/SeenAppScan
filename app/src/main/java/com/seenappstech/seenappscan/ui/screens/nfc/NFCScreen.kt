package com.seenappstech.seenappscan.ui.screens.nfc

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.seenappstech.seenappscan.ui.screens.nfc.components.NfcScreen

@Composable
fun NFCScreen(
    modifier: Modifier = Modifier,
    scanningActive: Boolean,
    onStartScan: () -> Unit,
    tags: List<String>
) {
    NfcScreen(
        scanningActive = scanningActive,
        onStartScan = onStartScan,
        tags = tags
    )
}
