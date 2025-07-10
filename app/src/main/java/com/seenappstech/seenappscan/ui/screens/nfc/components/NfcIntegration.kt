package com.seenappstech.seenappscan.ui.screens.nfc.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NfcScreen(
    scanningActive: Boolean,
    onStartScan: () -> Unit,
    tags: List<String>
) {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onStartScan,
            enabled = !scanningActive,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (scanningActive) "Scan" else "Start NFC Scan")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(tags) { tag ->
                Card(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = "Tag ID: $tag",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
