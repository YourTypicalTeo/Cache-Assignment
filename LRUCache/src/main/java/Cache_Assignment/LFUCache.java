package Cache_Assignment;
/*
 *
 * @author ASSIGNMENT 2024-2025
it2023101_it2023140_it2023024
 *
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LFUCache<K, V> implements Cache<K, V> {
    private final int capacity; // Το μέγεθος του cache
    private final CacheReplacementPolicy policy; // Πολιτική αντικατάστασης (LFU)
    private final Map<K, Node> map; // Χάρτης για την αποθήκευση δεδομένων του cache
    private final Map<Integer, HashSet<K>> frequencyMap; // Χάρτης για την παρακολούθηση της συχνότητας χρήσης
    private int minFrequency; // Η ελάχιστη συχνότητα μέσα στο cache

    public LFUCache(int capacity, CacheReplacementPolicy policy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Το μέγεθος του cache πρέπει να είναι μεγαλύτερο από το μηδέν.");
        }
        this.capacity = capacity; // Ορίζουμε το μέγεθος του cache
        this.policy = policy; // Ορίζουμε την πολιτική αντικατάστασης
        this.map = new HashMap<>(); // Αρχικοποιούμε τον χάρτη για τα δεδομένα του cache
        this.frequencyMap = new HashMap<>(); // Αρχικοποιούμε τον χάρτη συχνοτήτων
        this.minFrequency = 0; // Ορίζουμε την αρχική ελάχιστη συχνότητα σε 0
    }

    @Override
    public V get(K key) {
        if (!map.containsKey(key)) {
            return null; // Αν το κλειδί δεν υπάρχει, επιστρέφουμε null
        }
        Node node = map.get(key); // Παίρνουμε τον κόμβο από τον χάρτη
        increaseFrequency(node); // Αυξάνουμε τη συχνότητα χρήσης
        return node.value; // Επιστρέφουμε την τιμή του κόμβου
    }

    @Override
    public void put(K key, V value) {
        if (capacity == 0) {
            return; // Αν το μέγεθος του cache είναι 0, δεν κάνουμε τίποτα
        }
        if (map.containsKey(key)) {
            Node node = map.get(key); // Παίρνουμε τον κόμβο από τον χάρτη
            node.value = value; // Ενημερώνουμε την τιμή του κόμβου
            increaseFrequency(node); // Αυξάνουμε τη συχνότητα χρήσης
        } else {
            if (map.size() == capacity) {
                evictLFU(); // Αφαιρούμε το στοιχείο με τη χαμηλότερη συχνότητα
            }
            Node newNode = new Node(key, value); // Δημιουργούμε νέο κόμβο
            map.put(key, newNode); // Προσθέτουμε το νέο κόμβο στον χάρτη
            frequencyMap.computeIfAbsent(1, k -> new HashSet<>()).add(key); // Ενημερώνουμε τη συχνότητα χρήσης
            minFrequency = 1; // Επαναφέρουμε την ελάχιστη συχνότητα στο 1
        }
    }

    private void increaseFrequency(Node node) {
        int freq = node.frequency; // Παίρνουμε τη συχνότητα του κόμβου
        frequencyMap.get(freq).remove(node.key); // Αφαιρούμε το κλειδί από τη συχνότητα
        if (frequencyMap.get(freq).isEmpty()) {
            frequencyMap.remove(freq); // Αν δεν υπάρχουν άλλα στοιχεία στη συχνότητα, την αφαιρούμε
            if (freq == minFrequency) {
                minFrequency++; // Αυξάνουμε την ελάχιστη συχνότητα αν χρειάζεται
            }
        }
        node.frequency++; // Αυξάνουμε τη συχνότητα του κόμβου
        frequencyMap.computeIfAbsent(node.frequency, k -> new HashSet<>()).add(node.key); // Ενημερώνουμε τον χάρτη συχνοτήτων
    }

    private void evictLFU() {
        if (policy != CacheReplacementPolicy.LFU) {
            throw new UnsupportedOperationException("Αυτό το cache υποστηρίζει μόνο πολιτική LFU.");
        }
        // Παίρνουμε το πρώτο στοιχείο με τη μικρότερη συχνότητα
        K keyToRemove = frequencyMap.get(minFrequency).iterator().next();
        frequencyMap.get(minFrequency).remove(keyToRemove); // Αφαιρούμε το κλειδί από τον χάρτη συχνοτήτων
        if (frequencyMap.get(minFrequency).isEmpty()) {
            frequencyMap.remove(minFrequency); // Αν η συχνότητα είναι κενή, την αφαιρούμε
        }
        map.remove(keyToRemove); // Αφαιρούμε το στοιχείο από τον χάρτη
    }

    private class Node {
        K key; // Το κλειδί του κόμβου
        V value; // Η τιμή του κόμβου
        int frequency; // Η συχνότητα χρήσης του κόμβου

        Node(K key, V value) {
            this.key = key; // Ορίζουμε το κλειδί
            this.value = value; // Ορίζουμε την τιμή
            this.frequency = 1; // Αρχική συχνότητα είναι 1
        }
    }
}
