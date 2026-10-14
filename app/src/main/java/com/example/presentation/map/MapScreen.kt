package com.example.presentation.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComunaZone
import com.example.ui.theme.FigmaBackground
import com.example.ui.theme.FigmaBorder
import com.example.ui.theme.FigmaInputBg
import com.example.ui.theme.FigmaSurface
import com.example.ui.theme.FigmaTealLight
import com.example.ui.theme.FigmaTealPrimary
import com.example.ui.theme.FigmaTextMuted
import com.example.ui.theme.FigmaTextPrimary
import com.example.ui.theme.FigmaTextSecondary

data class NearbyPlace(
    val id: String,
    val title: String,
    val distance: String,
    val activeCount: Int,
    val icon: ImageVector,
    val comunaId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlaceTitle by remember { mutableStateOf("Parque Central") }
    var showAllPlacesSheet by remember { mutableStateOf(false) }

    // Request Runtime Permissions for GPS & Bluetooth Low Energy
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.startTrackingAndBle()
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
        viewModel.startTrackingAndBle()
    }

    // Nearby places matching Figma Screen 1
    val nearbyPlaces = remember {
        listOf(
            NearbyPlace(
                id = "1",
                title = "Parque Central",
                distance = "0.3 km",
                activeCount = 134,
                icon = Icons.Default.Park,
                comunaId = 10 // La Candelaria / Centro
            ),
            NearbyPlace(
                id = "2",
                title = "Biblioteca Municipal",
                distance = "0.7 km",
                activeCount = 203,
                icon = Icons.Default.MenuBook,
                comunaId = 11 // Laureles / Estadio
            ),
            NearbyPlace(
                id = "3",
                title = "Huerto Urbano",
                distance = "1.2 km",
                activeCount = 47,
                icon = Icons.Default.LocalFlorist,
                comunaId = 14 // El Poblado
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FigmaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 90.dp)
        ) {
            // 1. SCREEN TITLE: "Explorar zona"
            Text(
                text = "Explorar zona",
                color = FigmaTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // 2. SEARCH BAR: "Buscar lugar o zona..."
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = FigmaInputBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = FigmaTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Buscar lugar o zona...",
                                color = FigmaTextMuted,
                                fontSize = 14.sp
                            )
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = FigmaTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar",
                            tint = FigmaTextMuted,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { searchQuery = "" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. MAP CARD (Medellín Real Map with Free OpenStreetMap & ESP32-C3 Telemetry)
            MedellinRealMapView(
                comunas = state.comunas,
                activeComuna = state.activeComuna,
                gpsStatus = state.gpsStatus,
                zoneEvaluation = state.zoneEvaluation,
                bleConnectionState = state.bleConnectionState,
                onSelectComuna = { comuna ->
                    viewModel.selectComuna(comuna)
                    selectedPlaceTitle = comuna.sectorName
                },
                onReconnectBle = {
                    viewModel.reconnectBle()
                }
            )

            // Telemetry quick test row (1=Rojo, 2=Amarillo, 3=Verde, 4=Sin GPS)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = FigmaSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Simulador de Señal ESP32-C3 (Prueba de Hardware):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FigmaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEE2E2),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    state.comunas.firstOrNull { it.riskScore >= 55 }?.let {
                                        viewModel.selectComuna(it)
                                    }
                                }
                        ) {
                            Text(
                                text = "1: Rojo",
                                color = Color(0xFFB91C1C),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF3C7),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    state.comunas.firstOrNull { it.riskScore in 35..54 }?.let {
                                        viewModel.selectComuna(it)
                                    }
                                }
                        ) {
                            Text(
                                text = "2: Amarillo",
                                color = Color(0xFFB45309),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFD1FAE5),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    state.comunas.firstOrNull { it.riskScore < 35 }?.let {
                                        viewModel.selectComuna(it)
                                    }
                                }
                        ) {
                            Text(
                                text = "3: Verde",
                                color = Color(0xFF047857),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF64748B)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.simulateNoGps()
                                }
                        ) {
                            Text(
                                text = "4: Sin GPS",
                                color = Color(0xFF334155),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. SECTION HEADER: "Cerca de ti" & "Ver todo"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cerca de ti",
                    color = FigmaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Ver todo",
                    color = FigmaTealPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { showAllPlacesSheet = true }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. NEARBY PLACES LIST CARDS
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                nearbyPlaces.forEach { place ->
                    val isSelected = selectedPlaceTitle == place.title
                    NearbyPlaceCard(
                        place = place,
                        isSelected = isSelected,
                        onClick = {
                            selectedPlaceTitle = place.title
                            state.comunas.firstOrNull { it.id == place.comunaId }?.let {
                                viewModel.selectComuna(it)
                            }
                        }
                    )
                }
            }
        }

        // Bottom Sheet for "Ver todo"
        if (showAllPlacesSheet) {
            val sheetState = rememberModalBottomSheetState()
            ModalBottomSheet(
                onDismissRequest = { showAllPlacesSheet = false },
                sheetState = sheetState,
                containerColor = FigmaSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Lugares y Sectores en Medellín",
                        color = FigmaTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    state.comunas.take(8).forEach { comuna ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = FigmaSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable {
                                    viewModel.selectComuna(comuna)
                                    selectedPlaceTitle = comuna.sectorName
                                    showAllPlacesSheet = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(FigmaTealLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Park,
                                            contentDescription = null,
                                            tint = FigmaTealPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = comuna.name,
                                            color = FigmaTextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Cuadrante ${comuna.activeQuadrant} • ${comuna.riskScore}% índice",
                                            color = FigmaTextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = FigmaTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyPlaceCard(
    place: NearbyPlace,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = FigmaSurface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) FigmaTealPrimary else FigmaBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Square icon container (#EDF5F3)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FigmaTealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = place.icon,
                        contentDescription = place.title,
                        tint = FigmaTealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = place.title,
                        color = FigmaTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${place.distance} • ${place.activeCount} activos",
                        color = FigmaTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Detalles",
                tint = FigmaTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
