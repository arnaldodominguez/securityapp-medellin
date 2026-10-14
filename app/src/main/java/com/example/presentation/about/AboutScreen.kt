package com.example.presentation.about

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.FigmaBackground
import com.example.ui.theme.FigmaBorder
import com.example.ui.theme.FigmaSurface
import com.example.ui.theme.FigmaTealLight
import com.example.ui.theme.FigmaTealPrimary
import com.example.ui.theme.FigmaTextMuted
import com.example.ui.theme.FigmaTextPrimary
import com.example.ui.theme.FigmaTextSecondary

data class TeamMember(
    val name: String,
    val role: String,
    val avatarUrl: String
)

data class ValueItem(
    val iconEmoji: String,
    val title: String,
    val description: String
)

@Composable
fun AboutScreen(
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val teamMembers = listOf(
        TeamMember(
            name = "Ana Martínez",
            role = "Fundadora & Directora",
            avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=300&q=80"
        ),
        TeamMember(
            name = "Diego Fernández",
            role = "Coordinador de Proyectos",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=300&q=80"
        ),
        TeamMember(
            name = "Sara López",
            role = "Diseño y Comunicación",
            avatarUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=300&q=80"
        )
    )

    val values = listOf(
        ValueItem(
            iconEmoji = "🌱",
            title = "Sostenibilidad",
            description = "Cada acción orientada a un futuro más verde."
        ),
        ValueItem(
            iconEmoji = "🤝",
            title = "Colaboración",
            description = "Construimos juntos, nadie queda atrás."
        ),
        ValueItem(
            iconEmoji = "📍",
            title = "Arraigo local",
            description = "Soluciones que nacen del barrio, para el barrio."
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FigmaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 95.dp)
        ) {
            // 1. HERO HEADER WITH VOLUNTEER PHOTO & "Quiénes somos" TITLE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1559027615-cd4628902d4a?auto=format&fit=crop&w=1200&q=80",
                    contentDescription = "Voluntarios",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x990A1B18), Color(0xCC081513)),
                                startY = 60f
                            )
                        )
                )

                Text(
                    text = "Quiénes\nsomos",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 24.dp)
                )
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(28.dp))

                // 2. NUESTRA MISIÓN
                Text(
                    text = "NUESTRA MISIÓN",
                    color = Color(0xFF4D736E),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Conectamos a vecinas y vecinos para transformar sus barrios en comunidades más vivas, verdes y participativas. Creemos que el cambio empieza en la calle de al lado.",
                    color = FigmaTextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. STATS 2x2 GRID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(number = "2.400+", label = "Vecinos activos", modifier = Modifier.weight(1f))
                    StatCard(number = "38", label = "Proyectos vivos", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(number = "12", label = "Barrios", modifier = Modifier.weight(1f))
                    StatCard(number = "6", label = "Años de historia", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(30.dp))

                // 4. NUESTROS VALORES
                Text(
                    text = "NUESTROS VALORES",
                    color = Color(0xFF4D736E),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                values.forEach { valItem ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = FigmaSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(valItem.iconEmoji, fontSize = 24.sp)
                            Column {
                                Text(
                                    text = valItem.title,
                                    color = FigmaTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = valItem.description,
                                    color = FigmaTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // 5. EL EQUIPO
                Text(
                    text = "EL EQUIPO",
                    color = Color(0xFF4D736E),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                teamMembers.forEach { member ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = FigmaSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
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
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AsyncImage(
                                    model = member.avatarUrl,
                                    contentDescription = member.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                )

                                Column {
                                    Text(
                                        text = member.name,
                                        color = FigmaTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = member.role,
                                        color = FigmaTextSecondary,
                                        fontSize = 12.5.sp
                                    )
                                }
                            }

                            // LinkedIn badge pill (#EBF2F1)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEDF4F3),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "in",
                                        color = FigmaTealPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // 6. "¿QUIERES COLABORAR?" CTA BOX
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = FigmaTealPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¿Quieres colaborar?",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Escríbenos y sumamos tu energía al proyecto.",
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 13.5.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:contacto@comunidadmedellin.org")
                                    putExtra(Intent.EXTRA_SUBJECT, "Quiero colaborar con la comunidad")
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(46.dp)
                        ) {
                            Text(
                                text = "Contactar",
                                color = FigmaTealPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = FigmaSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = number,
                color = FigmaTealPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = FigmaTextSecondary,
                fontSize = 12.5.sp
            )
        }
    }
}
