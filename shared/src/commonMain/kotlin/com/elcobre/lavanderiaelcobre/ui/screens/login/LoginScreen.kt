package com.elcobre.lavanderiaelcobre.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.auth.AuthRepository
import com.elcobre.lavanderiaelcobre.data.auth.AuthResultado
import com.elcobre.lavanderiaelcobre.data.auth.PerfilRepository
import com.elcobre.lavanderiaelcobre.data.auth.PerfilResultado
import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.data.model.TurnoPlanta
import com.elcobre.lavanderiaelcobre.ui.components.BrandCtaGradient
import com.elcobre.lavanderiaelcobre.ui.components.CobreBackground
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.GlassCard
import com.elcobre.lavanderiaelcobre.ui.components.brandHeadingColor
import com.elcobre.lavanderiaelcobre.ui.components.brandMutedColor
import com.elcobre.lavanderiaelcobre.ui.components.cobreFieldColors
import com.elcobre.lavanderiaelcobre.ui.components.pressableScale
import com.elcobre.lavanderiaelcobre.ui.theme.Brand400
import com.elcobre.lavanderiaelcobre.ui.theme.Brand500
import com.elcobre.lavanderiaelcobre.ui.theme.Brand700
import com.elcobre.lavanderiaelcobre.ui.theme.Copper500
import com.elcobre.lavanderiaelcobre.ui.theme.Copper600
import com.elcobre.lavanderiaelcobre.ui.theme.DarkOutline
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface2
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.Stone200
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lavanderiaelcobre.shared.generated.resources.Res
import lavanderiaelcobre.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

/** Mapea el rol de Data Connect (igual que lib/roles.ts en la web) a una sesión de la app. */
internal fun sesionParaRol(
    rolNombre: String,
    nombre: String,
    turno: TurnoPlanta = TurnoPlanta.turnoActual(),
): Sesion? = when (rolNombre) {
    "operario" -> Sesion.DeOperario(Operario(nombre = nombre, rol = "Operario de planta", turno = turno.etiqueta))
    "admin" -> Sesion.DeAdministrador(Administrador(nombre = nombre))
    else -> null // "recepcionista" y "cliente" no tienen vista en esta app.
}

@Composable
fun LoginScreen(
    onLogin: (Sesion) -> Unit,
    onToggleTema: () -> Unit = {},
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val turnoAutomatico = remember { TurnoPlanta.turnoActual() }
    var error by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    var sesionLista by remember { mutableStateOf<Sesion?>(null) }
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val perfilRepository = remember { PerfilRepository() }

    LaunchedEffect(Unit) { visible = true }

    // Éxito → redirección, igual que el modal de la web (Navbar.tsx).
    LaunchedEffect(success) {
        val sesion = sesionLista
        if (success && sesion != null) {
            delay(1200)
            onLogin(sesion)
            success = false
        }
    }

    fun intentarEntrar() {
        if (correo.isBlank() || password.isBlank()) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            error = "Por favor ingresa tu correo y contraseña."
            return
        }
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        error = null
        isLoading = true
        scope.launch {
            when (val resultado = authRepository.iniciarSesion(correo.trim(), password)) {
                is AuthResultado.Exito -> {
                    when (val perfil = perfilRepository.obtenerMiPerfil()) {
                        is PerfilResultado.Exito -> {
                            val sesion = sesionParaRol(perfil.rolNombre, perfil.nombre, TurnoPlanta.turnoActual())
                            isLoading = false
                            if (sesion == null) {
                                authRepository.cerrarSesion()
                                error = "Tu cuenta no tiene acceso a la app móvil."
                            } else {
                                sesionLista = sesion
                                success = true
                            }
                        }
                        is PerfilResultado.Error -> {
                            authRepository.cerrarSesion()
                            isLoading = false
                            error = perfil.mensaje
                        }
                    }
                }
                is AuthResultado.Error -> {
                    isLoading = false
                    error = resultado.mensaje
                }
            }
        }
    }

    CobreBackground {
        val isDark = cobreIsDark()
        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    LoginCard(
                        visible = visible,
                        success = success,
                        correo = correo,
                        onCorreoChange = { correo = it; error = null },
                        password = password,
                        onPasswordChange = { password = it; error = null },
                        turno = turnoAutomatico,
                        error = error,
                        isLoading = isLoading,
                        onSubmit = { intentarEntrar() },
                    )
                    Spacer(Modifier.height(24.dp))
                    LoginFooter()
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
            ) {
                LoginThemeToggle(isDark = isDark, onClick = onToggleTema)
            }
        }
    }
}

@Composable
private fun LoginThemeToggle(isDark: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val btnBg = if (isDark) DarkSurface2 else Color.White
    val borderCol = if (isDark) DarkOutline else Stone200.copy(alpha = 0.8f)

    Box(
        modifier = Modifier
            .size(48.dp)
            .pressableScale(interaction)
            .clip(RoundedCornerShape(14.dp))
            .background(btnBg)
            .border(1.dp, borderCol, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClickLabel = if (isDark) "Cambiar a tema claro" else "Cambiar a tema oscuro",
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (isDark) CobreIcons.Sun else CobreIcons.Moon,
            contentDescription = if (isDark) "Tema oscuro activo, cambiar a claro" else "Tema claro activo, cambiar a oscuro",
            tint = if (isDark) Brand400 else Brand500,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun LoginCard(
    visible: Boolean,
    success: Boolean,
    correo: String,
    onCorreoChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    turno: TurnoPlanta,
    error: String?,
    isLoading: Boolean,
    onSubmit: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 6 },
    ) {
        GlassCard(
            modifier = Modifier.widthIn(max = 460.dp),
            shape = RoundedCornerShape(24.dp),
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
                        correo = correo,
                        onCorreoChange = onCorreoChange,
                        password = password,
                        onPasswordChange = onPasswordChange,
                        turno = turno,
                        error = error,
                        isLoading = isLoading,
                        onSubmit = onSubmit,
                    )
                }
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
        modifier = Modifier.semantics { heading() },
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
        modifier = Modifier
            .padding(vertical = 28.dp)
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
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
    correo: String,
    onCorreoChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    turno: TurnoPlanta,
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
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
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
            keyboardOptions = KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.None,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next,
            ),
        )
        Spacer(Modifier.height(16.dp))
        LoginField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            leadingIcon = CobreIcons.Lock,
            isPassword = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done,
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { onSubmit() }),
        )
        Spacer(Modifier.height(12.dp))
        TurnoDetectadoBanner(turno = turno)
        Spacer(Modifier.height(8.dp))
        val haptic = LocalHapticFeedback.current
        var aviso by remember { mutableStateOf(false) }
        LaunchedEffect(aviso) { if (aviso) { delay(2000); aviso = false } }
        val olvideInteraction = remember { MutableInteractionSource() }
        Text(
            "¿Olvidaste tu contraseña?",
            style = MaterialTheme.typography.labelSmall,
            color = Copper600,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(interactionSource = olvideInteraction, indication = null, role = Role.Button) {
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
                modifier = Modifier.align(Alignment.End).semantics { liveRegion = LiveRegionMode.Polite },
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
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp).semantics { contentDescription = "Verificando credenciales" },
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text("Ingresar", style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TurnoDetectadoBanner(turno: TurnoPlanta) {
    val isDark = cobreIsDark()
    val accent = if (isDark) Brand400 else Brand500
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = if (isDark) 0.14f else 0.08f))
            .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "Turno de planta asignado automáticamente: ${turno.etiqueta}, horario ${turno.horario}"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(CobreIcons.Schedule, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            Text(
                "${turno.etiqueta} (${turno.horario})",
                style = MaterialTheme.typography.labelMedium,
                color = brandHeadingColor(),
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            "Automático",
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun LoginFooter() {
    Text(
        "Lavandería El Cobre · Gestión interna",
        style = MaterialTheme.typography.labelSmall,
        color = Brand700.copy(alpha = 0.5f),
    )
    Text(
        "Acceso seguro para personal autorizado",
        style = MaterialTheme.typography.labelSmall,
        color = Copper500.copy(alpha = 0.4f),
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String?,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
) {
    var passwordVisible by remember { mutableStateOf(false) }
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
        trailingIcon = if (isPassword) {
            {
                androidx.compose.material3.IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) CobreIcons.VisibilityOff else CobreIcons.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = Brand700,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation()
            else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = cobreFieldColors(),
    )
}
