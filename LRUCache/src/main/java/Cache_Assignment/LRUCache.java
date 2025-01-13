package Cache_Assignment;
/*
 *
 * @author ASSIGNMENT 2024-2025
it2023101_it2023140_it2023024
 *
 */
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LRUCache<K, V> implements Cache<K, V> {
    private final int capacity;  // Το μέγεθος του cache
    private final CacheReplacementPolicy policy;  // Πολιτική αντικατάστασης (LRU, MRU, LFU)
    private final HashMap<K, Node> map;  // Ο χάρτης που αποθηκεύει τα δεδομένα του cache
    private final DoublyLinkedList list;  // Διπλά συνδεδεμένη λίστα για την παρακολούθηση της τάξης (LRU και MRU)
    private final TreeMap<Integer, DoublyLinkedList> frequencyMap;  // Χάρτης συχνοτήτων για την πολιτική LFU
    private int hitCount = 0;  // Μετρητής για τα cache hits
    private int missCount = 0;  // Μετρητής για τα cache misses

    public LRUCache(int capacity, CacheReplacementPolicy policy) {
        this.capacity = capacity;  // Ορίζουμε το μέγεθος του cache
        this.policy = policy;  // Ορίζουμε την πολιτική αντικατάστασης
        this.map = new HashMap<>();  // Δημιουργούμε τον χάρτη για τα δεδομένα του cache
        this.list = new DoublyLinkedList();  // Δημιουργούμε την διπλά συνδεδεμένη λίστα για LRU και MRU
        this.frequencyMap = new TreeMap<>();  // Δημιουργούμε τον χάρτη συχνοτήτων για LFU
    }

    @Override
    public V get(K key) {
        if (capacity == 0) {
            return null;  // Επιστρέφουμε null αν το μέγεθος του cache είναι 0
        }
        Node node = map.get(key);  // Ψάχνουμε για το στοιχείο στο cache
        if (node == null) {
            missCount++;  // Αν δεν βρούμε το στοιχείο, αυξάνουμε τα misses
            return null;
        }
        hitCount++;  // Αν το βρούμε, αυξάνουμε τα hits

        // Ενημέρωση του στοιχείου ανάλογα με την πολιτική αντικατάστασης
        if (policy == CacheReplacementPolicy.LRU) {
            list.moveToHead(node);  // Μετακίνηση του στοιχείου στην αρχή για LRU
        } else if (policy == CacheReplacementPolicy.MRU) {
            list.moveToTail(node);  // Μετακίνηση του στοιχείου στο τέλος για MRU
        } else if (policy == CacheReplacementPolicy.LFU) {
            increaseFrequency(node);  // Αύξηση της συχνότητας για LFU
        }

        return node.value;  // Επιστρέφουμε την τιμή του στοιχείου
    }

    @Override
    public void put(K key, V value) {
        if (capacity == 0) {
            return;  // Δεν κάνουμε τίποτα αν το μέγεθος του cache είναι 0
        }

        // Ελέγχουμε αν το στοιχείο υπάρχει ήδη στο cache
        Node node = map.get(key);
        if (node != null) {
            node.value = value;  // Ενημερώνουμε την τιμή του στοιχείου
            if (policy == CacheReplacementPolicy.LRU) {
                list.moveToHead(node);  // Μετακίνηση του στοιχείου στην αρχή για LRU
            } else if (policy == CacheReplacementPolicy.MRU) {
                list.moveToTail(node);  // Μετακίνηση του στοιχείου στο τέλος για MRU
            } else if (policy == CacheReplacementPolicy.LFU) {
                increaseFrequency(node);  // Αύξηση της συχνότητας για LFU
            }
            return;  // Τελειώνουμε αν το στοιχείο υπάρχει ήδη στο cache
        }

        // Αν το cache είναι γεμάτο, πρέπει να αφαιρέσουμε το στοιχείο με βάση την πολιτική αντικατάστασης
        if (map.size() == capacity) {
            Node nodeToRemove = null;
            if (policy == CacheReplacementPolicy.LRU) {
                nodeToRemove = list.removeLast();  // Αφαιρούμε το λιγότερο πρόσφατα χρησιμοποιημένο για LRU
            } else if (policy == CacheReplacementPolicy.MRU) {
                nodeToRemove = list.removeFirst();  // Αφαιρούμε το πιο πρόσφατα χρησιμοποιημένο για MRU
            } else if (policy == CacheReplacementPolicy.LFU) {
                nodeToRemove = removeLFU();  // Αφαιρούμε το λιγότερο συχνά χρησιμοποιημένο για LFU
            }
            if (nodeToRemove != null) {
                map.remove(nodeToRemove.key);  // Αφαιρούμε το στοιχείο από τον χάρτη
            }
        }

        // Δημιουργούμε έναν νέο κόμβο για το νέο στοιχείο
        Node newNode = new Node(key, value);
        map.put(key, newNode);  // Προσθέτουμε το νέο στοιχείο στον χάρτη

        // Προσθέτουμε το νέο στοιχείο στην αρχή ή στο τέλος ανάλογα με την πολιτική
        if (policy == CacheReplacementPolicy.LRU || policy == CacheReplacementPolicy.MRU) {
            list.addFirst(newNode);  // Προσθήκη στην αρχή ή στο τέλος για LRU/MRU
        } else if (policy == CacheReplacementPolicy.LFU) {
            addToFrequencyList(newNode, 1);  // Προσθήκη του νέου στοιχείου με συχνότητα 1 για LFU
        }
    }

    private void increaseFrequency(Node node) {
        // Αυξάνουμε τη συχνότητα και ενημερώνουμε τη θέση του στοιχείου
        DoublyLinkedList list = frequencyMap.get(node.frequency);
        list.remove(node);  // Αφαιρούμε το στοιχείο από τη λίστα της τρέχουσας συχνότητας

        if (list.size() == 0) {
            frequencyMap.remove(node.frequency);  // Αφαιρούμε την καταχώρηση για τη συχνότητα αν η λίστα είναι άδεια
        }

        node.frequency++;  // Αυξάνουμε τη συχνότητα του στοιχείου
        addToFrequencyList(node, node.frequency);  // Προσθέτουμε το στοιχείο στην νέα λίστα με την αυξημένη συχνότητα
    }

    private void addToFrequencyList(Node node, int frequency) {
        frequencyMap.putIfAbsent(frequency, new DoublyLinkedList());
        frequencyMap.get(frequency).addFirst(node);  // Προσθήκη του στοιχείου στην λίστα της συχνότητας
    }

    private Node removeLFU() {
        // Αφαιρούμε το λιγότερο συχνά χρησιμοποιημένο στοιχείο από το cache
        Map.Entry<Integer, DoublyLinkedList> entry = frequencyMap.firstEntry();
        DoublyLinkedList list = entry.getValue();
        Node nodeToRemove = list.removeLast();  // Αφαιρούμε το τελευταίο στοιχείο από την λίστα της συχνότητας

        if (list.size() == 0) {
            frequencyMap.remove(entry.getKey());  // Αφαιρούμε την καταχώρηση για τη συχνότητα αν η λίστα είναι άδεια
        }

        return nodeToRemove;  // Επιστρέφουμε το στοιχείο που αφαιρέσαμε
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
        list.clear();  // Καθαρίζουμε την λίστα LRU και MRU
        frequencyMap.clear();  // Καθαρίζουμε τον χάρτη συχνοτήτων για LFU
    }

    private class Node {
        K key;
        V value;
        int frequency;  // Συχνότητα χρήσης για την πολιτική LFU
        Node prev, next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.frequency = 1;  // Η συχνότητα αρχικά είναι 1
        }
    }

    private class DoublyLinkedList {
        private final Node head;
        private final Node tail;

        DoublyLinkedList() {
            head = new Node(null, null);  // Δημιουργούμε την ψεύτικη κεφαλή
            tail = new Node(null, null);  // Δημιουργούμε την ψεύτικη ουρά
            head.next = tail;  // Συνδέουμε την κεφαλή με την ουρά
            tail.prev = head;  // Συνδέουμε την ουρά με την κεφαλή
        }

        void moveToHead(Node node) {
            remove(node);  // Αφαιρούμε τον κόμβο από την τρέχουσα θέση
            addFirst(node);  // Προσθέτουμε τον κόμβο στην αρχή
        }

        void remove(Node node) {
            node.prev.next = node.next;  // Αφαιρούμε τον κόμβο από την λίστα
            node.next.prev = node.prev;  // Αφαιρούμε τον κόμβο από την λίστα
        }

        void addFirst(Node node) {
            node.next = head.next;  // Συνδέουμε τον κόμβο με τον επόμενο κόμβο
            node.next.prev = node;  // Συνδέουμε τον επόμενο κόμβο με τον κόμβο
            head.next = node;  // Συνδέουμε την κεφαλή με τον κόμβο
            node.prev = head;  // Συνδέουμε τον κόμβο με την κεφαλή
        }

        Node removeLast() {
            if (tail.prev == head) {
                return null;  // Αν η λίστα είναι άδεια, επιστρέφουμε null
            }
            Node last = tail.prev;  // Παίρνουμε τον τελευταίο κόμβο
            remove(last);  // Αφαιρούμε τον τελευταίο κόμβο
            return last;  // Επιστρέφουμε τον τελευταίο κόμβο
        }

        Node removeFirst() {
            if (head.next == tail) {
                return null;  // Αν η λίστα είναι άδεια, επιστρέφουμε null
            }
            Node first = head.next;  // Παίρνουμε τον πρώτο κόμβο
            remove(first);  // Αφαιρούμε τον πρώτο κόμβο
            return first;  // Επιστρέφουμε τον πρώτο κόμβο
        }

        void clear() {
            head.next = tail;  // Επαναφέρουμε την λίστα στην αρχική της κατάσταση
            tail.prev = head;  // Επαναφέρουμε την λίστα στην αρχική της κατάσταση
        }

        int size() {
            int size = 0;
            Node current = head.next;
            while (current != tail) {
                size++;
                current = current.next;
            }
            return size;  // Επιστρέφουμε το μέγεθος της λίστας
        }

        // Μέθοδος για την μετακίνηση ενός κόμβου στο τέλος της λίστας
        void moveToTail(Node node) {
            remove(node);  // Αφαιρούμε τον κόμβο από την τρέχουσα θέση του
            addLast(node);  // Προσθέτουμε τον κόμβο στο τέλος της λίστας
        }

        // Μέθοδος για την προσθήκη ενός κόμβου στο τέλος της λίστας
        void addLast(Node node) {
            node.prev = tail.prev;  // Συνδέουμε τον κόμβο με τον προηγούμενο του τελευταίου κόμβου
            node.next = tail;  // Συνδέουμε τον κόμβο με την ουρά
            tail.prev.next = node;  // Συνδέουμε τον προηγούμενο του τελευταίου κόμβου με τον κόμβο
            tail.prev = node;  // Συνδέουμε την ουρά με τον κόμβο
        }
    }
}