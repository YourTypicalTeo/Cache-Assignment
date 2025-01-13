package Cache_Assignment;
/*
 *
 * @author ASSIGNMENT 2024-2025
 * it2023101_it2023140_it2023024
 *
 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class LFUCache<K, V> implements Cache<K, V> {
    private final int capacity; // Χωρητικότητα της προσωρινής μνήμης
    private final Map<K, Node> cache; // Χάρτης για αποθήκευση στοιχείων
    private final Map<Integer, Queue<K>> frequencyMap; // Χάρτης για συχνότητα
    private int minFrequency; // Ελάχιστη συχνότητα
    private int hitCount = 0; // Μετρητής hits
    private int missCount = 0; // Μετρητής misses

    public LFUCache(int capacity, CacheReplacementPolicy policy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Η χωρητικότητα πρέπει να είναι μεγαλύτερη από το μηδέν.");
        }
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.frequencyMap = new HashMap<>();
        this.minFrequency = 0;
    }

    @Override
    public V get(K key) {
        if (!cache.containsKey(key)) {
            missCount++;
            return null;
        }
        Node node = cache.get(key);
        increaseFrequency(node);
        hitCount++;
        return node.value;
    }

    @Override
    public void put(K key, V value) {
        if (capacity == 0) {
            return;
        }

        // If key already exists, update the value and increase frequency
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.value = value;
            increaseFrequency(node);
            return;
        }

        // If cache is full, evict the least frequently used item
        if (cache.size() == capacity) {
            evictLFU();
        }

        // Insert the new key-value pair
        Node newNode = new Node(key, value);
        cache.put(key, newNode);
        frequencyMap.computeIfAbsent(1, k -> new LinkedList<>()).add(key);
        minFrequency = 1;
        missCount++;
    }

    private void increaseFrequency(Node node) {
        int currentFrequency = node.frequency; // Αποθηκεύουμε την τρέχουσα συχνότητα του κόμβου
        Queue<K> currentQueue = frequencyMap.get(currentFrequency); // Παίρνουμε την ουρά για την τρέχουσα συχνότητα
        // Αφαιρούμε το κλειδί από την τρέχουσα ουρά συχνότητας
        currentQueue.remove(node.key);
        // Αν η ουρά της τρέχουσας συχνότητας είναι άδεια, την αφαιρούμε από το χάρτη
        if (currentQueue.isEmpty()) {
            frequencyMap.remove(currentFrequency);
            // Αν αφαιρέσαμε την τελευταία καταχώριση από τη συχνότητα, αυξάνουμε την ελάχιστη συχνότητα
            if (currentFrequency == minFrequency) {
                minFrequency++;
            }
        }
        // Αυξάνουμε τη συχνότητα του κόμβου και τον προσθέτουμε στην ουρά της νέας συχνότητας
        node.frequency++;
        frequencyMap.computeIfAbsent(node.frequency, k -> new LinkedList<>()).add(node.key);
    }
    private void evictLFU() {
        // Παίρνουμε την ουρά για την ελάχιστη συχνότητα
        Queue<K> minFreqQueue = frequencyMap.get(minFrequency);
        // Αποβάλλουμε το πρώτο στοιχείο από την ουρά (το στοιχείο με την ελάχιστη συχνότητα)
        K keyToEvict = minFreqQueue.poll(); // Παίρνουμε το πρώτο στοιχείο στη συχνότητα
        // Αν η ουρά της ελάχιστης συχνότητας είναι άδεια, την αφαιρούμε από το χάρτη
        if (minFreqQueue.isEmpty()) {
            frequencyMap.remove(minFrequency);
        }
        // Αφαιρούμε το κλειδί από το cache
        cache.remove(keyToEvict);
    }

    public int getHitCount() {
        return hitCount;
    }

    public int getMissCount() {
        return missCount;
    }

    private class Node {
        K key;
        V value;
        int frequency;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.frequency = 1; // Αρχική συχνότητα 1
        }
    }
}