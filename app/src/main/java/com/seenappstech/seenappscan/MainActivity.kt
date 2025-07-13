package com.seenappstech.seenappscan

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.seenappstech.seenappscan.ui.screens.nfc.NFCScreen
import com.seenappstech.seenappscan.ui.screens.qrcode.QRCodeScreen
import com.seenappstech.seenappscan.ui.theme.SeenappscanTheme

object NfcTagStorage {
    private val _tags = mutableStateListOf<String>()
    val tags: List<String> get() = _tags

    fun addTag(tag: String) {
        if (!_tags.contains(tag)) {
            // Prepend tag to the list to show recent scans first
            _tags.add(0, tag)
        }
    }
}

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null

    // Flag to indicate NFC scan is active
    private val scanningMode = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            SeenappscanTheme {
                MainScreen(
                    scanningActive = scanningMode.value,
                    onQrCodeScan = {
                        scanningMode.value = false
                    },
                    onStartScan = {
                        scanningMode.value = true
                        if (nfcAdapter == null) {
                            Toast.makeText(this, "Nfc is not available", Toast.LENGTH_SHORT).show()
                        } else if (nfcAdapter!!.isEnabled) {
                            Toast.makeText(this, "Nfc is  available but off", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Nfc is available", Toast.LENGTH_SHORT).show()
                            enableNfcForegroundDispatch()
                        }
                    },
                    tags = NfcTagStorage.tags
                )
            }
        }
    }

    private fun enableNfcForegroundDispatch() {
        val intent =
            Intent(this, javaClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
        scanningMode.value = false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Process only if scan was initiated by the user
        if (scanningMode.value && intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }

            val tagId = tag?.id?.joinToString("") { String.format("%02X", it) }
            tagId?.let {
                NfcTagStorage.addTag(it)
            }
            scanningMode.value = false
        }
    }
}

@Composable
fun MainScreen(
    scanningActive: Boolean,
    onStartScan: () -> Unit,
    onQrCodeScan: () -> Unit,
    tags: List<String>
) {
    val navController = rememberNavController()
    Scaffold(
        modifier =
        Modifier
            .fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        val graph =
            navController.createGraph(startDestination = Screen.QRScan.rout) {
                composable(route = Screen.QRScan.rout) {
                    onQrCodeScan()
                    QRCodeScreen()
                }
                composable(route = Screen.NFCScan.rout) {
                    NFCScreen(
                        scanningActive = scanningActive,
                        onStartScan = onStartScan,
                        tags = tags
                    )
                }
            }
        NavHost(
            navController = navController,
            graph = graph,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val selectedNavigationIndex =
        rememberSaveable {
            mutableIntStateOf(0)
        }
    NavigationBar(
        containerColor = Color.White
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    Text(
                        item.title,
                        color =
                        if (index == selectedNavigationIndex.intValue) {
                            Color.Black
                        } else {
                            Color.Gray
                        }
                    )
                },
                colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

sealed class Screen(
    val rout: String
) {
    data object QRScan : Screen("qr_screen")

    data object NFCScan : Screen("nfc_screen")
}

val navigationItems =
    listOf(
        NavigationItem(
            title = "QRScan",
            icon = Icons.Default.QrCode,
            route = Screen.QRScan.rout
        ),
        NavigationItem(
            title = "NFCScan",
            icon = Icons.Default.Nfc,
            route = Screen.NFCScan.rout
        )
    )

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SeenappscanTheme {
        MainScreen(
            scanningActive = false,
            onStartScan = {},
            onQrCodeScan = {},
            tags = emptyList()
        )
    }
}
