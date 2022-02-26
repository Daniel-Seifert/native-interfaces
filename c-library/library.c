#include "library.h"

#include <stdio.h>
#include <unistd.h>

int msleep(unsigned int tms);

void greet(char *firstname, char *lastname) {
    printf("Hello: %s %s\n", firstname, lastname);
}

void greetDelayed(const DelayedGreeting pfn) {
    msleep(1000);
    char firstname[] = "Parker";
    char lastname[] = "Peter";

    (*pfn)((char *) &firstname, (char *) &lastname);
}

int msleep(unsigned int tms) {
    return usleep(tms * 1000);
}