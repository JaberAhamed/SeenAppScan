package com.seenappstech.seenappscan.ui.screens.qrcode.component

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenappstech.seenappscan.ui.theme.SeenappscanTheme

@Composable
fun ShowBarCode(
    modifier: Modifier = Modifier,
    barcodeValue: String,
    reScan: () -> Unit,
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Spacer(Modifier.weight(1f))

            Icon(
                modifier =
                    Modifier.clickable {
                        clipboardManager.setText(AnnotatedString(barcodeValue))
                        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                    },
                imageVector = Icons.Rounded.CopyAll,
                contentDescription = "",
            )

            Spacer(Modifier.width(12.dp))

            Icon(
                modifier =
                    Modifier.clickable {
                        val intent =
                            Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, barcodeValue)
                            }
                        val chooser = Intent.createChooser(intent, "Share via")
                        context.startActivity(chooser)
                    },
                imageVector = Icons.Rounded.Share,
                contentDescription = "",
            )
        }

        ClickableLinkText(barcodeValue)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = reScan,
        ) {
            Text("Re-Scan")
        }
    }
}

@Composable
fun ClickableLinkText(text: String) {
    val context = LocalContext.current

    if (text.startsWith("http://") || text.startsWith("https://")) {
        Text(
            modifier =
                Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(text))
                    context.startActivity(intent)
                },
            text =
                AnnotatedString(
                    text,
                    spanStyle =
                        SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                        ),
                ),
        )
    } else {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun ShowBarCodePreview() {
    SeenappscanTheme {
        ShowBarCode(
            barcodeValue = "Hello",
        ) {}
    }
}
