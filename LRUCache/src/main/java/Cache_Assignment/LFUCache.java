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
import java.util.TreeMap;

public class LFUCache<K, V> implements Cache<K, V> {
    private final int capacity; // Το μέγεθος του cache
    private final CacheReplacementPolicy policy; // Πολιτική αντικατάστασης (LFU)
    private final Map<K, Node> map; // Χάρτης για δεδομένα του cache
    private final TreeMap<Integer, HashSet<K>> frequencyMap; // Συχνότητα ταξινομημένη με TreeMap
    private int minFrequency; // Ελάχιστη συχνότητα

    // Constructor
    public LFUCache(int capacity, CacheReplacementPolicy policy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cache capacity must be greater than zero.");
        }
        this.capacity = capacity;
        this.policy = policy;
        this.map = new HashMap<>();
        this.frequencyMap = new TreeMap<>();
        this.minFrequency = 0;
    }

    @Override
    public V get(K key) {
        if (!map.containsKey(key)) {
            return null; // Miss
        }
        Node node = map.get(key);
        increaseFrequency(node);
        return node.value; // Hit
    }

    @Override
    public void put(K key, V value) {
        if (capacity == 0) {
            return; // Αν το cache έχει μέγεθος 0, δεν αποθηκεύουμε τίποτα
        }

        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value; // Ενημέρωση τιμής
            increaseFrequency(node);
        } else {
            if (map.size() == capacity) {
                evictLFU(); // Διαγραφή του λιγότερο συχνού στοιχείου
            }
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            frequencyMap.computeIfAbsent(1, k -> new HashSet<>()).add(key);
            minFrequency = 1; // Η νέα ελάχιστη συχνότητα είναι 1
        }
    }

    private void increaseFrequency(Node node) {
        int freq = node.frequency;
        frequencyMap.get(freq).remove(node.key); // Αφαίρεση από την παλιά συχνότητα
        if (frequencyMap.get(freq).isEmpty()) {
            frequencyMap.remove(freq); // Αν είναι κενή, αφαιρείται η καταχώρηση
            if (freq == minFrequency) {
                minFrequency++; // Ενημέρωση της ελάχιστης συχνότητας
            }
        }

        node.frequency++; // Αύξηση συχνότητας
        frequencyMap.computeIfAbsent(node.frequency, k -> new HashSet<>()).add(node.key); // Προσθήκη στη νέα συχνότητα
    }

    private void evictLFU() {
        if (policy != CacheReplacementPolicy.LFU) {
            throw new UnsupportedOperationException("This cache only supports LFU policy.");
        }

        // Βρίσκουμε το στοιχείο με τη χαμηλότερη συχνότητα
        K keyToRemove = frequencyMap.get(minFrequency).iterator().next();
        frequencyMap.get(minFrequency).remove(keyToRemove);
        if (frequencyMap.get(minFrequency).isEmpty()) {
            frequencyMap.remove(minFrequency);
        }
        map.remove(keyToRemove); // Αφαίρεση από τον κύριο χάρτη
    }

    private class Node {
        K key;
        V value;
        int frequency;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.frequency = 1; // Ξεκινάμε με συχνότητα 1
        }
    }
}
