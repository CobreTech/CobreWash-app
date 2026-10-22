package com.elcobre.lavanderiaelcobre.data.auth

/** Solo devuelve un perfil después de validar la identidad actual con el servidor. */
expect suspend fun restaurarPerfilAutenticado(): PerfilResultado?
