#include "library.h"

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <string.h>

void greet(char *firstname, char *lastname) {
    printf("Hello: %s %s\n", firstname, lastname);
    fflush(stdout);
}

void greetAll(Person *persons, int size){
    int i;
    for (i=0; i < size; i++) {
        greet(persons[i].firstname, persons[i].lastname);
    }
}

void greetingCallback(const DelayedGreeting pfn) {
    sleep(1);
    char firstname[] = "Black";
    char lastname[] = "Panther";
    (*pfn)((char *) &firstname, (char *) &lastname);
}

void greetDelayed(const DelayedGreeting pfn) {
    pthread_t thread1;
    pthread_create(&thread1, NULL, (void *(*)(void *)) greetingCallback, (void*) pfn);
    pthread_detach(thread1);
}

void getPersonsToGreet(Person **persons, int *size) {
    *size = 3;
    *persons = (Person*) malloc(sizeof(Person) * 3);
    memset(*persons, 0, sizeof(Person) * 3);
    (*persons)[0].firstname = "Bruce";
    (*persons)[0].lastname = "Banner";
    (*persons)[1].firstname = "Doctor";
    (*persons)[1].lastname = "Strange";
    (*persons)[2].firstname = "Captain";
    (*persons)[2].lastname = "America";
}

void freePersonsToGreet(Person *personsToFree) {
    free(personsToFree);
}