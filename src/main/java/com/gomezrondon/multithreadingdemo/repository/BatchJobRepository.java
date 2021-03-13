package com.gomezrondon.multithreadingdemo.repository;


import com.gomezrondon.multithreadingdemo.entities.BatchJob;
import com.gomezrondon.multithreadingdemo.entities.BatchJobId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface BatchJobRepository extends CrudRepository<BatchJob, BatchJobId> {

    @Query("SELECT c.batchId FROM BatchJob c ORDER BY c.batchId desc")
    BatchJob findLastId();

}
