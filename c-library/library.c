#include "library.h"

#include <stdio.h>
#include <unistd.h>
#include <pthread.h>

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