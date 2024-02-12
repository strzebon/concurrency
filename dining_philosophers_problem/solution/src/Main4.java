import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.lang.Thread.sleep;

public class Main4 {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService e = Executors.newCachedThreadPool();

        Philosopher4[] philosophers = new Philosopher4[Main.numberOfPhilosophers];
        Object[] forks = new Object[philosophers.length];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < philosophers.length; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % forks.length];

            philosophers[i] = new Philosopher4(leftFork, rightFork);

            Thread t = new Thread(philosophers[i], "Philosopher " + (i + 1));
            e.execute(t);
        }

        sleep(Main.simulationTime);
        e.shutdownNow();
    }
}