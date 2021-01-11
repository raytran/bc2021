package baseplayer;
import baseplayer.ds.MinHeap;

import java.util.Comparator;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
public class MinHeapTest {

    @Test
    public void testComparator(){
        Comparator<Integer> opposite = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2) * -1;
            }
        };
        MinHeap<Integer> ints = new MinHeap<Integer>(opposite);
        Random r = new Random();
        for(int i = 0; i < 50; i++) {
            int d = r.nextInt();
            ints.add(d);
        }
        assertEquals(50, ints.size());
        Integer prev = Integer.MAX_VALUE;
        while(!ints.isEmpty()){
            Integer polled = ints.poll();
            if(polled.compareTo(prev) > 0){
                fail("THINGS BROKEN LOL");
                System.out.println("THE HEAP IS BROKEN: ");
                System.out.println(polled);
            }
            else System.out.println(polled);
            prev = polled;
        }
    }
    @Test
    public void testInsertThenRemoveOrdered(){
        MinHeap<Integer> numbers = new MinHeap<Integer>(100);
        for(int i = 0; i < 100; i++){
            numbers.add(i);
        }
        for(int i = 0; i < 100; i++){
            assertEquals(new Integer(i), numbers.poll());
            //System.out.println(numbers.poll());
        }
    }
    @Test
    public void testRandomDoubles(){
        MinHeap<Double> doubles = new MinHeap<Double>();
        Random r = new Random();
        for(int i = 0; i < 50; i++) {
            double d = r.nextDouble();
            doubles.add(d);
        }
        Double prev = 0.0;
        while(!doubles.isEmpty()){
            Double polled = doubles.poll();
            if(polled.compareTo(prev) < 0){
                System.out.println("THE HEAP IS BROKEN: ");
                fail("THINGS BROKEN LOL");
                System.out.println(polled);
            }
            else System.out.println(polled);
            prev = polled;
        }
    }
    @Test
    public void testRandomChars(){
        MinHeap<Character> chars = new MinHeap<Character>();
        Random r = new Random();
        for(int i = 0; i < 50; i++) {
            char c = (char) (r.nextInt(26) + 'a');
            chars.add(c);
        }
        Character prev = 'a';
        while(!chars.isEmpty()){
            Character polled = chars.poll();
            if(polled.compareTo(prev) < 0){
                fail("THINGS BROKEN LOL");
                System.out.println(polled);
            }
            else System.out.println(polled);
            prev = polled;
        }
    }
    @Test
    public void addDeleteAddDelete(){
        MinHeap<Integer> minHeap = new MinHeap<Integer>();
        minHeap.add(1);
        minHeap.add(7);
        minHeap.add(2);
        minHeap.add(1);
        int pollCounter = 0;
        while(!minHeap.isEmpty()){
            System.out.println(minHeap.poll());
            pollCounter++;
        }
        assertEquals(4,pollCounter);
    }

}
