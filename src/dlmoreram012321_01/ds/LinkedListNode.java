package dlmoreram012321_01.ds;

public class LinkedListNode<T> {
    LinkedListNode<T> next;
    LinkedListNode<T> prev;
    T data;
    public LinkedListNode(T data){
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public LinkedListNode<T> getNext(){
        return next;
    }

    public LinkedListNode<T> getPrev(){
        return prev;
    }
}