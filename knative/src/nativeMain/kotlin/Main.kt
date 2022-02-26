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
        /*nativelib.getPersonsToGreet()
        receiveStructArrayFromC(library).forEach {
            println("Received person: ${it.firstname}, ${it.lastname}")
        }*/

        // Sleep to assure callback is invoked
        sleep(2)
    }
}

fun String.printBanner() {
    val banner = "#".repeat(10)
    println("\n$banner $this")
    println("$banner${"#".repeat(this.length + 1)}")
}