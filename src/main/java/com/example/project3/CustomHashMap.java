package com.example.project3;

import java.util.Objects;



public class CustomHashMap<K, V> {
    private Entry<K, V>[] buckets;
    private int capacity = 16; // Initial capacity
    private int size = 0;
    private final double loadFactor = 0.75;

    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        buckets = new Entry[capacity];
    }


    private int getHash(K key) {
        return Objects.hashCode(key) & (capacity - 1);
    }


    public void put(K key, V value) {
        int hash = getHash(key);
        Entry<K, V> existing = buckets[hash];

        for (; existing != null; existing = existing.next) {
            if (existing.key.equals(key)) {
                existing.value = value; // Update existing value
                return;
            }
        }

        Entry<K, V> entry = new Entry<>(key, value);
        entry.next = buckets[hash];
        buckets[hash] = entry;
        size++;

        // Check load factor
        if ((1.0 * size) / capacity >= loadFactor) {
            resize();
        }
    }


    public V get(K key) {
        int hash = getHash(key);
        Entry<K, V> existing = buckets[hash];

        while (existing != null) {
            if (existing.key.equals(key)) {
                return existing.value;
            }
            existing = existing.next;
        }

        return null; // Not found
    }


    public V remove(K key) {
        int hash = getHash(key);
        Entry<K, V> existing = buckets[hash];
        Entry<K, V> prev = null;

        while (existing != null) {
            if (existing.key.equals(key)) {
                if (prev != null) {
                    prev.next = existing.next;
                } else {
                    buckets[hash] = existing.next;
                }
                size--;
                return existing.value;
            }
            prev = existing;
            existing = existing.next;
        }

        return null; // Not found
    }


    public int size() {
        return size;
    }


    public boolean isEmpty() {
        return size == 0;
    }


    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        Entry<K, V>[] newBuckets = new Entry[newCapacity];

        // Rehash all existing entries
        for (int i = 0; i < capacity; i++) {
            Entry<K, V> existing = buckets[i];
            while (existing != null) {
                Entry<K, V> next = existing.next;
                int hash = Objects.hashCode(existing.key) & (newCapacity - 1);
                existing.next = newBuckets[hash];
                newBuckets[hash] = existing;
                existing = next;
            }
        }

        capacity = newCapacity;
        buckets = newBuckets;
    }


    @SuppressWarnings("unchecked")
    public void clear() {
        buckets = new Entry[capacity];
        size = 0;
    }
}
