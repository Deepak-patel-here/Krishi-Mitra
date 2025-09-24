package com.midnightcoder.krishimitra.ui.screens.permission

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined:Boolean,
    onDismiss:()->Unit,
    onOkClick:()->Unit,
    onGoToAppSettingsClick:()->Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column (modifier = Modifier.fillMaxWidth()){
                Divider()
                Text(
                    text = if (isPermanentlyDeclined){
                        "Grant Permission"
                    }else{
                        "OK"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable{
                            if(isPermanentlyDeclined){
                                onGoToAppSettingsClick()
                            }else {
                                onOkClick()
                            }
                        },
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

            }
        },
        title = {
            Text("Permission required")
        },
        text = {
            Text(
                text =permissionTextProvider.getDescription(isPermanentlyDeclined)
            )
        },
        modifier = modifier

    )
}

interface PermissionTextProvider{
    fun getDescription(isPermanentlyDeclined: Boolean):String
}

class AudioTextProvider:PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seems you permanently declined Audio permission. " +
                    "You can go to the app settings to grant it."
        }else {
            "This app needs access to your Auido so that you " +
                    "can Talk to Ai."
        }
    }
}

class NotificationPermissionTextProvider: PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seems you permanently declined notifications permission. " +
                    "You can go to the app settings to grant it."
        }else {
            "This app needs access to your notifications so that you " +
                    "you can remind to take medicine on time."
        }
    }
}