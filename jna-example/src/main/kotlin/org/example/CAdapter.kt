package org.example

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Structure

interface CLib : Library {
    fun greet(firstname: String, lastname: String)
    fun greetAll(persons: Person.ByReference, size: Int)
    fun greetDelayed(callback: DelayedGreeting)

    interface DelayedGreeting : Callback {
        fun invoke(firstname: String, lastname: String)
    }

    @Structure.FieldOrder("firstname", "lastname")
    open class Person(
        @JvmField var firstname: String = "",
        @JvmField var lastname: String = "",
    ) : Structure() {
        class ByReference : Person(), Structure.ByReference
        companion object {
            fun fromList(persons: List<Person>): Pair<ByReference, Int> {
                val personRef = ByReference()
                val array = personRef.toArray(persons.size) as Array<Person>
                persons.forEachIndexed { i, person ->
                    array[i].firstname = person.firstname
                    array[i].lastname = person.lastname
                }
                return personRef to persons.size
            }
        }
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


    callbackFromCtoJvm(library)
    library.greet("Black", "Widow")
    jvmArrayToC(library)
    Thread.sleep(2000)
}

fun callbackFromCtoJvm(library: CLib) {
    val callback = object : CLib.DelayedGreeting {
        override fun invoke(firstname: String, lastname: String) {
            println("Async greetings to : $firstname $lastname")
        }
    }
    library.greetDelayed(callback)
}

fun jvmArrayToC(library: CLib) {
    val (personRef, size) = CLib.Person.fromList(
        listOf(
            CLib.Person(firstname = "Peter", lastname = "Parker"),
            CLib.Person(firstname = "Tony", lastname = "Stark")
        )
    )
    library.greetAll(personRef, size)
}
