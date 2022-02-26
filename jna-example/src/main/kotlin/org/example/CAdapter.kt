package org.example

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference

interface CLib : Library {
    fun greet(firstname: String, lastname: String)
    fun greetAll(persons: Person.ByReference, size: Int)
    fun greetDelayed(callback: DelayedGreeting)
    fun getPersonsToGreet(personsToReturn: PointerByReference, size: IntByReference)
    fun freePersonsToGreet(personsToFree: Pointer)

    interface DelayedGreeting : Callback {
        fun invoke(firstname: String, lastname: String)
    }

    @Structure.FieldOrder("firstname", "lastname")
    open class Person : Structure {
        class ByReference : Person(), Structure.ByReference

        constructor() : super()
        constructor(p: Pointer) : super(p)
        constructor(firstname: String, lastname: String) {
            this.firstname = firstname
            this.lastname = lastname
        }

        @JvmField
        var firstname: String = ""

        @JvmField
        var lastname: String = ""

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
        OperatingSystem.UNIX -> "../c-library/cmake-build-debug/libNative_Interfaces.so".loadRelativeCLib()
        OperatingSystem.DARWIN -> "../c-library/cmake-build-debug/libNative_Interfaces.dylib".loadRelativeCLib()
        OperatingSystem.WINDOWS -> "../c-library/cmake-build-debug/libNative_Interfaces.dll".loadRelativeCLib()
        else -> throw java.lang.IllegalStateException("Platform is not supported")
    }

    // Test functionalities
    callbackFromCtoJvm(library)
    "Call C function from JVM".printBanner()
    library.greet("Black", "Widow")
    "Passing JVM array to C".printBanner()
    jvmArrayToC(library)
    "Passing C array to JVM".printBanner()
    receiveStructArrayFromC(library).forEach {
        println("Received person: ${it.firstname}, ${it.lastname}")
    }

    // Sleep to assure callback is invoked
    Thread.sleep(2000)
}

fun callbackFromCtoJvm(library: CLib) {
    val callback = object : CLib.DelayedGreeting {
        override fun invoke(firstname: String, lastname: String) {
            "JVM callback from C".printBanner()
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

fun receiveStructArrayFromC(library: CLib): Array<CLib.Person> {
    val arrayPointerRef = PointerByReference()
    val sizePointer = IntByReference()
    library.getPersonsToGreet(arrayPointerRef, sizePointer)

    val size = sizePointer.value
    val personPointer = arrayPointerRef.value
    val personRef = CLib.Person(personPointer)
    personRef.read()

    val persons = personRef.toArray(size) as Array<CLib.Person>
    library.freePersonsToGreet(personPointer)
    return persons
}

fun String.loadRelativeCLib(): CLib = Native.load(this.toAbsolutePath(), CLib::class.java) as CLib
fun String.printBanner() {
    val banner = "#".repeat(10)
    println("\n$banner $this")
    println("$banner${"#".repeat(this.length + 1)}")
}
