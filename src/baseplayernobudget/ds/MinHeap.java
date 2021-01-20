package baseplayernobudget.ds;
import java.util.Comparator;

public class MinHeap<T extends Comparable<T>> {
    private static final int DEFAULT_CAPACITY = 100;
    private T[] heap;
    private int size;
    private int capacity;
    private Comparator<? super T> c;

    public MinHeap() {
        this(DEFAULT_CAPACITY);
        size = 0;
    }
    public MinHeap(int initialCapacity) {
        this(initialCapacity, new Comparator<T>() {
            @Override
            public int compare(T a, T b) {
                Comparable<? super T> key = (Comparable<? super T>) a;
                return key.compareTo(b);
            }
        });
        capacity = initialCapacity;
    }
    /**
     * Construct an empty <code>MinHeap</code> specifying the method for comparing
     * objects of type T
     */
    public MinHeap(Comparator<? super T> comparator) {
        this(DEFAULT_CAPACITY,comparator);
    }
    /**
     * Construct an empty <code>MinHeap</code> specifying the method for comparing
     * objects of type T and the heap's initial capacity
     */
    @SuppressWarnings("unchecked")
    public MinHeap(int initialCapacity, Comparator<? super T> comparator) {
        this.c = comparator;
        heap = (T[]) new Comparable[initialCapacity];
        this.capacity = initialCapacity;
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size() == 0;
    }
    public void add(T data) {
        //assert (size <= capacity);
        heap[size++] = data;
        int parentIndex = size-1 > 0 ? (size-2)/2 : -1;
        int currentIndex = size - 1;
        T t;
        //Swap with parents and bubble up until the heap is valid
        while (parentIndex >= 0) {
            if (c.compare(data,heap[parentIndex]) < 0) {
                t = heap[currentIndex];
                heap[currentIndex] = heap[parentIndex];
                heap[parentIndex] = t;
                currentIndex = parentIndex;
            }
            parentIndex = parentIndex > 0 ? (parentIndex-1)/2 : -1;
        }
    }
    public T poll() {
       // assert(size > 0);
        T top = heap[0];
        heap[0] = heap[size-1];
        size--;
        bubbleDown(0);
        return top;
    }
    public T peek() {
        if (isEmpty()) throw new IllegalStateException();
        return heap[0];
    }
    public void clear(){
        size = 0;
    }
    private void bubbleDown(int root) {
        int smallest = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;
        //If left is smaller than the current smallest
        if (left < size && c.compare(heap[left],heap[root]) < 0) {
            smallest = left;
        }
        //IF the right child is smaller than the smallest
        if (right < size && c.compare(heap[right],heap[smallest])  < 0) {
            smallest = right;
        }
        //If the smallest is not the root bubble down again
        if (smallest != root) {
            T t = heap[smallest];
            int parent = smallest > 0 ? (smallest-1)/2 : -1;
            heap[smallest] = heap[parent];
            heap[parent] = t;
            bubbleDown(smallest);
        }
    }

    private static <T> void swap(T[] heap, int child, int parent) {
        T t = heap[child];
        heap[child] = heap[parent];
        heap[parent] = t;
    }
    /*
    private boolean hasLeftChild(int i) {
        return leftIndex(i) <= size;
    }
    private boolean hasRightChild(int i) {
        return rightIndex(i) <= size;
    }
    private boolean hasParent(int i) {
        return i > 0;
    }
     */
    private static int parentIndex(int i) {
        if (i > 0) return (i - 1) / 2;
        else return -1;
    }
    private static int leftIndex(int i) {
        return 2 * i + 1;
    }
    private static int rightIndex(int i) {
        return 2 * i + 2;
    }
}
