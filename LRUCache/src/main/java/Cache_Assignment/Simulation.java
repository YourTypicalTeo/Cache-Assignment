package Cache_Assignment;

/*
 *
 * @author ASSIGNMENT 2024-2025
 * it2023101_it2023140_it2023024
 *
 */

import java.util.Random;

public class Simulation {
    public static void main(String[] args) {
        int capacity = 25;  // Ορίζουμε το μέγεθος του cache
        int totalOperations = 100000;  // Ορίζουμε τον συνολικό αριθμό των λειτουργιών που θα εκτελέσουμε

        // Επιλέγουμε την πολιτική αντικατάστασης (LRU, MRU, LFU)
        CacheReplacementPolicy policy = CacheReplacementPolicy.LFU;

        // Δημιουργούμε το cache ανάλογα με την επιλεγμένη πολιτική αντικατάστασης
        LFUCache<Integer, String> cache = new LFUCache<>(capacity, policy);

        Random random = new Random();  // Δημιουργούμε αντικείμενο Random για την παραγωγή τυχαίων αριθμών

        // Σενάριο 80/20: Το 80% των προσπελάσεων αφορά το 20% των κλειδιών
        int[] frequentKeys = new int[20];  // Τα 20 πιο συχνά χρησιμοποιούμενα κλειδιά
        for (int i = 0; i < frequentKeys.length; i++) {
            frequentKeys[i] = random.nextInt(100);  // Επιλέγουμε τυχαία κλειδιά για το 20%
        }

        // Εκτελούμε τις λειτουργίες του cache (ανάκτηση και αποθήκευση στοιχείων)
        int actualOperations = 0;
        for (int i = 0; i < totalOperations; i++) {
            actualOperations++;
            int key;
            if (random.nextDouble() < 0.8) {
                // Στο 80% των περιπτώσεων, χρησιμοποιούμε ένα από τα συχνά κλειδιά
                key = frequentKeys[random.nextInt(frequentKeys.length)];
            } else {
                // Στο υπόλοιπο 20%, επιλέγουμε ένα τυχαίο κλειδί
                key = random.nextInt(100);
            }

            if (cache.get(key) == null) {
                cache.put(key, "Value" + key);  // Προσθέτουμε το νέο στοιχείο στο cache
            }
        }

        // Λαμβάνουμε τις μετρήσεις του cache (hits και misses)
        int hits = cache.getHitCount();  // Πόσες φορές βρήκαμε το στοιχείο στο cache
        int misses = cache.getMissCount();  // Πόσες φορές το στοιχείο δεν βρέθηκε και το προσθέσαμε
        double hitRate = (hits * 100.0) / actualOperations;  // Υπολογίζουμε το ποσοστό των hits
        double missRate = (misses * 100.0) / actualOperations;  // Υπολογίζουμε το ποσοστό των misses

        // Εκτυπώνουμε τα αποτελέσματα
        System.out.println("Total operations executed: " + actualOperations);
        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.printf("Hit Rate: %.2f%%\n", hitRate);
        System.out.printf("Miss Rate: %.2f%%\n", missRate);
    }
}