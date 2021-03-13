package com.gomezrondon.multithreadingdemo.service;

import com.gomezrondon.multithreadingdemo.entities.BatchJobId;
import com.gomezrondon.multithreadingdemo.entities.WorkRange;

import java.math.BigDecimal;
import java.util.List;

public interface ProcessingService {

     List<WorkRange> splitWorkLoad(int numOfCores);

     List<BatchJobId> setTheWorkTable(List<WorkRange> list);

     BigDecimal startWork(List<BatchJobId> list, int coreCount) throws InterruptedException;

     void reExecuteProcess();

     void ReSplitWorkLoad();

     void directValidation();

     void restoreH2Tables();
}
