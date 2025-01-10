package com.example.project3;


public class SLLNode<T> {
    private T element;
    private SLLNode<T> next;

    public SLLNode(T element) {
        this.element = element;
        this.next = null;
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    public SLLNode<T> getNext() {
        return next;
    }

    public void setNext(SLLNode<T> next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}
