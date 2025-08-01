# LRUCache & MRUCache & LFUCache

Αυτό το Project υλοποιεί μια κρυφή μνήμη (cache) τύπου LRU (Least Recently Used) μέσα στην οποία υπάρχει και η MRUCache(Most Recently Used) και η LFU(Least Frequency Used).
## Τεχνολογίες
Το Project ειναι φτιαγμένο επάνω σε:

- **Java JDK 21**
- **JUnit 5** για (unit tests).
- **Maven**
  
Η κλάση Simulation υλοποιεί ένα σενάριο χρήσης 80/20:
  • Το 80% των προσπελάσεων αφορά τα 20% των κλειδιών.
  • Το υπόλοιπο 20% των προσπελάσεων αφορά τυχαία κλειδιά.
## Οδηγίες 
Για εκτέλεση διαφορετικών Cache υπάρχουν οδηγίες στην Κλαση __Simulation__
Απλά αλλάζουμε το .LRU στο ανάλογο MRU Ή LFU
## Test Program

Για να τρέξετε το πρόγραμμα, χρησιμοποιήστε τις εξής εντολές του Maven στο Terminal:

  Στο bash
  βεβαιωθείται ότι είστε στο σωστό FilePath και τρέξτε τις παρακάτω εντολές:

1: Καθαρισμός του Project μέσω της εντολής ``mvn clean``

2:Μεταγλώττιση του Project μέσω της εντολής ``mvn compile``

3:Εκτέλεση των δοκιμών μέσω της εντολής ``mvn exec:java``

4:Επαναφορά στην αρχική κατάσταση του Project μέσω της εντολής ``mvn clean``
