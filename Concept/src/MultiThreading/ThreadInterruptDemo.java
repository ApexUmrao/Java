package MultiThreading;

class MyThreadCombined extends Thread
{
    public void run()
    {
        // -------------------------------
        // PART 1: Interrupt flag checking
        // -------------------------------
        System.out.println("=== Checking Interrupt Flag ===");

        // Set interrupt flag
        Thread.currentThread().interrupt();

        // interrupted() -> true, then clears flag
        System.out.println("First interrupted(): " + Thread.interrupted());

        // interrupted() again -> false
        System.out.println("Second interrupted(): " + Thread.interrupted());

        // Set flag again
        Thread.currentThread().interrupt();

        // isInterrupted() -> true (does NOT clear flag)
        System.out.println("First isInterrupted(): " + Thread.currentThread().isInterrupted());

        // Still true
        System.out.println("Second isInterrupted(): " + Thread.currentThread().isInterrupted());

        // ------------------------------------
        // PART 2: Interrupt during sleep()
        // ------------------------------------
        System.out.println("\n=== Interrupt During Sleep ===");

        try
        {
            for (int i = 1; i <= 5; i++)
            {
                System.out.println("Count: " + i);

                // Sleep for 1 second
                Thread.sleep(1000);
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Thread Interrupted during sleep, exiting...");
        }
    }
}

public class ThreadInterruptDemo
{
    public static void main(String[] args)
    {
        MyThreadCombined t = new MyThreadCombined();
        t.start();

        // Give thread some time to start and reach sleep
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {}

        // Interrupt the thread while it is sleeping
        t.interrupt();
    }
}