package com.elcobre.lavanderiaelcobre.data.model

import java.util.Calendar

internal actual fun obtenerHoraActualDelDia(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
