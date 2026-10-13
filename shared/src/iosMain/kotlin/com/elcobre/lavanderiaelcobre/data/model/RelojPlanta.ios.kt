package com.elcobre.lavanderiaelcobre.data.model

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSDate

internal actual fun obtenerHoraActualDelDia(): Int {
    val date = NSDate()
    val calendar = NSCalendar.currentCalendar
    return calendar.component(NSCalendarUnitHour, fromDate = date).toInt()
}
