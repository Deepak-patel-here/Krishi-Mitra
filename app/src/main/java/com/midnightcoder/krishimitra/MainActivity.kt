package com.midnightcoder.krishimitra

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.midnightcoder.krishimitra.ui.screens.homescreen.HomeScreen
import com.midnightcoder.krishimitra.ui.screens.permission.AudioTextProvider
import com.midnightcoder.krishimitra.ui.screens.permission.PermissionDialog
import com.midnightcoder.krishimitra.ui.theme.KrishiMitraTheme
import com.midnightcoder.krishimitra.ui.viewmodel.PermissionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    val multiplePermission=arrayOf(
        Manifest.permission.RECORD_AUDIO
    )
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {


            KrishiMitraTheme {

                val viewModel= viewModel<PermissionViewModel>()
                val dialogQueue=viewModel.visiblePermissionDialogQueue
                val context= LocalContext.current

                val multiplePermissionResultLauncher= rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = {perms->
                        multiplePermission.forEach {permission->
                            viewModel.onPermissionResult(
                                permission = permission,
                                isGranted = perms[permission] == true
                            )
                        }

                    }
                )
                LaunchedEffect(Unit) {
                    multiplePermissionResultLauncher.launch(
                        multiplePermission
                    )
                }
                dialogQueue.reversed().forEach { permission ->
                    PermissionDialog(
                        permissionTextProvider = when (permission) {

                            Manifest.permission.RECORD_AUDIO -> {
                                AudioTextProvider()
                            }

                            else -> return@forEach
                        },
                        onDismiss = viewModel::dismissDialog,
                        onOkClick = {
                            viewModel.dismissDialog()
                            multiplePermissionResultLauncher.launch(
                                arrayOf(
                                    permission
                                )
                            )
                        },
                        onGoToAppSettingsClick = { openAppSettings(context) },
                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission)
                    )
                }
                HomeScreen(onImageSelected = {})
            }
        }
    }
    private fun openAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }
}

