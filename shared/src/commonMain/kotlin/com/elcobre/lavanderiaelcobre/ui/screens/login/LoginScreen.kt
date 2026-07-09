package com.elcobre.lavanderiaelcobre.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.mock.MockData
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.cobreFieldColors
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.Copper500
import com.elcobre.lavanderiaelcobre.ui.theme.Copper600
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark
import kotlinx.coroutines.delay
import lavanderiaelcobre.shared.generated.resources.Res
import lavanderiaelcobre.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

/**
 * Rol con el que ingresa el prototipo: la web no tiene este selector (el rol lo
 * resuelve el backend tras autenticar), pero el prototipo no tiene backend, así
 * que necesita una forma de elegir a qué vista entrar — se resuelve como un link
 * discreto debajo del formulario en vez de un selector prominente, para no alejar
 * el diseño del de la web más de lo estrictamente necesario.
 */
private enum class RolLogin(val etiqueta: String) {
    OPERARIO("Operario"),
    ADMINISTRADOR("Administrador"),
}

@Composable
fun LoginScreen(onLogin: (Sesion) -> Unit) {
    var rol by remember { mutableStateOf(RolLogin.OPERARIO) }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) { visible = true }

    // Loading → éxito → redirección, igual que el modal de la web (Navbar.tsx).
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(900)
            isLoading = false
            success = true
        }
    }

    LaunchedEffect(success) {
        if (success) {
            delay(1200)
            onLogin(
                when (rol) {
                    RolLogin.OPERARIO -> Sesion.DeOperario(MockData.operario)
                    RolLogin.ADMINISTRADOR -> Sesion.DeAdministrador(MockData.administrador)
                },
            )
            success = false
        }
    }

    fun intentarEntrar() {
        if (correo.isBlank() || password.isBlank()) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            error = "Por favor ingresa tu correo y contraseña."
        } else {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            error = null
            isLoading = true
        }
    }

    CobreBackground {
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 6 },
                ) {
                    GlassCard(
                        modifier = Modifier.widthIn(max = 460.dp),
                        shape = RoundedCornerShape(24.dp), // rounded-3xl de la web, radio de sus modales
                        contentPadding = PaddingValues(32.dp),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            LoginHeader()
                            Spacer(Modifier.height(24.dp))
                            AnimatedVisibility(visible = success) {
                                LoginSuccess()
                            }
                            AnimatedVisibility(visible = !success) {
                                LoginForm(
                                    rol = rol,
                                    onCambiarRol = { rol = it },
                                    correo = correo,
                                    onCorreoChange = { correo = it; error = null },
                                    password = password,
                                    onPasswordChange = { password = it; error = null },
                                    error = error,
                                    isLoading = isLoading,
                                    onSubmit = { intentarEntrar() },
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                LoginFooter()
            }
        }
    }
}

@Composable
private fun LoginHeader() {
    Image(
        painter = painterResource(Res.drawable.logo),
        contentDescription = "Lavandería El Cobre",
        modifier = Modifier.size(50.dp),
    )
    Spacer(Modifier.height(12.dp))
    Text(
        "Iniciar Sesión",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
        color = brandHeadingColor(),
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(6.dp))
    Text(
        "Ingresa tus datos para acceder a tu intranet de lavandería.",
        style = MaterialTheme.typography.bodySmall,
        color = brandMutedColor(),
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = 260.dp),
    )
}

