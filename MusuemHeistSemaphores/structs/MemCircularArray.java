package structs;

public class MemCircularArray<T> {
    
    private T [] data;

    private int index;

    public MemCircularArray(T [] array) {
        data = array;
        index = 0;
    }

    public void put( T object ) {
        if (index == data.length) {
            index = 0;
        }
        data[index] = object;
        System.out.println("[CA - PUT] PUT " + index);
        index++;
    }

    public T get () {
        if (index == data.length) {
            index = 0;
        }
        T obj = data[index];
        System.out.println("[CA - GET] GET " + index);
        index++;
        return obj;
    }

    public T next () {
        index++;
        if (index == data.length) {
            index = 0;
        }
        return data[index];
    }

    public T [] getArray() {
        return data;
    }

}
