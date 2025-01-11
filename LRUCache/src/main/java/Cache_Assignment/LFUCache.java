package Cache_Assignment;

import java.util.*;

public class LFUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, Node> map; // Map για τα δεδομένα του cache
    private final TreeMap<Integer, LinkedHashSet<K>> frequencyMap; // Διαχείριση μετρητών συχνοτήτων
    private int minFrequency; // Ελάχιστη συχνότητα

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.frequencyMap = new TreeMap<>();
        this.minFrequency = 0;
    }

    @Override
    public V get(K key) {
        if (!map.containsKey(key)) {
            return null;
        }
        Node node = map.get(key);
        increaseFrequency(node);
        return node.value;
    }

    @Override
    public void put(K key, V value) {
        if (capacity == 0) {
            return;
        }
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            increaseFrequency(node);
        } else {
            if (map.size() == capacity) {
                evictLFU();
            }
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            frequencyMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
            minFrequency = 1;
        }
    }

    private void increaseFrequency(Node node) {
        int freq = node.frequency;
        frequencyMap.get(freq).remove(node.key);
        if (frequencyMap.get(freq).isEmpty()) {
            frequencyMap.remove(freq);
            if (freq == minFrequency) {
                minFrequency++;
            }
        }
        node.frequency++;
        frequencyMap.computeIfAbsent(node.frequency, k -> new LinkedHashSet<>()).add(node.key);
    }

    private void evictLFU() {
        K keyToRemove = frequencyMap.get(minFrequency).iterator().next();
        frequencyMap.get(minFrequency).remove(keyToRemove);
        if (frequencyMap.get(minFrequency).isEmpty()) {
            frequencyMap.remove(minFrequency);
        }
        map.remove(keyToRemove);
    }

    private class Node {
        K key;
        V value;
        int frequency;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.frequency = 1;
        }
    }

    public static void main(String[] args) {
        LFUCache<Integer, String> cache = new LFUCache<>(3);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        System.out.println(cache.get(1)); // ΘΑ ΕΠΡΕΠΕ ΝΑ ΤΥΠΩΣΕΙ "one"
        cache.put(4, "four"); // Evicts key 2

        System.out.println(cache.get(2)); // Should print null
        System.out.println(cache.get(3)); // Should print "three"
        System.out.println(cache.get(4)); // Should print "four"
    }
}
