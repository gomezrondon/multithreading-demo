package com.gomezrondon.multithreadingdemo.appconfig;



//import com.github.javafaker.Faker;
import com.gomezrondon.fakedata.utils.BackupServiceKt;
import com.gomezrondon.multithreadingdemo.entities.BatchJob;
import com.gomezrondon.multithreadingdemo.entities.BatchJobId;
import com.gomezrondon.multithreadingdemo.entities.BatchStatus;
import com.gomezrondon.multithreadingdemo.entities.Client;
import com.gomezrondon.multithreadingdemo.entities.WorkRange;
import com.gomezrondon.multithreadingdemo.entities.Worker;
import com.gomezrondon.multithreadingdemo.repository.BatchJobRepository;
import com.gomezrondon.multithreadingdemo.repository.ClientRepository;
import com.gomezrondon.multithreadingdemo.service.ProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Configuration
public class Config {

    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private final ProcessingService processingService;

    @Value("${app.total.records:10000}")
    private int totalRecords;
    @Value("${app.core.count:0}")
    private int coreCount;
    @Value("${app.restore:false}")
    private boolean restoreBackups;
    @Value("${app.trigger.error:false}")
    private boolean triggerError;
    @Value("${app.chunk.percent:0.10}")
    private float chunkSize;

    public Config(ProcessingService processingService) {
        this.processingService = processingService;
    }


    @Bean
    public String printConfiguration() {
        getCoreCount();
        StringBuilder strBuilder = new StringBuilder(" \n");
        strBuilder.append("============ Core Count:    "+coreCount+"    ============ \n")
                  .append("============ Total Records: "+totalRecords+" ============ \n")
                  .append("============ Chunk size: "+((totalRecords / coreCount) * chunkSize)+"    ============ \n")
                  .append("============ Restore backup: "+restoreBackups+"    ============ \n")
                  .append("============ Trigger Error: "+triggerError+"    ============ \n")
                  .append(" \n");

        return strBuilder.toString();
    }

    @Bean
    @Profile("retry")
    public CommandLineRunner commandLReProcessWork(ClientRepository clientRepository, BatchJobRepository batchJobRepository) {
        return (args) -> {
            log.info(" ");
            log.info("=========== Restoring Batch table  ========= ");
            log.info(" ");

            processingService.reExecuteProcess();

            long totalRecords = StreamSupport.stream(clientRepository.findAll().spliterator(), false).count();

            log.info(" ");
            log.info("=============== Split Work ========= ");
            log.info(" ");
          //  getCoreCount();

            processingService.ReSplitWorkLoad();
            var batchTable = (List<BatchJob> )batchJobRepository.findAll();
            batchTable.forEach(member -> log.info(String.valueOf(member)));

            log.info(" ");
            log.info("=============== Start Work ========= ");
            log.info(" ");
            var batchJobMembers = batchTable.stream()
                    .filter(t -> t.getStatus().equals(BatchStatus.INITIAL.getValue()))
                    .map(batchJob -> new BatchJobId(batchJob.getBatchId(), batchJob.getThreadId()))
                    .collect(Collectors.toList());

            processingService.startWork(batchJobMembers);
            // Check if everything ended correctly
            batchTable = (List<BatchJob> )batchJobRepository.findAll();
            var countBatch = batchTable.stream().map(BatchJob::getStatus).filter(t -> t.equals(BatchStatus.FINISHED.getValue())).count();

            if (countBatch == coreCount) {

               BigDecimal avg = batchTable.stream()
                        .map(BatchJob::getSalaryTotalSum)
                        .reduce(BigDecimal::add)
                        .get()
                        .divide(BigDecimal.valueOf(totalRecords));

                log.info(" ");
                log.info("========= Multi-Thread Calculation AVG Salary:  " + avg);
                log.info(" ");

                batchTable.forEach(member -> log.info(String.valueOf(member)));


                processingService.directValidation();

            }

        };

    }

    private void getCoreCount() {
        if (coreCount == 0) {
            coreCount = Runtime.getRuntime().availableProcessors();
        }
    }

    @Bean
    @Profile("normal")
    public CommandLineRunner commandLRWithExecutorService(ClientRepository clientRepository, BatchJobRepository batchJobRepository) {
        return (args) -> {
            var startTime = Instant.now();
            if (restoreBackups) {
                log.info(" ");
                log.info("=========== Restoring Batch table  ========= ");
                log.info(" ");
                processingService.restoreH2Tables();

            }else {
                log.info(" ");
                log.info("=========== Generating Fake Data ========= ");
                log.info(" ");

//                var faker = new Faker(Locale.US);
                var atomicId = new AtomicLong(0);
//                var lista = new ArrayList<Thread>();

                var threadList =  IntStream.range(0, totalRecords)
                        .mapToObj(index -> Thread.ofVirtual().unstarted(() -> {
                            var salary = getRandomSalary(46000, 250000);
                            var client = new Client(atomicId.getAndIncrement(), "1254", "pepe", salary);
                            clientRepository.save(client);
                        })

                        ).toList();

                threadList.forEach(Thread::start);

                for (Thread thread : threadList) {
                    thread.join();
                }

            }

            log.info(" ");
            log.info("=============== Split Work ========= ");
            log.info(" ");

         //   getCoreCount();

            List<WorkRange> workRanges = processingService.splitWorkLoad(coreCount);
//            workRanges.forEach(range -> log.info(String.valueOf(range)));

            log.info(" ");
            log.info("=============== Assign Work ========= ");
            log.info(" ");
            List<BatchJobId> batchJobMembers = processingService.setTheWorkTable(workRanges);
            List<BatchJob> batchTable = (List<BatchJob> )batchJobRepository.findAll();
           // batchTable.forEach(member -> log.info(String.valueOf(member)));

            log.info(" ");
            log.info("=============== Start Work ========= ");
            log.info(" ");
            BigDecimal totalSum = processingService.startWork(batchJobMembers);
           // Check if everything ended correctly
              batchTable = (List<BatchJob> )batchJobRepository.findAll();
            var countBatch = batchTable.stream().map(BatchJob::getStatus).filter(t -> t.equals(BatchStatus.FINISHED.getValue())).count();


            if (countBatch == coreCount) {

                BigDecimal bdTotal = BigDecimal.valueOf(totalRecords);
                BigDecimal avg = totalSum.divide(bdTotal, 2,RoundingMode.CEILING);
                log.info(" ");
                log.info("========= Multi-Thread Calculation AVG Salary:  " + avg.setScale(2, RoundingMode.CEILING));
                log.info(" ");

            //    batchTable.forEach(member -> log.info(String.valueOf(member)));

                BigDecimal total = batchTable.stream()
                        .map(BatchJob::getSalaryTotalSum)
                        .reduce(BigDecimal::add)
                        .get()
                        .divide(BigDecimal.valueOf(totalRecords), 2, RoundingMode.CEILING);
                log.info(" ");
                log.info("========= total from batch table: " + total);
                log.info(" ");

                processingService.directValidation();

            } else {
                log.info("");
                log.info("");
                log.info("*************** One or More thread ended with Error  **********************");
                log.info("");
                log.info("");

              //  batchTable.forEach(member -> log.info(String.valueOf(member)));
            }
            var stopTime = Instant.now();
           log.info(Duration.between(startTime, stopTime).toSeconds() +" - Seconds");

            List<Client> clientList = StreamSupport.stream(clientRepository.findAll().spliterator(), false).collect(Collectors.toList());

            if (!restoreBackups) {// do not override the backup
                BackupServiceKt.backUpH2BatchJob(batchTable);
                BackupServiceKt.manageH2BackupWrite("Client-backup", clientList);
                log.info("");
                log.info("*************** Finishing backup of tables **********************");
                log.info("");
            }



        };

    }

    private double getRandomSalary(int min, int max) {
        return  (Math.random() * (max - min) + min);
    }


    @Bean
    @Profile("thread")
    public CommandLineRunner applicationStartupRunner(ClientRepository repository) {
        return (args) -> {

            AtomicLong userCode = new AtomicLong(1);

            int coreCount = Runtime.getRuntime().availableProcessors();
            int rowsPerBranch = totalRecords / coreCount;

            List<Thread> tasks = new ArrayList<>(coreCount);
            IntStream.range(0, coreCount).forEach(i -> {
                synchronized (tasks) {
                    tasks.add(new Worker(userCode, rowsPerBranch, repository));
                }
            });

            System.out.println(" ");
            System.out.println("============ Capacity: "+coreCount+" ============ ");
            System.out.println("============ Records per Core: "+rowsPerBranch+" ============ ");
            System.out.println("============ Total Records: "+totalRecords+" ============ ");
            System.out.println(" ");

            IntStream.range(0, coreCount).forEach(i -> {
                tasks.get(i).start();
            });

            IntStream.range(0, coreCount).forEach(i -> {
                try {
                    tasks.get(i).join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("=============== Finished inserting data ========= ");
        };

    }

}
