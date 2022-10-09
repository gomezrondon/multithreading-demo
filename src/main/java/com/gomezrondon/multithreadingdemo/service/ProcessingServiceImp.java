package com.gomezrondon.multithreadingdemo.service;

import com.gomezrondon.fakedata.utils.BackupServiceKt;
import com.gomezrondon.multithreadingdemo.entities.BatchJob;
import com.gomezrondon.multithreadingdemo.entities.BatchJobId;
import com.gomezrondon.multithreadingdemo.entities.BatchStatus;
import com.gomezrondon.multithreadingdemo.entities.Client;
import com.gomezrondon.multithreadingdemo.entities.ProcessSalaryWorker;
import com.gomezrondon.multithreadingdemo.entities.WorkRange;
import com.gomezrondon.multithreadingdemo.repository.BatchJobRepository;
import com.gomezrondon.multithreadingdemo.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.gomezrondon.fakedata.utils.BackupServiceKt.splitListInChunks;


@Service
public class ProcessingServiceImp implements ProcessingService {

    private static final Logger log = LoggerFactory.getLogger(ProcessingServiceImp.class);

    private final ClientRepository clientRepository;
    private final BatchJobRepository batchJobRepository;
    @Value("${app.trigger.error:false}") //"${some.key:true}"
    private boolean forceError;
    @Value("${app.chunk.percent:0.10}")
    public float CHUNK_PERCENT;

    public ProcessingServiceImp(ClientRepository clientRepository, BatchJobRepository batchJobRepository) {
        this.clientRepository = clientRepository;
        this.batchJobRepository = batchJobRepository;
    }

    @Override
    public void reExecuteProcess() {
        List<BatchJob> batchBackup = BackupServiceKt.restoreH2BatchJob();
        long count = batchBackup.stream().filter(x -> x.getStatus().equals(BatchStatus.ERROR.getValue())).count();

        if (count > 0) {
            List<Client> clientBackup =  BackupServiceKt.manageH2BackupRead("Client-backup" );
            clientRepository.saveAll(clientBackup);
            batchJobRepository.saveAll(batchBackup);
        }
    }

    public void ReSplitWorkLoad() {
        List<BatchJob> batchJobList = (List<BatchJob>) batchJobRepository.findAll();
        List<BatchJob> BatchErrorList = batchJobList.stream().filter(x -> x.getStatus().equals(BatchStatus.ERROR.getValue())).toList();

        BatchErrorList.forEach(batchJob -> {
            Long lastElement = batchJob.getLastElement();
            batchJob.setRangeIni(lastElement + 1);
            batchJob.setStatus(BatchStatus.INITIAL.getValue());
            batchJobRepository.save(batchJob);
        });

    }


    @Override
    public List<WorkRange> splitWorkLoad(int numOfCores) {

        List<Long> allClientId = clientRepository.findAllClientId();
        Collections.sort(allClientId);

        int total = allClientId.size();
        int range = total / numOfCores;
        List<List<Long>> chunkList = splitListInChunks(allClientId, range);

        List<WorkRange> workRangeList = new ArrayList<>();
        chunkList.forEach(list -> {
            WorkRange wRange = new WorkRange();
            //  System.out.println(list.get(0)+" "+list.get(list.size()-1));
            wRange.iniRange = list.get(0);
            wRange.endRange = list.get(list.size() - 1);
            //  System.out.println(iniRange +" "+endRange);
            workRangeList.add(wRange);
        });

        return workRangeList;
    }

    @Override
    public List<BatchJobId> setTheWorkTable(List<WorkRange> list) {

        BatchJob top = batchJobRepository.findLastId();
        Long batchId = 1L;
        if (top != null) {
            batchId = top.getBatchId() + 1;
        }

        List<BatchJobId> outPutList = new ArrayList<>();
        for (int i = 0; i < list.size() ; i++) {
            WorkRange workRange = list.get(i);
            long index = i + 1;
            BatchJob batchJob = new BatchJob(batchId, index, "processing clients", workRange.iniRange, workRange.endRange,BatchStatus.INITIAL.getValue());
            batchJob.setRecordCount(workRange.endRange - workRange.iniRange + 1);
            batchJobRepository.save(batchJob);
            outPutList.add(new BatchJobId(batchId, index));
        }

        return outPutList;
    }

    @Override
    public BigDecimal startWork(List<BatchJobId> list, int coreCount) throws InterruptedException {

        List<Future<BigDecimal>> futures = null;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<BigDecimal>> tasks = new ArrayList<>();
            for (BatchJobId batchJobId : list) {
                tasks.add(new ProcessSalaryWorker(batchJobId, batchJobRepository, clientRepository, forceError, CHUNK_PERCENT));
            }
            futures =  executor.invokeAll(tasks);
        }


       BigDecimal reduce = futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
           return BigDecimal.ZERO;
        }).reduce(BigDecimal::add).get();

        List<BatchJob> batchJobList = (List<BatchJob>) batchJobRepository.findAll();
        batchJobList.stream()
                .filter(batchJob -> batchJob.getStatus().equals(BatchStatus.START.getValue()))
                .forEach(batchJob -> {
                    batchJob.setStatus(BatchStatus.ERROR.getValue());
                    batchJobRepository.save(batchJob);
                });

        return reduce;
    }

    @Override
    public void directValidation() {
        // ============= Validating Multi-Thread Calculation =============================
      BigDecimal reduce = StreamSupport.stream(clientRepository.findAll().spliterator(), false)
                .map(client -> BigDecimal.valueOf(client.getSalary())).reduce(BigDecimal::add).get();

        long count = StreamSupport.stream(clientRepository.findAll().spliterator(), false).count();
        BigDecimal total = reduce.divide(BigDecimal.valueOf(count),2, RoundingMode.CEILING);
        log.info(" ");
        log.info("========= Validating Multi-Thread Calculation total: " + total.setScale(2, RoundingMode.CEILING));
    }

    @Override
    public void restoreH2Tables() {
        List<Client> lita =  BackupServiceKt.manageH2BackupRead("Client-backup" );
        clientRepository.saveAll(lita);

    }


}
