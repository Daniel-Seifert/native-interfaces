#ifndef NATIVE_INTERFACES_LIBRARY_H
#define NATIVE_INTERFACES_LIBRARY_H

typedef void(*DelayedGreeting)(char*, char*);

void greet(char *firstname, char *lastname);
void greetDelayed(const DelayedGreeting pfn);

#endif //NATIVE_INTERFACES_LIBRARY_H
