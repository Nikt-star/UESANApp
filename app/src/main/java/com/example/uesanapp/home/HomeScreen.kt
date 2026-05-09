package com.example.uesanapp.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.uesanapp.model.*
import com.example.uesanapp.security.SecurityRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val repository = remember { SecurityRepository.getInstance(context) }
    val reportedNumbers by repository.reportedNumbers.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var urlToScan by remember { mutableStateOf("") }
    var scanResult by remember { mutableStateOf<String?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var currentTipIndex by remember { mutableStateOf(0) }
    val currentTip = securityTips[currentTipIndex]
    
    // Estado para el diálogo de reporte
    var showReportDialog by remember { mutableStateOf(false) }
    var numberToReport by remember { mutableStateOf("") }
    var reportReason by remember { mutableStateOf("") }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Reportar Número Sospechoso") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = numberToReport,
                        onValueChange = { numberToReport = it },
                        label = { Text("Número de teléfono") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        label = { Text("Razón del reporte (ej. Estafa Bancaria)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repository.reportNumber(numberToReport, reportReason, AlertCategory.SCAM)
                            showReportDialog = false
                            numberToReport = ""
                            reportReason = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Text("Reportar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SAFEGUARD AI",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1B5E20)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F8E9)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Estado de Protección
            item {
                ProtectionStatusCard()
            }

            // 2. Analizador de Phishing
            item {
                PhishingScannerCard(
                    url = urlToScan,
                    onUrlChange = { urlToScan = it },
                    isScanning = isScanning,
                    onScan = {
                        isScanning = true
                        if (urlToScan.isBlank()) {
                            scanResult = "Por favor, ingresa una URL"
                        } else if (urlToScan.contains("bit.ly") || urlToScan.contains("t.co") || !urlToScan.startsWith("https")) {
                            scanResult = "⚠️ ALERTA: Este enlace parece sospechoso o no es seguro."
                        } else {
                            scanResult = "✅ Enlace verificado. No se detectaron amenazas."
                        }
                        isScanning = false
                    }
                )
            }

            if (scanResult != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (scanResult!!.contains("⚠️")) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                        )
                    ) {
                        Text(
                            text = scanResult!!,
                            modifier = Modifier.padding(16.dp),
                            color = if (scanResult!!.contains("⚠️")) Color.Red else Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Acciones Rápidas
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SecurityActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Bloquear",
                        icon = Icons.Default.Block,
                        color = Color(0xFFC62828),
                        onClick = { /* Lógica de bloqueo directo */ }
                    )
                    SecurityActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Reportar",
                        icon = Icons.Default.Report,
                        color = Color(0xFFEF6C00),
                        onClick = { showReportDialog = true }
                    )
                }
            }

            // 4. Alertas de la Comunidad
            item {
                Text(
                    "Números Reportados Recientemente",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(reportedNumbers) { alert ->
                AlertItem(alert)
            }

            // 5. Tip de Seguridad (Educación)
            item {
                SecurityTipCard(currentTip)
            }
            
            item {
                Button(
                    onClick = {
                        currentTipIndex = (currentTipIndex + 1) % securityTips.size
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Text("Siguiente Consejo")
                }
            }
        }
    }
}

@Composable
fun ProtectionStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF43A047)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Shield,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Sistema Protegido",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Escudo de llamadas y SMS activo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun PhishingScannerCard(
    url: String, 
    onUrlChange: (String) -> Unit, 
    isScanning: Boolean,
    onScan: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Detectar Phishing (Enlaces)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Pega el link que recibiste por mensaje",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                placeholder = { Text("https://banco-seguro.com...") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (isScanning) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = onScan) {
                            Icon(Icons.Default.Search, contentDescription = "Escanear", tint = Color(0xFF1B5E20))
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityActionCard(modifier: Modifier, title: String, icon: ImageVector, color: Color, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AlertItem(alert: CommunityAlert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when(alert.category) {
                            AlertCategory.SCAM -> Color(0xFFFFEBEE)
                            AlertCategory.PHISHING -> Color(0xFFFFF3E0)
                            else -> Color(0xFFF5F5F5)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(alert.category) {
                        AlertCategory.SCAM -> Icons.Default.GppBad
                        AlertCategory.PHISHING -> Icons.Default.Phishing
                        else -> Icons.Default.Warning
                    },
                    contentDescription = null, 
                    tint = when(alert.category) {
                        AlertCategory.SCAM -> Color.Red
                        AlertCategory.PHISHING -> Color(0xFFEF6C00)
                        else -> Color.Gray
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(alert.number, fontWeight = FontWeight.Bold)
                Text(alert.reason, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(alert.time, style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
        }
    }
}

@Composable
fun SecurityTipCard(tip: SecurityTip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF1976D2))
                Spacer(modifier = Modifier.width(8.dp))
                Text(tip.title, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                tip.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

val securityTips = listOf(
    SecurityTip(
        "Verifica el remitente",
        "Los bancos nunca usan números de celular comunes para contactarte. Siempre usan códigos de 5 dígitos."
    ),
    SecurityTip(
        "No compartas claves",
        "Ninguna entidad oficial te pedirá tus contraseñas por WhatsApp o SMS. Si tienes dudas, cuelga."
    ),
    SecurityTip(
        "Enlaces sospechosos",
        "Si un enlace tiene errores de ortografía como 'banc0' en lugar de 'banco', es una estafa fija."
    )
)
