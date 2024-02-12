import static java.lang.Thread.sleep;

public class Philosopher1 implements Runnable {
    private final Object leftFork;
    private final Object rightFork;

    public Philosopher1(Object leftFork, Object rightFork) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void sleeping() throws InterruptedException {
        sleep(Main.thinkingAndEatingTime);
    }

}