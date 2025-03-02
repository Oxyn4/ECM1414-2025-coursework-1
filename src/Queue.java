import java.util.ArrayList;
import java.util.List;

// Queue class: a simple FIFO queue implemented with a linked list.
// Used for managing floor requests in the elevator system.
public class Queue {
    private Node front;  // head of the queue (where elements are removed)
    private Node rear;   // tail of the queue (where elements are added)
    private int size;    // current number of elements

    public Queue() {
        front = null;
        rear = null;
        size = 0;
    }

    // Check if the queue is empty.
    public boolean isEmpty() {
        return size == 0;
    }

    // Add a new floor request to the end of the queue.
    public void enqueue(int floor) {
        Node newNode = new Node(floor);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    // Remove and return the floor request at the front of the queue.
    public int dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        int result = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return result;
    }

    public List<Integer> dequeue(int n) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            result.add(dequeue());
        }

        return result;
    }

    public List<Integer> dequeueAll() {
        List<Integer> result = new ArrayList<>();

        while (this.isEmpty()) {
            result.add(dequeue());
        }

        return result;
    }

    public void enqueue(List<Integer> requests) {
        for (Integer request : requests) {
            enqueue(request);
        }
    }

    // Look at the front floor request without removing it.
    public int peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return front.data;
    }

    public int peek(int position) {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        Node Previous = this.front;

        for (int i = 0; i < position; i++) {
            Previous = Previous.next;
        }

        return Previous.data;
    }

    public List<Integer> peekAll() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < this.size(); i++) {
            result.add(peek(i));
        }
        return result;
    }

    // Return the number of elements in the queue.
    public int size() {
        return size;
    }

    // Return a string representation of the queue.
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node curr = front;
        while (curr != null) {
            sb.append(curr.data);
            if (curr.next != null) {
                sb.append(" -> ");
            }
            curr = curr.next;
        }
        return "Queue: " + sb;
    }
}
