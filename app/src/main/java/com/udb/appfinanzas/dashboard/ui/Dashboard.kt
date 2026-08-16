package com.udb.appfinanzas.dashboard.ui

import ScaffoldApp
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//usamos el molde de la app
@Composable
fun Dashboard()
{
    ScaffoldApp(title = "Dashboard",
        navigationIcon = {Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)} )
    {
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxSize()){
            //tarjeta que muestra monto semanal
            ElevatedCard(elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .size(width = 340.dp, height = 175.dp)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopCenter)){

                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)) {
                    Text("Saldo Semanal", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically)
                 {
                        Icon(imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp))
                     Text("-143.23", fontSize = 40.sp, color = Color.Red)
                 }
                }
            }
        }
    }
}

//Preview
@Preview(showBackground = true,
    showSystemUi = true)
@Composable
private fun DashboardPreview(){
    MaterialTheme{
        Dashboard()
    }
}