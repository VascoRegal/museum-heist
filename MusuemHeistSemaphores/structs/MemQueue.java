package structs;

public class MemQueue<T> {
    private int front, rear, size;
    private T[] array;

    public MemQueue( T [] array) {
        this.array = array;
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    public void enqueue(T object) {
        if (!isFull()) {
            rear = (rear + 1) % array.length;
            array[rear] = object;
            size += 1;
        }
    }

    public T dequeue() {
        T object = null;
        if (!isEmpty()) {

            object = this.array[front];
            front = (front + 1) % array.length;
            size -= 1;
        }
        return object;
    }

    public int size() {
        return this.size;
    }

    private boolean isFull() {
        return (size == array.length);
    }

    private boolean isEmpty() {
        return (size == 0);
    }
}

