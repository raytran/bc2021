package baseplayer;
import baseplayer.ds.CircularLinkedList;
import baseplayer.ds.LinkedListNode;
import org.junit.Test;
import scala.Array;
import scala.collection.immutable.List;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class CircularLinkedListTest {

    @Test
    public void testAddThenRemove() {
        CircularLinkedList<String> list = new CircularLinkedList<>();
        LinkedListNode<String> first = list.addToTail("a");
        LinkedListNode<String> second = list.addToTail("b");
        LinkedListNode<String> third = list.addToTail("c");
        LinkedListNode<String> last = list.addToTail("d");
        assertEquals(Arrays.asList("a", "b", "c","d"), list.toList());

        list.remove(first);
        assertEquals(Arrays.asList("b", "c","d"), list.toList());


        list.remove(third);
        assertEquals(Arrays.asList("b","d"), list.toList());

        list.remove(last);
        assertEquals(Arrays.asList("b"), list.toList());

        list.remove(second);
        assertEquals(new ArrayList<>(), list.toList());
    }

    @Test
    public void testAddRemoveInterleave(){
        CircularLinkedList<String> list = new CircularLinkedList<>();
        LinkedListNode<String> a = list.addToTail("a");
        LinkedListNode<String> b = list.addToTail("b");
        assertEquals(Arrays.asList("a", "b"), list.toList());
        list.remove(a);
        assertEquals(Arrays.asList("b"), list.toList());
        LinkedListNode<String> c = list.addToTail("c");
        list.remove(b);
        assertEquals(Arrays.asList("c"), list.toList());
        LinkedListNode<String> d = list.addToTail("d");
        assertEquals(Arrays.asList("c","d"), list.toList());
    }

    @Test
    public void testGetHead(){
        CircularLinkedList<String> list = new CircularLinkedList<>();
        assertNull(list.getHead());
        LinkedListNode<String> a = list.addToTail("a");
        LinkedListNode<String> b = list.addToTail("b");
        assertEquals(Arrays.asList("a", "b"), list.toList());
        assertEquals(a, list.getHead());
        list.remove(a);
        assertEquals(Arrays.asList("b"), list.toList());
        assertEquals(b, list.getHead());
        LinkedListNode<String> c = list.addToTail("c");
        list.remove(b);
        assertEquals(Arrays.asList("c"), list.toList());
        LinkedListNode<String> d = list.addToTail("d");
        assertEquals(Arrays.asList("c","d"), list.toList());
        assertEquals(c, list.getHead());
    }

    @Test
    public void testGetTail(){
        CircularLinkedList<String> list = new CircularLinkedList<>();
        assertNull(list.getTail());
        LinkedListNode<String> a = list.addToTail("a");
        LinkedListNode<String> b = list.addToTail("b");
        assertEquals(Arrays.asList("a", "b"), list.toList());
        assertEquals(b, list.getTail());
        list.remove(a);
        assertEquals(Arrays.asList("b"), list.toList());
        assertEquals(b, list.getTail());
        LinkedListNode<String> c = list.addToTail("c");
        list.remove(b);
        assertEquals(Arrays.asList("c"), list.toList());
        assertEquals(c, list.getTail());
        LinkedListNode<String> d = list.addToTail("d");
        assertEquals(Arrays.asList("c","d"), list.toList());
        assertEquals(d, list.getTail());
    }

    @Test
    public void testCircle(){
        CircularLinkedList<String> list = new CircularLinkedList<>();
        LinkedListNode<String> a = list.addToTail("a");
        assertEquals(a, a.getNext());
        assertEquals(a, a.getPrev());
        LinkedListNode<String> b = list.addToTail("b");
        assertEquals(a, b.getNext());
        assertEquals(a.getPrev(), b);

        LinkedListNode<String> c = list.addToTail("c");
        assertEquals(b, a.getNext());
        list.remove(b);
        assertEquals(c, a.getPrev());
        assertEquals(c.getNext(), a);
    }

    @Test
    public void testSize(){
        CircularLinkedList<String> list = new CircularLinkedList<>();
        assertEquals(0, list.getSize());
        LinkedListNode<String> a = list.addToTail("a");
        list.addToTail("b");
        assertEquals(2, list.getSize());
        list.remove(a);
        assertEquals(1, list.getSize());
    }

    @Test
    public void testSample(){
        CircularLinkedList<String> list = new CircularLinkedList<>();
        boolean exceptionThrown1 = false;
        try{
            list.sampleWithMemory(500);
        } catch (RuntimeException e) {
            exceptionThrown1 = true;
        }
        assertTrue(exceptionThrown1);

        list.addToTail("a");


        boolean exceptionThrown2 = false;
        try{
            list.sampleWithMemory(0);
        } catch (IllegalArgumentException e) {
            exceptionThrown2 = true;
        }
        assertTrue(exceptionThrown2);


        list.addToTail("b");
        list.addToTail("c");
        list.addToTail("d");

        assertEquals(Arrays.asList("a","b","c"), list.sampleWithMemory(3));
    }

    @Test
    public void testSampleNGreater(){
        CircularLinkedList<String> list = new CircularLinkedList<>();
        list.addToTail("a");
        list.addToTail("b");
        assertEquals(Arrays.asList("a", "b"), list.sampleWithMemory(5));
    }

    @Test
    public void testSampleWithMemory() {
        CircularLinkedList<String> list = new CircularLinkedList<>();
        LinkedListNode<String> a = list.addToTail("a");
        LinkedListNode<String> b = list.addToTail("b");
        LinkedListNode<String> c = list.addToTail("c");
        LinkedListNode<String> d = list.addToTail("d");
        LinkedListNode<String> e = list.addToTail("e");
        assertEquals(Arrays.asList("a","b","c"), list.sampleWithMemory(3));
        assertEquals(Arrays.asList("d","e","a"), list.sampleWithMemory(3));

        LinkedListNode<String> f = list.addToTail("f");
        LinkedListNode<String> g = list.addToTail("g");
        LinkedListNode<String> h = list.addToTail("h");

        assertEquals(Arrays.asList("b","c","d","e","f"), list.sampleWithMemory(5));
        assertEquals(Arrays.asList("g","h","a","b","c"), list.sampleWithMemory(5));
        list.remove(d);
        list.remove(e);
        assertEquals(Arrays.asList("f","g"), list.sampleWithMemory(2));
        assertEquals(Arrays.asList("h"), list.sampleWithMemory(1));
        list.remove(a);
        list.remove(c);
        assertEquals(Arrays.asList("b", "f", "g"), list.sampleWithMemory(3));

    }
}