/** Check verde + mensaje, igual que el estado `authSuccess` del modal de la web antes de redirigir. */
@Composable
private fun LoginSuccess() {
    val isDark = cobreIsDark()
    val verde = if (isDark) StatusGreenDark else StatusGreen
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 28.dp),
    ) {
        Icon(CobreIcons.CheckCircle, contentDescription = null, tint = verde, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(14.dp))
        Text("¡Acceso Exitoso!", style = MaterialTheme.typography.titleMedium, color = brandHeadingColor(), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(
            "Redireccionando a tu intranet...",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LoginForm(
    rol: RolLogin,
    onCambiarRol: (RolLogin) -> Unit,
    correo: String,
    onCorreoChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    error: String?,
    isLoading: Boolean,
    onSubmit: () -> Unit,
) {
    // Column propio: al llamarse dentro de un AnimatedVisibility (que se comporta
    // como un Box, apilando en vez de arreglar en columna), sin esto los campos y
    // el botón quedaban todos superpuestos en el mismo punto.
    Column(modifier = Modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = error != null) {
            val rojo = if (cobreIsDark()) StatusRedDark else StatusRed
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .background(rojo.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(CobreIcons.ErrorOutline, contentDescription = null, tint = rojo, modifier = Modifier.size(18.dp))
                Text(error.orEmpty(), style = MaterialTheme.typography.bodySmall, color = rojo)
            }
        }

        LoginField(
            value = correo,
            onValueChange = onCorreoChange,
            label = "Correo Electrónico",
            leadingIcon = CobreIcons.Mail,
        )
        Spacer(Modifier.height(16.dp))

        LoginField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            leadingIcon = CobreIcons.Lock,
            isPassword = true,
        )
        Spacer(Modifier.height(8.dp))

        // Texto clickeable simple, no TextButton: este último fuerza un alto
        // mínimo táctil (~40dp) que hacía ver todo apretado. El padding en el
        // propio clickable amplía el área táctil a ~40dp sin agrandar el texto.
        val haptic = LocalHapticFeedback.current
        var aviso by remember { mutableStateOf(false) }
        LaunchedEffect(aviso) {
            if (aviso) {
                delay(2000)
                aviso = false
            }
        }
        val olvideInteraction = remember { MutableInteractionSource() }
        Text(
            "¿Olvidaste tu contraseña?",
            style = MaterialTheme.typography.labelSmall,
            color = Copper600,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(interactionSource = olvideInteraction, indication = null) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    aviso = true
                }
                .padding(vertical = 10.dp),
        )
        AnimatedVisibility(visible = aviso) {
            Text(
                "Disponible próximamente",
                style = MaterialTheme.typography.labelSmall,
                color = brandMutedColor(),
                modifier = Modifier.align(Alignment.End),
            )
        }

        Spacer(Modifier.height(16.dp))

        val interaction = remember { MutableInteractionSource() }
        Button(
            onClick = onSubmit,
            enabled = !isLoading,
            interactionSource = interaction,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .pressableScale(interaction)
                .background(BrandCtaGradient, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Ingresar", style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))
        CambiarRolLink(rol, onCambiarRol)
    }
}

/** Link discreto para elegir con qué rol entrar; ver nota en [RolLogin]. */
@Composable
private fun CambiarRolLink(rol: RolLogin, onCambiarRol: (RolLogin) -> Unit) {
    val otro = if (rol == RolLogin.OPERARIO) RolLogin.ADMINISTRADOR else RolLogin.OPERARIO
    val interaction = remember { MutableInteractionSource() }
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Text(
            "¿Ingresas como ${otro.etiqueta.lowercase()}? ",
            style = MaterialTheme.typography.labelSmall,
            color = brandMutedColor(),
        )
        Text(
            "Cambiar",
            style = MaterialTheme.typography.labelSmall,
            color = Copper600,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .pressableScale(interaction)
                .clickable(interactionSource = interaction, indication = null) { onCambiarRol(otro) }
                .padding(vertical = 10.dp),
        )
    }
}

@Composable
private fun LoginFooter() {
    Text(
        "v1.0.4-beta · Lavandería El Cobre S.A.",
        style = MaterialTheme.typography.labelSmall,
        color = Brand700.copy(alpha = 0.5f),
    )
    Text(
        "Demostración de Prototipo",
        style = MaterialTheme.typography.labelSmall,
        color = Copper500.copy(alpha = 0.4f),
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String?,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label?.let { { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = Brand700, modifier = Modifier.size(20.dp))
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions.Default,
        colors = cobreFieldColors(),
    )
}
