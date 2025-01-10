package com.example.project3;

import java.util.*;
import java.util.function.Predicate;



public class ClassicSLL<T> implements Iterable<T> {

    private SLLNode<T> first;
    private int size;

    public ClassicSLL() {
        this.first = null;
        this.size = 0;
    }

    public void addFirst(T element) {
        SLLNode<T> newNode = new SLLNode<>(element);
        newNode.setNext(first);
        first = newNode;
        size++;
    }

    public void addLast(T element) {
        SLLNode<T> newNode = new SLLNode<>(element);
        if (first == null) {
            first = newNode;
        } else {
            SLLNode<T> current = first;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }
        size++;
    }

    public T removeFirst() {
        if (first == null) return null;
        T elem = first.getElement();
        first = first.getNext();
        size--;
        return elem;
    }

    public T removeLast() {
        if (first == null) return null;
        if (first.getNext() == null) {
            T elem = first.getElement();
            first = null;
            size--;
            return elem;
        }
        SLLNode<T> current = first;
        while (current.getNext().getNext() != null) {
            current = current.getNext();
        }
        T elem = current.getNext().getElement();
        current.setNext(null);
        size--;
        return elem;
    }

    public T find(Predicate<T> condition) {
        SLLNode<T> current = first;
        while (current != null) {
            if (condition.test(current.getElement())) {
                return current.getElement();
            }
            current = current.getNext();
        }
        return null;
    }

    public T getFirst() {
        return first == null ? null : first.getElement();
    }

    public T getLast() {
        if (first == null) return null;
        SLLNode<T> current = first;
        while (current.getNext() != null) {
            current = current.getNext();
        }
        return current.getElement();
    }

    public boolean isEmpty() {
        return first == null;
    }

    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private SLLNode<T> current = first;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) throw new NoSuchElementException();
                T val = current.getElement();
                current = current.getNext();
                return val;
            }
        };
    }

    public List<T> getAllElements() {
        List<T> result = new ArrayList<>();
        SLLNode<T> current = first;
        while (current != null) {
            result.add(current.getElement());
            current = current.getNext();
        }
        return result;
    }

    @Override
    public String toString() {
        if (first == null) return "Empty List";
        StringBuilder sb = new StringBuilder();
        SLLNode<T> cur = first;
        while (cur != null) {
            sb.append(cur.getElement()).append(" -> ");
            cur = cur.getNext();
        }
        sb.setLength(sb.length() - 4); // remove last arrow
        return sb.toString();
    }
}
