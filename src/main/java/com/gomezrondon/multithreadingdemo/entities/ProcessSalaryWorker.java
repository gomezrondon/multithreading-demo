package com.gomezrondon.multithreadingdemo.entities;


import com.gomezrondon.multithreadingdemo.repository.BatchJobRepository;
import com.gomezrondon.multithreadingdemo.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class ProcessSalaryWorker implements Callable<BigDecimal> {

    private static final Logger log = LoggerFactory.getLogger(ProcessSalaryWorker.class);

    private final BatchJobRepository batchJobRepository;
    private final ClientRepository clientRepo;
    private final BatchJobId batchId;
    private final boolean forceError;
    private final float chunck_percent;


    public ProcessSalaryWorker(BatchJobId batchId, BatchJobRepository batchJobRepository, ClientRepository clientRepo, Boolean forceError, float chunck_percent) {
        this.batchId = batchId;
        this.batchJobRepository = batchJobRepository;
        this.clientRepo = clientRepo;
        this.forceError = forceError;
        this.chunck_percent = chunck_percent;
    }

    @Override
    public BigDecimal call() {

        String name = Thread.currentThread().getName();

        Optional<BatchJob> op = batchJobRepository.findById(batchId);
        BigDecimal sum = BigDecimal.ZERO;

        if (op.isPresent()) {
            BatchJob batchJob = op.get();
            batchJob.setStatus(BatchStatus.START.getValue());  // ***** Start Status ****
            batchJobRepository.save(batchJob);

            BigDecimal salaryTotalSum = batchJob.getSalaryTotalSum();
            if (salaryTotalSum != null) {
                sum = sum.add(salaryTotalSum) ; // pick total from table
            }

            Long ini = batchJob.getRangeIni();
            Long end = batchJob.getRangeEnd();
            int printNumber = 0;


            int CHUNK_SIZE = (int) (batchJob.getRecordCount() * chunck_percent);

            Long tempEnd = ini;
            while (ini <= end) {
                tempEnd += CHUNK_SIZE;

                if (tempEnd > end) {
                    tempEnd = end;
                }

                //BigDecimal.valueOf()
                List<Double> clientIdRange = clientRepo.findClientSalaryRange(ini, tempEnd);

                sum = sum.add(BigDecimal.valueOf(clientIdRange.stream().reduce((double) 0, Double::sum)));

                for (Long i = ini; i <= tempEnd; i++) {
                    Client client = clientRepo.findById(i).get();
                    client.setStatus(BatchStatus.PROCESSING.getValue());
                    clientRepo.save(client);
                }

                batchJob.setLastElement(tempEnd);

                batchJob.setSalaryTotalSum(sum);
                batchJobRepository.save(batchJob);

                if (batchJob.getThreadId() == 2L && forceError) {
                    throw new RuntimeException("Something when wrong!!");
                }

/*                if (printNumber % 10 == 0 ) {
                   log.info("Working Thread: "+name + " Calculating avg salary...");
                }*/

                ini = tempEnd + 1;
                printNumber++;

            }

            //last element
            batchJob.setLastElement(end);
            batchJob.setStatus(BatchStatus.FINISHED.getValue());  // ***** Finished Status ****
            batchJob.setSalaryTotalSum(sum);
            batchJobRepository.save(batchJob);
        }

        return sum;
    }
}
