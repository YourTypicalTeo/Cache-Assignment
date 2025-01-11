package Cache_Assignment;
/*
 *
 * @author ASSIGNMENT 2024-2025
it2023101_it2023140_it2023024
 *
 */
/**
 * Enum που ορίζει τις στρατηγικές αντικατάστασης στην κρυφή μνήμη.
 */
public enum CacheReplacementPolicy {
LRU("Least Recently Used"),
MRU("Most Recently Used"),
LFU("Least Frequently Used");
private final String description;

CacheReplacementPolicy(String description) {
this.description = description;
 }
public String getDescription() {
return description;
 }
@Override
public String toString() {
return description;
 }
} 
