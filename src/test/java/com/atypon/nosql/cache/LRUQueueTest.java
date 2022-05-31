package com.atypon.nosql.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LRUQueueTest {
    @Test
    void add() {
        LRUQueue<String> queue = new LRUQueue<>();
        queue.add("Parrot");
        queue.add("Dog");
        queue.add("Cat");
        assertEquals("[Parrot, Dog, Cat]", queue.toString());
    }

    @Test
    void use() {
        LRUQueue<String> queue = new LRUQueue<>();
        queue.add("Parrot");
        queue.add("Dog");
        queue.add("Cat");
        queue.use("Parrot");
        assertEquals("[Dog, Cat, Parrot]", queue.toString());
        queue.use("Cat");
        assertEquals("[Dog, Parrot, Cat]", queue.toString());
    }

    @Test
    void removeLeastUsed() {
        LRUQueue<String> queue = new LRUQueue<>();
        queue.add("Parrot");
        queue.add("Dog");
        queue.add("Cat");
        queue.use("Parrot");
        queue.use("Cat");
        assertEquals("Dog", queue.removeLeastUsed());
        assertEquals("[Parrot, Cat]", queue.toString());
        assertEquals("Parrot", queue.removeLeastUsed());
        assertEquals("[Cat]", queue.toString());
        assertEquals("Cat", queue.removeLeastUsed());
        assertEquals("[]", queue.toString());
        queue.add("Snake");
        assertEquals("[Snake]", queue.toString());
        assertEquals("Snake", queue.removeLeastUsed());
        assertEquals("[]", queue.toString());
    }
}