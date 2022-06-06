package com.atypon.nosql.cache;

import com.atypon.nosql.utils.DoublyLinkedList;
import org.junit.jupiter.api.Test;

import static com.atypon.nosql.utils.DoublyLinkedList.Node;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DoublyLinkedListTest {
    @Test
    void add() {
        DoublyLinkedList<String> queue = new DoublyLinkedList<>();
        queue.add(Node.fromValue("Parrot"));
        queue.add(Node.fromValue("Dog"));
        queue.add(Node.fromValue("Cat"));
        assertEquals("[Parrot, Dog, Cat]", queue.toString());
    }

    @Test
    void moveToFront() {
        DoublyLinkedList<String> queue = new DoublyLinkedList<>();
        Node<String> parrot = Node.fromValue("Parrot");
        Node<String> dog = Node.fromValue("Dog");
        Node<String> cat = Node.fromValue("Cat");
        queue.add(parrot);
        queue.add(dog);
        queue.add(cat);
        queue.moveToFront(parrot);
        assertEquals("[Dog, Cat, Parrot]", queue.toString());
        queue.moveToFront(cat);
        assertEquals("[Dog, Parrot, Cat]", queue.toString());
    }

    @Test
    void removeAll() {
        DoublyLinkedList<String> queue = new DoublyLinkedList<>();
        queue.add(Node.fromValue("Parrot"));
        queue.add(Node.fromValue("Dog"));
        queue.add(Node.fromValue("Cat"));
        queue.clear();
        assertEquals("[]", queue.toString());
    }

    @Test
    void remove() {
        DoublyLinkedList<String> linkedList = new DoublyLinkedList<>();
        Node<String> A = Node.fromValue("A");
        Node<String> B = Node.fromValue("B");
        linkedList.add(A);
        linkedList.add(B);
        linkedList.remove(A);
        assertEquals("[B]", linkedList.toString());
        linkedList.remove(B);
        assertEquals("[]", linkedList.toString());
    }
}