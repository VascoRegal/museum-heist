package structs;

public class MemPartyTree<T> {
    
    public static class Node<V> {

        private V val;
        private Node<V> next;

        public Node(V object) {
            val = object;
            next = null;
        }

        public Node(V object, Node<V> nxt) {
            val = object;
            next = nxt;
        }

        public void setNext(Node<V> node) {
            next = node;
        }

        public Node<V> getNext() {
            return next;
        }

        public boolean hasNext() {
            return next != null;
        }
    }


    private Node<T> head;

    public MemPartyTree() {
        head = null;
    }

    public void add(T object) {
        Node<T> last;
        Node<T> newnode = new Node<T>(object);

        if (head == null) {
            head = newnode;
        } else {
            last = head;
            while (last.hasNext()) {
                last = last.getNext();
            }
            last.setNext(newnode);
        }
    }
}
