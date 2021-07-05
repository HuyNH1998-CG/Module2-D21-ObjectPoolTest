import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TaxiThread implements Runnable {
    private TaxiPool taxiPool;

    public TaxiThread(TaxiPool taxiPool) {
        this.taxiPool = taxiPool;
    }

    @Override
    public void run() {
        takeATaxi();
    }

    private void takeATaxi() {
        try {
            System.out.println("New Client " + Thread.currentThread().getName());
            Taxi taxi = taxiPool.getTaxi();
            TimeUnit.MICROSECONDS.sleep(randInt(1000, 1500));
            taxiPool.release(taxi);
            System.out.println("Client served " + Thread.currentThread().getName());
        } catch (Exception e) {
            System.out.println("Rejected" + Thread.currentThread().getName());
        }
    }

    private int randInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
