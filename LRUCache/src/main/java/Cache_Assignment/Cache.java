package Cache_Assignment;
/*
 *
 * @author ASSIGNMENT 2024-2025
it2023101_it2023140_it2023024
 *
 */
//Το InterFace που είχε δωθεί στο Eclass για το πρώτο μέρος.
public interface Cache<K, V> {
    /**
     * Get the value for a key. Returns null if the key is not in the cache.
     * @param key the key
     * @return the value for the key, or null if not found
     */
    V get(K key);

    /**
     * Put a new key-value pair in the cache.
     * @param key the key
     * @param value the value
     */
    void put(K key, V value);
}