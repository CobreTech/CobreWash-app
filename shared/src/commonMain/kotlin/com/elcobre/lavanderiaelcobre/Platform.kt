package com.elcobre.lavanderiaelcobre

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
