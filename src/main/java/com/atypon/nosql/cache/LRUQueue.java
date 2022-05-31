package com.atypon.nosql.cache;

import java.util.HashMap;
import java.util.Map;

class Node<T> {
    private final T value;

    private Node<T> next;

    private Node<T> prev;

    public Node(T value) {
        this.value = value;
    }

    public Node(T value, Node<T> prev, Node<T> next) {
        this.value = value;
        this.next = next;
        this.prev = prev;
    }

    public T getValue() {
        return value;
    }

    public Node<T> getNext() {
        return next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }
}

public class LRUQueue<T> {
    private Node<T> head;

    private Node<T> tail;

    private final Map<T, Node<T>> map = new HashMap<>();

    private int size = 0;

    private void remove(Node<T> node) {
        synchronized (map) {
            if (isEmpty()) {
                throw new IllegalStateException("Can't remove from an empty list");
            }
            if (!map.containsKey(node.getValue())) {
                throw new RuntimeException("Can't remove file because it does not exist: " + node.getValue());
            }
            if (head == node) {
                if (head.getNext() == null) {
                    head = tail = null;
                } else {
                    head = head.getNext();
                    head.setPrev(null);
                }
            } else if (tail == node) {
                tail = tail.getPrev();
                tail.setNext(null);
            } else {
                if (node.getPrev() != null) {
                    node.getPrev().setNext(node.getNext());
                }
                if (node.getNext() != null) {
                    node.getNext().setPrev(node.getPrev());
                }
            }
            map.remove(node.getValue());
            size--;
        }
    }

    public boolean isEmpty() {
        synchronized (map) {
            return size == 0;
        }
    }

    public void add(T value) {
        synchronized (map) {
            Node<T> node = new Node<>(value, tail, null);
            map.put(value, node);
            if (head == null) {
                head = tail = node;
            } else {
                tail.setNext(node);
                tail = node;
            }
            size++;
        }
    }

    public void use(T value) {
        remove(map.get(value));
        add(value);
    }

    public T removeLeastUsed() {
        T leastUsed = head.getValue();
        remove(head);
        return leastUsed;
    }

    public int size() {
        return size;
    }

    public void remove(T value) {
        remove(map.get(value));
    }

    public void removeAll() {
        synchronized (map) {
            for (Node<T> itr = head; itr != null; itr = itr.getNext()) {
                remove(itr);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[");
        for (Node<T> itr = head; itr != null; itr = itr.getNext()) {
            result.append(itr.getValue());
            if (itr.getNext() != null) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }
}
