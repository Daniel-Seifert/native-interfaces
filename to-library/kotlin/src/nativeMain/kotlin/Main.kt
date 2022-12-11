import kotlinx.cinterop.*
import nativelib.DelayedGreeting
import nativelib.Person
import platform.posix.sleep

fun main() {
    memScoped {

        "Trigger Callback in C".printBanner()
        val callback: DelayedGreeting =
            staticCFunction { firstname: CPointer<ByteVarOf<Byte>>?, lastname: CPointer<ByteVarOf<Byte>>? ->
                "Callback from C".printBanner()
                println("Async greetings to : ${firstname?.toKString()} ${lastname?.toKString()}")
            }
        nativelib.greetDelayed(callback)

        "Call C function from Kotlin native".printBanner()
        nativelib.greet("Black".cstr, "Widow".cstr)

        "Passing array to C".printBanner()
        val persons = listOf(
            "Peter".cstr.ptr to "Parker".cstr.ptr,
            "Tony".cstr.ptr to "Stark".cstr.ptr
        )
        val array = allocArray<Person>(persons.size)
        persons.forEachIndexed { index, person ->
            array[index].firstname = person.first
            array[index].lastname = person.second
        }
        nativelib.greetAll(array, persons.size)

        "Passing C array to Kotlin native".printBanner()
        val personsFromC = alloc<CPointerVar<Person>>()
        val personsInCAmount = alloc<IntVar>()
        nativelib.getPersonsToGreet(personsFromC.ptr, personsInCAmount.ptr)
        println("${personsInCAmount.value} Persons at ${personsFromC.value}")
        (0 until personsInCAmount.value).forEach { index ->
            personsFromC.value?.get(index)?.let { person ->
                println("$index: ${person.firstname?.toKString()}, ${person.lastname?.toKString()}")
            }
        }
        nativelib.freePersonsToGreet(personsFromC.value)

        // Sleep to assure callback is invoked
        sleep(2)
    }
}

fun String.printBanner() {
    val banner = "#".repeat(10)
    println("\n$banner $this")
    println("$banner${"#".repeat(this.length + 1)}")
}