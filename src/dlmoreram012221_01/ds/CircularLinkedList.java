package dlmoreram012221_01.ds;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Implements a circular linked list
 * Maintains the invariant that tail.next = head
 * @param <T>
 */
public class CircularLinkedList<T> {
    private int size;
    private LinkedListNode<T> head;
    private LinkedListNode<T> tail;
    private LinkedListNode<T> lastSampled;

    public CircularLinkedList(){}

    //Rep invariant:
    // tail.next = head
    private void checkRep() {
        assert tail == null && head == null || head == tail || tail.next == head;
    }

    /**
     * Adds an element to the tail of the circularly linked list
     * @param element element to add
     * @return the node that was added
     */
    public LinkedListNode<T> addToTail(T element){
        LinkedListNode<T> newNode = new LinkedListNode<>(element);
        if (head == null && tail == null) {
            head = newNode;
            tail = newNode;
            newNode.next = newNode;
            newNode.prev = newNode;
        } else {
            assert head != null;
            assert tail != null;
            LinkedListNode<T> oldTail = tail;
            oldTail.next = newNode;
            tail = newNode;
            tail.prev = oldTail;
            tail.next = head;
            head.prev = tail;
        }
        size += 1;

        checkRep();
        return newNode;
    }

    /**
     * Removes the provided node from the list
     * @param node must be in the list
     */
    public void remove(LinkedListNode<T> node) {
        if (node == tail && node == head) {
            // Removing the only node
            head = null;
            tail = null;
            lastSampled = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            if (node == tail) {
                tail = node.prev;
            } else if (node == head) {
                head = node.next;
            }

            if (node == lastSampled){
                lastSampled = lastSampled.next;
            }
        }

        checkRep();
        size -= 1;
    }

    /**
     * Samples n items from the list
     * if n <= list.size(), samples circularly from the last node that was sampled
     * if n > list.size(), returns all items
     * @param n amount to sample
     * @return array of sampled elements
     */
    public List<T> sampleWithMemory(int n){
        if (n > size || size == 0) {
            return toList();
        } else {
            List<T> items = new LinkedList<>();
            if (lastSampled == null){
                lastSampled = head;
            }
            assert lastSampled != null;
            while (items.size() < n) {
                items.add(lastSampled.data);
                lastSampled = lastSampled.getNext();
            }

            return items;
        }
    }

    public LinkedListNode<T> getHead(){
        return head;
    }

    public LinkedListNode<T> getTail(){
        return tail;
    }

    public int getSize(){
        return size;
    }

    public List<T> toList() {
        List<T> returnList = new ArrayList<>();
        LinkedListNode<T> current = head;
        for (int i=0;i<size;i++) {
           returnList.add(current.data);
           current = current.next;
        }
        return returnList;
    }

    @Override
    public String toString() {
        String stringRep = "";
        LinkedListNode<T> current = head;
        for (int i=0;i<size;i++){
            stringRep += current.data;
            current = current.next;
        }
        return stringRep;
    }

}
