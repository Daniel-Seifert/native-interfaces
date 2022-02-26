#ifndef NATIVE_INTERFACES_LIBRARY_H
#define NATIVE_INTERFACES_LIBRARY_H

typedef void(*DelayedGreeting)(char*, char*);

typedef struct Person {
    char *firstname;
    char *lastname;
} Person;

void greet(char *firstname, char *lastname);
void greetAll(Person *persons, int size);
void greetDelayed(const DelayedGreeting pfn);

#endif //NATIVE_INTERFACES_LIBRARY_H
