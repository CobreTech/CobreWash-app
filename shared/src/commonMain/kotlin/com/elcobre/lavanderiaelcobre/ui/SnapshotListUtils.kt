package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.runtime.snapshots.SnapshotStateList

/** Busca el primer elemento que cumple [predicate] y lo reemplaza por su copia transformada; no-op si no hay match. */
internal inline fun <T> SnapshotStateList<T>.reemplazarPrimero(predicate: (T) -> Boolean, transform: (T) -> T) {
    val i = indexOfFirst(predicate)
    if (i >= 0) this[i] = transform(this[i])
}
