package com.udb.appfinanzas.core.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.udb.appfinanzas.BuildConfig

/** Android 17 exige autorización antes de abrir conexiones a una API de la red local. */
@Composable
fun AccesoRedLocal(content: @Composable () -> Unit) {
    val host = remember { Uri.parse(BuildConfig.API_BASE_URL).host.orEmpty().lowercase() }
    val esLocal = host == "localhost" || host == "::1" || host == "[::1]" ||
        host.startsWith("127.") || host.startsWith("10.") || host.startsWith("192.168.") ||
        host.matches(Regex("172\\.(1[6-9]|2[0-9]|3[01])\\..*")) || host.endsWith(".local")

    if (Build.VERSION.SDK_INT < 37 || !esLocal) {
        content()
        return
    }

    val context = LocalContext.current
    val permiso = Manifest.permission.ACCESS_LOCAL_NETWORK
    fun tienePermiso() = ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED
    var permitido by remember { mutableStateOf(tienePermiso()) }
    var solicitado by rememberSaveable { mutableStateOf(false) }
    val solicitud = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permitido = it
    }
    val ajustes = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        permitido = tienePermiso()
    }
    ActualizarAlReanudar { permitido = tienePermiso() }

    LaunchedEffect(Unit) {
        if (!permitido && !solicitado) {
            solicitado = true
            solicitud.launch(permiso)
        }
    }

    if (permitido) {
        content()
    } else {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.safeDrawingPadding().padding(24.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Conecta con tu PC", style = MaterialTheme.typography.headlineMedium)
                Text("Para crear tu cuenta e iniciar sesión, Finanzas UDB necesita conectarse al servidor que está en tu PC.")
                Text("Android solicita el permiso de dispositivos cercanos para permitir esta conexión local.")
                Button(onClick = { solicitud.launch(permiso) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Permitir conexión local")
                }
                TextButton(onClick = {
                    ajustes.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
                }) {
                    Text("Abrir ajustes de la app")
                }
            }
        }
    }
}
