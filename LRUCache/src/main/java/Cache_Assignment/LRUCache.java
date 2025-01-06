package Cache_Assignment;
/*
 *
 * @author ASSIGNMENT 2024-2025
it2023101_it2023140_it2023024
 *
 */
import java.util.HashMap;

public class LRUCache<K, V> implements Cache<K, V> {
    private final int capacity;  // Μέγεθος του cache
    private final CacheReplacementPolicy policy;  // Πολιτική αντικατάστασης (LRU ή MRU)
    private final HashMap<K, Node> map;  // Χάρτης που κρατά τα δεδομένα του cache
    private final DoublyLinkedList list;  // Διπλά συνδεδεμένη λίστα για παρακολούθηση της τάξης χρήσης
    private int hitCount = 0;  // Μετρητής για hits
    private int missCount = 0;  // Μετρητής για misses

    public LRUCache(int capacity, CacheReplacementPolicy policy) {
        this.capacity = capacity;  // Ορίζουμε το μέγεθος του cache
        this.policy = policy;  // Ορίζουμε την πολιτική αντικατάστασης
        this.map = new HashMap<>();  // Δημιουργούμε τον χάρτη για τα δεδομένα
        this.list = new DoublyLinkedList();  // Δημιουργούμε τη λίστα για την παρακολούθηση της τάξης
    }

    @Override
    public V get(K key) {
        if (capacity == 0) {
            return null;  // Αν το μέγεθος του cache είναι 0, επιστρέφουμε null
        }
        Node node = map.get(key);  // Ψάχνουμε αν υπάρχει το στοιχείο στο cache
        if (node == null) {
            missCount++;  // Αν δεν το βρούμε, αυξάνουμε τα misses
            return null;
        }
        hitCount++;  // Αν το βρούμε, αυξάνουμε τα hits
        list.moveToHead(node);  // Μετακινούμε το στοιχείο στην αρχή της λίστας (πιο πρόσφατο)
        return node.value;  // Επιστρέφουμε την τιμή του στοιχείου
    }

    @Override
    public void put(K key, V value) {
        if (capacity == 0) {
            return;  // Αν το μέγεθος του cache είναι 0, δεν κάνουμε τίποτα
        }

        // Ελέγχουμε αν το στοιχείο υπάρχει ήδη στο cache
        Node node = map.get(key);
        if (node != null) {
            node.value = value;  // Ενημερώνουμε την τιμή του στοιχείου
            list.moveToHead(node);  // Μετακινούμε το στοιχείο στην αρχή της λίστας (ως πιο πρόσφατα χρησιμοποιημένο)
            return;  // Τελειώνουμε εδώ αφού ενημερώσαμε το στοιχείο
        }

        // Αν το cache είναι γεμάτο, πρέπει να αφαιρέσουμε ένα στοιχείο για να κάνουμε χώρο
        if (map.size() == capacity) {
            Node nodeToRemove = null;

            // Επιλέγουμε το στοιχείο προς αφαίρεση ανάλογα με την πολιτική αντικατάστασης
            if (policy == CacheReplacementPolicy.LRU) {
                nodeToRemove = list.removeLast();  // Αφαιρούμε το λιγότερο πρόσφατα χρησιμοποιημένο
            } else if (policy == CacheReplacementPolicy.MRU) {
                nodeToRemove = list.removeFirst();  // Αφαιρούμε το πιο πρόσφατα χρησιμοποιημένο
            }

            // Αφαιρούμε το στοιχείο από τον χάρτη εάν υπάρχει
            if (nodeToRemove != null) {
                map.remove(nodeToRemove.key);
            }
        }

        // Δημιουργούμε έναν νέο κόμβο για το νέο στοιχείο
        Node newNode = new Node(key, value);
        map.put(key, newNode);  // Προσθέτουμε το στοιχείο στον χάρτη
        list.addFirst(newNode);  // Τοποθετούμε το νέο στοιχείο στην αρχή της λίστας
    }

    public int getHitCount() {
        return hitCount;  // Επιστρέφουμε τον αριθμό των hits
    }

    public int getMissCount() {
        return missCount;  // Επιστρέφουμε τον αριθμό των misses
    }

    public int size() {
        return map.size();  // Επιστρέφουμε το μέγεθος του cache
    }

    public void clear() {
        map.clear();  // Καθαρίζουμε τον χάρτη
        list.clear();  // Καθαρίζουμε τη λίστα
    }

    private class Node {
        K key;
        V value;
        Node prev, next;

        Node(K key, V value) {
            this.key = key;  // Ορίζουμε το κλειδί του κόμβου
            this.value = value;  // Ορίζουμε την τιμή του κόμβου
        }
    }

    private class DoublyLinkedList {
        private final Node head;  // Η κεφαλή της λίστας
        private final Node tail;  // Η ουρά της λίστας

        DoublyLinkedList() {
            head = new Node(null, null);  // Δημιουργούμε μια ψεύτικη κεφαλή
            tail = new Node(null, null);  // Δημιουργούμε μια ψεύτικη ουρά
            head.next = tail;  // Σύνδεση κεφαλής και ουράς
            tail.prev = head;  // Σύνδεση ουράς και κεφαλής
        }

        void moveToHead(Node node) {
            remove(node);  // Αφαιρούμε τον κόμβο από τη θέση του
            addFirst(node);  // Τον προσθέτουμε στην αρχή της λίστας
        }

        void remove(Node node) {
            node.prev.next = node.next;  // Αφαιρούμε τον κόμβο από την αλυσίδα
            node.next.prev = node.prev;  // Αφαιρούμε τον κόμβο από την αλυσίδα
        }

        void addFirst(Node node) {
            node.next = head.next;  // Συνδέουμε τον κόμβο με την επόμενη θέση της κεφαλής
            node.next.prev = node;  // Συνδέουμε την επόμενη θέση με τον κόμβο
            head.next = node;  // Συνδέουμε την κεφαλή με τον κόμβο
            node.prev = head;  // Συνδέουμε τον κόμβο με την κεφαλή
        }

        Node removeLast() {
            if (tail.prev == head) {
                return null;  // Αν δεν υπάρχει στοιχείο, επιστρέφουμε null
            }
            Node last = tail.prev;  // Παίρνουμε τον τελευταίο κόμβο
            remove(last);  // Αφαιρούμε τον τελευταίο κόμβο
            return last;  // Επιστρέφουμε τον τελευταίο κόμβο
        }

        Node removeFirst() {
            if (head.next == tail) {
                return null;  // Αν δεν υπάρχει στοιχείο, επιστρέφουμε null
            }
            Node first = head.next;  // Παίρνουμε τον πρώτο κόμβο
            remove(first);  // Αφαιρούμε τον πρώτο κόμβο
            return first;  // Επιστρέφουμε τον πρώτο κόμβο
        }

        void clear() {
            head.next = tail;  // Επαναφέρουμε την αρχική κατάσταση της λίστας
            tail.prev = head;  // Επαναφέρουμε την αρχική κατάσταση της λίστας
        }
    }
}