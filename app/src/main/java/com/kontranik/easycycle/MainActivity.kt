package com.kontranik.easycycle

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.core.content.ContextCompat
import com.kontranik.easycycle.model.Settings
import com.kontranik.easycycle.storage.SettingsService
import com.kontranik.easycycle.ui.DrawerParams.drawerButtons
import com.kontranik.easycycle.ui.MainCompose
import com.kontranik.easycycle.ui.theme.EasyCycleTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val showOnStart = SettingsService.loadSettings(this).showOnStart
        val start = drawerButtons[showOnStart].drawerOption
        Log.d("MainActivity", "onCreate: $start")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val alertDialogBuilder = AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getString(R.string.title_notification_alert_permission))
                alertDialogBuilder.setMessage(getString(R.string.message_notification_alert_permission));
                alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                    DialogInterface.OnClickListener { _, _ ->
                        val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(intent)
                    })
                alertDialogBuilder.setNegativeButton(getString(R.string.no),
                    DialogInterface.OnClickListener { _, _ -> { } })
                val alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }


        setContent {
            EasyCycleTheme {
                val context = LocalContext.current

                var hasNotificationPermission by remember {
                    mutableStateOf(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        } else true
                    )
                }

                val permissionRequest =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { result ->
                        hasNotificationPermission = result
                        // if granted you can show notification here
                        // if(hasNotificationPermission) showNotification(context)
                    }

                // Effekt, der beim Start der Composable ausgeführt wird
                LaunchedEffect(Unit) {
                    if (hasNotificationPermission) {
                        // showNotification(context)
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                MainCompose(start)
            }
        }
    }
}
