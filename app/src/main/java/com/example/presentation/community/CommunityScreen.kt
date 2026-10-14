package com.example.presentation.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.FigmaBackground
import com.example.ui.theme.FigmaBorder
import com.example.ui.theme.FigmaSurface
import com.example.ui.theme.FigmaTealChip
import com.example.ui.theme.FigmaTealLight
import com.example.ui.theme.FigmaTealPrimary
import com.example.ui.theme.FigmaTextMuted
import com.example.ui.theme.FigmaTextPrimary
import com.example.ui.theme.FigmaTextSecondary

data class FigmaPost(
    val id: String,
    val authorName: String,
    val authorInitials: String,
    val avatarBgColor: Color,
    val timeAgo: String,
    val category: String,
    val content: String,
    val imageUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Todo") }
    var showCreatePostSheet by remember { mutableStateOf(false) }

    // Categories matching Figma Screen 2
    val categories = remember {
        listOf("Todo", "Eventos", "Medio Ambiente", "Huerto", "Seguridad")
    }

    // Posts matching Figma Screen 2
    var posts by remember {
        mutableStateOf(
            listOf(
                FigmaPost(
                    id = "1",
                    authorName = "María González",
                    authorInitials = "MG",
                    avatarBgColor = Color(0xFF47766F),
                    timeAgo = "hace 12 min",
                    category = "Medio Ambiente",
                    content = "¡Increíble la limpieza del parque hoy! Más de 40 vecinos participaron. Así se hace comunidad 🌿",
                    imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=80"
                ),
                FigmaPost(
                    id = "2",
                    authorName = "Carlos Ruiz",
                    authorInitials = "CR",
                    avatarBgColor = Color(0xFF2C5852),
                    timeAgo = "hace 1 h",
                    category = "Huerto",
                    content = "El huerto urbano de la calle Olmo ya tiene sus primeras lechugas listas. ¡Quien quiera puede pasar a recolectar!"
                ),
                FigmaPost(
                    id = "3",
                    authorName = "Laura Sanz",
                    authorInitials = "LS",
                    avatarBgColor = Color(0xFF4D7D76),
                    timeAgo = "hace 3 h",
                    category = "Evento",
                    content = "Recordad que mañana hay taller de reciclaje en el Centro Cívico, 17:00 h. ¡Plazas limitadas, inscribíos ya!",
                    imageUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=1200&q=80"
                )
            )
        )
    }

    val filteredPosts = remember(selectedCategory, posts) {
        if (selectedCategory == "Todo") posts
        else posts.filter {
            it.category.contains(selectedCategory, ignoreCase = true) ||
                    (selectedCategory == "Eventos" && it.category == "Evento")
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FigmaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp)
        ) {
            // 1. HEADER ROW: "Comunidad" + Teal Square "+" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comunidad",
                    color = FigmaTextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                // Teal Square "+" button (40dp x 40dp, corner 10dp)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FigmaTealPrimary,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { showCreatePostSheet = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Crear publicación",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. CATEGORY FILTER CHIPS ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) FigmaTealPrimary else FigmaTealChip,
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else Color(0xFF476662),
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. COMMUNITY FEED POSTS
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 85.dp)
            ) {
                items(filteredPosts, key = { it.id }) { post ->
                    CommunityPostCard(post = post)
                }
            }
        }

        // Create Post Modal BottomSheet
        if (showCreatePostSheet) {
            val sheetState = rememberModalBottomSheetState()
            var newContent by remember { mutableStateOf("") }
            var newCategory by remember { mutableStateOf("Medio Ambiente") }

            ModalBottomSheet(
                onDismissRequest = { showCreatePostSheet = false },
                sheetState = sheetState,
                containerColor = FigmaSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .padding(bottom = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nueva publicación",
                            color = FigmaTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showCreatePostSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = FigmaTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "CATEGORÍA",
                        color = FigmaTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Medio Ambiente", "Huerto", "Evento", "Seguridad").forEach { cat ->
                            val isSel = newCategory == cat
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSel) FigmaTealPrimary else FigmaTealChip,
                                modifier = Modifier.clickable { newCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) Color.White else Color(0xFF476662),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "¿QUÉ QUIERES COMPARTIR?",
                        color = FigmaTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        placeholder = { Text("Escribe aquí tu mensaje para la comunidad...", color = FigmaTextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FigmaTealPrimary,
                            unfocusedBorderColor = FigmaBorder,
                            focusedContainerColor = FigmaSurface,
                            unfocusedContainerColor = FigmaSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (newContent.isNotBlank()) {
                                val created = FigmaPost(
                                    id = System.currentTimeMillis().toString(),
                                    authorName = "Vecino Activo",
                                    authorInitials = "VA",
                                    avatarBgColor = FigmaTealPrimary,
                                    timeAgo = "ahora",
                                    category = newCategory,
                                    content = newContent
                                )
                                posts = listOf(created) + posts
                                showCreatePostSheet = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FigmaTealPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Publicar en Comunidad", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunityPostCard(post: FigmaPost) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = FigmaSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author row: Avatar circle, Name, time, Category pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Avatar circle with initials
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(post.avatarBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorInitials,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = post.authorName,
                            color = FigmaTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = post.timeAgo,
                            color = FigmaTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                // Category pill on top right
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FigmaTealLight
                ) {
                    Text(
                        text = post.category,
                        color = FigmaTealPrimary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post content text
            Text(
                text = post.content,
                color = FigmaTextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            // Optional Image banner
            if (post.imageUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            }
        }
    }
}
