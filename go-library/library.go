package main

/*
#include <stdlib.h>

typedef void(*DelayedGreeting)(char*, char*);
void bridge_callback(DelayedGreeting dg, char* firstname, char* lastname);

typedef struct Person {
    char *firstname;
    char *lastname;
} Person;
*/
import "C"
import (
	"fmt"
	"time"
	"unsafe"
)

type Person struct {
	firstname string
	lastname  string
}

func main() {}

//export greet
func greet(firstname *C.char, lastname *C.char) {
	fmt.Printf("%s %s", C.GoString(firstname), C.GoString(lastname))
}

//export greetAll
func greetAll(persons *C.Person, size C.int) {
	// Copy c array to native slice
	var personSlice []Person
	var x *C.Person = persons
	for _, p := range unsafe.Slice(x, int(size)) {
		personSlice = append(personSlice, Person{
			firstname: C.GoString(p.firstname),
			lastname:  C.GoString(p.lastname),
		})
	}

	// Print slice
	for _, person := range personSlice {
		fmt.Printf("\nGeetings to: %s %s", person.firstname, person.lastname)
	}
}

//export greetDelayed
func greetDelayed(callback C.DelayedGreeting) {
	dg := func(firstname string, lastname string) {
		// Call bridge
		first := C.CString(firstname)
		last := C.CString(lastname)
		C.bridge_callback(callback, first, last)

		// Free resources
		C.free(unsafe.Pointer(first))
		C.free(unsafe.Pointer(last))
	}

	go func() {
		<-time.After(time.Second)
		dg("Black", "Panther")
	}()
}

//export getPersonsToGreet
func getPersonsToGreet(persons **C.Person, size *C.int) {
	type CPerson struct {
		firstname *C.char
		lastname  *C.char
	}
	p := []*CPerson{
		{firstname: C.CString("Bruce"), lastname: C.CString("Banner")},
		{firstname: C.CString("Doctor"), lastname: C.CString("Strange")},
		{firstname: C.CString("Captain"), lastname: C.CString("America")},
	}

	*size = C.int(3)
	*persons = (*C.Person)(p[0])
}

//export freePersonsToGreet
func freePersonsToGreet(persons *C.Person) {
	//C.free(unsafe.Pointer(persons))
}
