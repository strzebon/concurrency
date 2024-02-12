import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class Philosopher2 implements Runnable {
    private final ReentrantLock leftFork;
    private final ReentrantLock rightFork;

    public static LinkedList<Integer> avgWaitingTime = new LinkedList<>();
    public static long totalWaitingTime = 0;
    public static int totalWaitingCounter = 0;

    private long waitingTime = 0;
    private int waitingCounter = 0;

    public Philosopher2(ReentrantLock leftFork, ReentrantLock rightFork) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + action);
    }

    private static synchronized void addToTotalTime(long waitingTime, int waitingCounter) {
        totalWaitingTime += waitingTime;
        totalWaitingCounter += waitingCounter;
        avgWaitingTime.add((Math.round((float) waitingTime / waitingCounter)));
    }

    @Override
    public void run() {
        try {
            while (true) {
                doAction(": Thinking");
                sleeping();

                long start = System.currentTimeMillis();

                while (!(leftFork.tryLock() && rightFork.tryLock())){
                    if (leftFork.isHeldByCurrentThread()) leftFork.unlock();
                    if (rightFork.isHeldByCurrentThread()) rightFork.unlock();
                    sleep(1);
                }

                waitingTime += System.currentTimeMillis() - start;
                waitingCounter += 1;

                doAction(": Picked up forks - eating");
                sleeping();

                leftFork.unlock();
                rightFork.unlock();

                doAction(": Put down forks. Back to thinking");
            }
        } catch (InterruptedException e) {
            addToTotalTime(waitingTime, waitingCounter);
            Thread.currentThread().interrupt();
        }
    }

    private static void sleeping() throws InterruptedException {
        sleep(Main.thinkingAndEatingTime);
    }

}