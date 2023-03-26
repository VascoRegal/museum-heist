package structs;

/**
 *  MemQueue Class
 * 
 *  parametric FIFO queue
 * 
 *  used to manage available and arriving thieves
 *  
 */
public class MemQueue<T> {
    private int in, out, size;
    private boolean empty;
    private T[] array;

    public MemQueue( T [] array) {
        this.array = array;
        this.in = this.out = 0;
        this.size = 0;
        empty = true;
    }

    public void enqueue(T object) {
        if ((in != out) || empty) {
            array[in] = object;
            in = (in + 1) % array.length;
            empty = false;
            size++;
        }
    }

    public T dequeue() {

        T obj = null;

        if (!empty) {
            obj = array[out];
            out = (out + 1) % array.length;
            empty = (in == out);
            size--;
        }
        return obj;
    }

    public T[] array() {
        return array;
    }

    public int size() {
        return this.size;
    }

    public T[] getArray() {
        return this.array;
    }

}

