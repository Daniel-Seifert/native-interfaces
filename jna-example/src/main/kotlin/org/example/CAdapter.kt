package org.example

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native

interface CLib : Library {
    fun greet(firstname: String, lastname: String)
    fun greetDelayed(callback: DelayedGreeting)

    interface DelayedGreeting : Callback {
        fun invoke(firstname: String, lastname: String)
    }
}

fun main(args: Array<String>) {
    val platform = determineOs()
    println("Running on Platform: $platform")
    val library = when (platform) {
        OperatingSystem.UNIX -> Native.load(
            "../c-library/cmake-build-debug/libNative_Interfaces.so".toAbsolutePath(),
            CLib::class.java
        ) as CLib
        OperatingSystem.DARWIN -> Native.load(
            "../c-library/cmake-build-debug/libNative_Interfaces.dylib".toAbsolutePath(),
            CLib::class.java
        ) as CLib
        else -> throw java.lang.IllegalStateException("Platform is not supported")
    }

    // Simple function call
    library.greet("Peter", "Parker")

    // Callback C -> JVM
    val callback = object : CLib.DelayedGreeting {
        override fun invoke(firstname: String, lastname: String) {
            println("Greetings to : $firstname $lastname")
        }
    }
    library.greetDelayed(callback)
    Thread.sleep(2000)
}

