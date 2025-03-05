import java.util.List;
import java.util.ArrayList;

/**
 * Test for the Queue class
 * Makes sure the queue works as expected
 */
public class QueueTest {
    
    public static void main(String[] args) {
        testQueueBasic();
        testQueueDequeue();
        testQueueMultiple();
        testQueueEmpty();
        
        System.out.println("All Queue tests completed!");
    }
    
    static void testQueueBasic() {
        System.out.println("Testing basic queue operations...");
        
        // Make a new queue
        Queue q = new Queue();
        
        // Check if empty
        if (!q.isEmpty()) {
            System.out.println("FAILED: New queue should be empty");
            return;
        }
        
        // Add one item
        q.enqueue(5);
        
        // Check size
        if (q.size() != 1) {
            System.out.println("FAILED: Queue size should be 1 after adding one item");
            return;
        }
        
        // Peek at first item
        if (q.peek() != 5) {
            System.out.println("FAILED: Queue peek should return 5");
            return;
        }
        
        // Add another item
        q.enqueue(10);
        
        // Check size again
        if (q.size() != 2) {
            System.out.println("FAILED: Queue size should be 2 after adding two items");
            return;
        }
        
        // Make sure peek still returns first item
        if (q.peek() != 5) {
            System.out.println("FAILED: Queue peek should still return 5");
            return;
        }
        
        System.out.println("Basic queue operations test passed!");
    }
    
    static void testQueueDequeue() {
        System.out.println("Testing queue dequeue operations...");
        
        Queue q = new Queue();
        
        // Add a few floors
        q.enqueue(3);
        q.enqueue(7);
        q.enqueue(2);
        
        // Dequeue first item
        int first = q.dequeue();
        if (first != 3) {
            System.out.println("FAILED: First dequeued item should be 3, got " + first);
            return;
        }
        
        // Check size
        if (q.size() != 2) {
            System.out.println("FAILED: Queue size should be 2 after dequeueing one item");
            return;
        }
        
        // Dequeue second item
        int second = q.dequeue();
        if (second != 7) {
            System.out.println("FAILED: Second dequeued item should be 7, got " + second);
            return;
        }
        
        // Dequeue last item
        int third = q.dequeue();
        if (third != 2) {
            System.out.println("FAILED: Third dequeued item should be 2, got " + third);
            return;
        }
        
        // Queue should now be empty
        if (!q.isEmpty()) {
            System.out.println("FAILED: Queue should be empty after dequeueing all items");
            return;
        }
        
        System.out.println("Queue dequeue operations test passed!");
    }
    
    static void testQueueMultiple() {
        System.out.println("Testing enqueueing and dequeueing multiple items...");
        
        Queue q = new Queue();
        
        // Create list of floors
        List<Integer> floors = new ArrayList<>();
        floors.add(5);
        floors.add(8);
        floors.add(11);
        
        // Add all floors
        q.enqueue(floors);
        
        // Check size
        if (q.size() != 3) {
            System.out.println("FAILED: Queue size should be 3 after enqueueing 3 items");
            return;
        }
        
        // Dequeue 2 items
        List<Integer> result = q.dequeue(2);
        
        // Check result size
        if (result.size() != 2) {
            System.out.println("FAILED: Dequeuing 2 items should return list of size 2");
            return;
        }
        
        // Check if we got the right items
        if (result.get(0) != 5 || result.get(1) != 8) {
            System.out.println("FAILED: Dequeued items should be [5, 8], got " + result);
            return;
        }
        
        // Queue should have 1 item left
        if (q.size() != 1) {
            System.out.println("FAILED: Queue should have 1 item left");
            return;
        }
        
        // Check the last item
        if (q.peek() != 11) {
            System.out.println("FAILED: Last item should be 11, got " + q.peek());
            return;
        }
        
        System.out.println("Multiple items enqueue/dequeue test passed!");
    }
    
    static void testQueueEmpty() {
        System.out.println("Testing empty queue operations...");
        
        Queue q = new Queue();
        
        // Try to peek at empty queue
        int peekResult = q.peek();
        if (peekResult != -1) {
            System.out.println("FAILED: Peek on empty queue should return -1");
            return;
        }
        
        // Try to dequeue from empty queue
        int dequeueResult = q.dequeue();
        if (dequeueResult != -1) {
            System.out.println("FAILED: Dequeue on empty queue should return -1");
            return;
        }
        
        // Try to dequeue multiple from empty queue
        List<Integer> multiResult = q.dequeue(3);
        if (multiResult.size() != 0) {
            System.out.println("FAILED: Dequeueing from empty queue should return empty list");
            return;
        }
        
        // Dequeue more than available
        q.enqueue(42);
        List<Integer> overResult = q.dequeue(5);
        if (overResult.size() != 1 || overResult.get(0) != 42) {
            System.out.println("FAILED: Dequeueing more than available should return all available items");
            return;
        }
        
        System.out.println("Empty queue operations test passed!");
    }
}
