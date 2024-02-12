import java.util.Random;

import static java.lang.Thread.sleep;

public class Philosopher4 implements Runnable {
    private final Object leftFork;
    private final Object rightFork;
    private final Random random;
    public Philosopher4(Object leftFork, Object rightFork) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.random = new Random();
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + action);
    }

    @Override
    public void run() {
        try {
            while (true) {
                doAction(": Thinking");
                sleeping();

                int x = random.nextInt(2);

                if (x == 0) {
                    synchronized (rightFork) {
                        doAction(": Picked up right fork");

                        synchronized (leftFork) {
                            doAction(": Picked up left fork - eating");
                            sleeping();

                            doAction(": Put down left fork");
                        }

                        doAction(": Put down right fork. Back to thinking");
                    }
                } else {
                    synchronized (leftFork) {
                        doAction(": Picked up left fork");

                        synchronized (rightFork) {
                            doAction(": Picked up right fork - eating");
                            sleeping();

                            doAction(": Put down right fork");
                        }

                        doAction(": Put down left fork. Back to thinking");
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void sleeping() throws InterruptedException {
        sleep(Main.thinkingAndEatingTime);
    }

}
