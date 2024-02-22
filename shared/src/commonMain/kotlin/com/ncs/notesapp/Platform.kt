package com.ncs.notesapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform