package com.gomezrondon.multithreadingdemo.entities;

//import com.github.javafaker.Faker;
import com.gomezrondon.multithreadingdemo.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;


public class Worker extends Thread implements Callable {
    private final int rowsPerBranch;
    private final AtomicLong userCode;
    private final ClientRepository repository;
    private static final Logger log = LoggerFactory.getLogger(Worker.class);

    public Worker(AtomicLong userCode, int rowsPerBranch, ClientRepository repository) {
        this.rowsPerBranch = rowsPerBranch;
        this.userCode = userCode;
        this.repository = repository;
    }

    @Override
    public void run() {
        work();
    }

    @Override
    public Object call() throws Exception {
        work();
        return "Done";
    }

    private void work() {
        String name = Thread.currentThread().getName();
//        Faker faker = new Faker(Locale.US);
        int lastRecord = rowsPerBranch;


        IntStream.range(0, rowsPerBranch).forEach(j -> {
            double salary = getRandomSalary(46_000, 250_000);

            Client client = new Client( userCode.getAndIncrement(), "4152", "javier", salary);
            repository.save(client);
            if (j % 10000 == 0 || lastRecord == j) { //peek every 10k records
                log.info(client + "        thread: "+name);
            }
        });
    }

    public static double getRandomSalary(int min, int max) {
        return (double) ((Math.random() * (max - min)) + min);
    }

}
