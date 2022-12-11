package org.example

import java.nio.file.FileSystems


enum class OperatingSystem {
    WINDOWS, UNIX, DARWIN, OTHER;
}

fun determineOs(): OperatingSystem {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> OperatingSystem.WINDOWS
        os.contains("mac") -> OperatingSystem.DARWIN
        os.contains("(nix)|(nux)|(aix)".toRegex()) -> OperatingSystem.UNIX
        else -> OperatingSystem.OTHER
    }
}

fun String.toAbsolutePath(): String = FileSystems.getDefault().getPath(this).normalize().toAbsolutePath().toString()