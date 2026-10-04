package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable

/**
 * Intercepta el botón o gesto de volver atrás en plataformas que lo soportan (Android).
 * En iOS es un no-op seguro ya que la navegación se controla mediante la UI.
 */
@Composable
expect fun CobreBackHandler(enabled: Boolean = true, onBack: () -> Unit)
