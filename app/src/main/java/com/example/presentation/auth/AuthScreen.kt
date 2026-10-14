package com.example.presentation.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FigmaBackground
import com.example.ui.theme.FigmaBorder
import com.example.ui.theme.FigmaSurface
import com.example.ui.theme.FigmaTealLight
import com.example.ui.theme.FigmaTealPrimary
import com.example.ui.theme.FigmaTextMuted
import com.example.ui.theme.FigmaTextPrimary
import com.example.ui.theme.FigmaTextSecondary

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }

    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var neighborhood by remember { mutableStateOf("El Poblado") }
    var isSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FigmaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 95.dp)
        ) {
            // 1. HEADER: "Únete a nosotros"
            Text(
                text = "Únete a nosotros",
                color = FigmaTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Step Indicator: "Paso 1 de 3"
            Text(
                text = "Paso $step de 3",
                color = FigmaTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE2EFEF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = step / 3f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(FigmaTealPrimary)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (isSuccess) {
                // Success registered state
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = FigmaSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(FigmaTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FigmaTealPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "¡Bienvenido, $name!",
                            color = FigmaTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Tu perfil vecinal ha sido creado con éxito en $neighborhood. Ya puedes participar y conectar con tu barrio.",
                            color = FigmaTextSecondary,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { isSuccess = false; step = 1 },
                            colors = ButtonDefaults.buttonColors(containerColor = FigmaTealPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Modificar datos", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                when (step) {
                    1 -> {
                        // STEP 1: Datos personales
                        Text(
                            text = "Datos personales",
                            color = FigmaTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Field 1: NOMBRE
                        FigmaFormField(
                            label = "NOMBRE",
                            value = name,
                            onValueChange = { name = it },
                            placeholder = "Ej: María"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Field 2: APELLIDOS
                        FigmaFormField(
                            label = "APELLIDOS",
                            value = lastName,
                            onValueChange = { lastName = it },
                            placeholder = "Ej: González López"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Field 3: CORREO ELECTRÓNICO
                        FigmaFormField(
                            label = "CORREO ELECTRÓNICO",
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "tu@email.com",
                            keyboardType = KeyboardType.Email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Field 4: CONTRASEÑA
                        Text(
                            text = "CONTRASEÑA",
                            color = FigmaTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Mín. 8 caracteres", color = FigmaTextMuted) },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = FigmaTextMuted
                                    )
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FigmaTealPrimary,
                                unfocusedBorderColor = Color(0xFFD7E5E3),
                                focusedContainerColor = FigmaSurface,
                                unfocusedContainerColor = FigmaSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        // Big Button: Continuar
                        Button(
                            onClick = { step = 2 },
                            colors = ButtonDefaults.buttonColors(containerColor = FigmaTealPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "Continuar",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    2 -> {
                        // STEP 2: Ubicación y Barrio
                        Text(
                            text = "Ubicación en Medellín",
                            color = FigmaTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        FigmaFormField(
                            label = "BARRIO O COMUNA",
                            value = neighborhood,
                            onValueChange = { neighborhood = it },
                            placeholder = "Ej: Laureles, Poblado, Belén..."
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        FigmaFormField(
                            label = "TELÉFONO DE CONTACTO (OPCIONAL)",
                            value = "",
                            onValueChange = { },
                            placeholder = "+57 300 123 4567",
                            keyboardType = KeyboardType.Phone
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFEBF2F1),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clickable { step = 1 }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Atrás", color = FigmaTealPrimary, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = { step = 3 },
                                colors = ButtonDefaults.buttonColors(containerColor = FigmaTealPrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(2f)
                                    .height(52.dp)
                            ) {
                                Text("Continuar", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    3 -> {
                        // STEP 3: Intereses Comunitarios
                        Text(
                            text = "¿Qué te interesa en tu barrio?",
                            color = FigmaTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        listOf("Medio Ambiente y Huertos", "Eventos Vecinales", "Seguridad y Cuadrantes", "Talleres Culturales").forEach { interest ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = FigmaSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(interest, color = FigmaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        Button(
                            onClick = { isSuccess = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FigmaTealPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text("Completar registro", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FigmaFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            color = FigmaTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = FigmaTextMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FigmaTealPrimary,
                unfocusedBorderColor = Color(0xFFD7E5E3),
                focusedContainerColor = FigmaSurface,
                unfocusedContainerColor = FigmaSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
