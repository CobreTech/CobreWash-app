package com.elcobre.lavanderiaelcobre

import androidx.compose.ui.window.ComposeUIViewController

// PascalCase intencional: es el punto de entrada de la UI para iOS (convención Compose/KMP).
@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController { App() }
