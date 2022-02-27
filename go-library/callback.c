typedef void(*DelayedGreeting)(char*, char*);

void bridge_callback(DelayedGreeting dg, char* firstname, char* lastname) {
    dg(firstname, lastname);
}