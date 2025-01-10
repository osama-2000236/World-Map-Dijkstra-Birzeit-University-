package com.example.project3;


public class Entry<K, V> {
    K key;
    V value;
    Entry<K, V> next; // Reference to the next entry in the same bucket

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
