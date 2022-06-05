package com.atypon.nosql.utils;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DoublyLinkedList<T> {
    private Node<T> head;

    private Node<T> tail;

    private int size = 0;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void remove(Node<T> node) {
        lock.writeLock().lock();
        if (head == node) {
            if (head.next == null) {
                head = tail = null;
            } else {
                head = head.next;
                head.prev = null;
            }
        } else if (tail == node) {
            tail = tail.prev;
            tail.next = null;
        } else {
            if (node.prev != null) {
                node.prev.next = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }
        }
        size--;
        lock.writeLock().unlock();
    }

    public boolean isEmpty() {
        lock.readLock().lock();
        boolean empty = size == 0;
        lock.readLock().unlock();
        return empty;
    }

    public void add(Node<T> node) {
        lock.writeLock().lock();
        node.prev = tail;
        node.next = null;
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
        lock.writeLock().unlock();
    }

    public void moveToFront(Node<T> node) {
        lock.writeLock().lock();
        remove(node);
        add(node);
        lock.writeLock().unlock();
    }

    public int size() {
        lock.readLock().lock();
        int currentSize = size;
        lock.readLock().unlock();
        return currentSize;
    }

    public void clear() {
        lock.writeLock().lock();
        for (Node<T> itr = head; itr != null; itr = itr.next) {
            remove(itr);
        }
        lock.writeLock().unlock();
    }

    public Node<T> getFront() {
        lock.readLock().lock();
        Node<T> currentHead = head;
        lock.readLock().unlock();
        return currentHead;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[");
        lock.readLock().lock();
        for (Node<T> itr = head; itr != null; itr = itr.next) {
            result.append(itr.value);
            if (itr.next != null) {
                result.append(", ");
            }
        }
        lock.readLock().unlock();
        result.append("]");
        return result.toString();
    }

    public static class Node<T> {
        private final T value;

        private Node<T> next;

        private Node<T> prev;

        public static <T> Node<T> fromValue(T value) {
            return new Node<>(value);
        }

        public Node(T value) {
            this.value = value;
        }

        public Node(T value, Node<T> prev, Node<T> next) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }

        public T value() {
            return value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    '}';
        }
    }
}
