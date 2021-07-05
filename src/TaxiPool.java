import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TaxiPool {
    private static final long ExpiredTimeInMilis = 1200;
    private static final int NumberOfTaxi = 4;
    private final List<Taxi> avail = Collections.synchronizedList(new ArrayList<>());
    private final List<Taxi> inUse = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicBoolean wait = new AtomicBoolean(false);

    public synchronized void release(Taxi taxi){
        inUse.remove(taxi);
        avail.add(taxi);
        System.out.println(taxi.getName() + " is free");
    }

    public synchronized Taxi getTaxi(){
        if(!avail.isEmpty()){
            Taxi taxi = avail.get(0);
            inUse.add(taxi);
            return taxi;
        }
        if(count.get() == NumberOfTaxi){
            this.waitUntilAvail();
            return this.getTaxi();
        }
        Taxi taxi = this.createTaxi();
        inUse.add(taxi);
        return taxi;
    }

    private Taxi createTaxi(){
        waiting(200);
        Taxi taxi = new Taxi("Taxi " + count.incrementAndGet());
        System.out.println(taxi.getName() + " Created");
        return taxi;
    }

    private void waitUntilAvail(){
        if(wait.get()){
            wait.set(false);
            throw new TaxiNotFound("No taxi Avail");
        }
        wait.set(true);
        waiting(ExpiredTimeInMilis);
    }

    private void waiting(long time){
        try {
            TimeUnit.MICROSECONDS.sleep(time);
        } catch (Exception e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
