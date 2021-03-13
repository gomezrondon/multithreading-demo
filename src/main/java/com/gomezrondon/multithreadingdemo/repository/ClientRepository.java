package com.gomezrondon.multithreadingdemo.repository;

import com.gomezrondon.multithreadingdemo.entities.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientRepository extends CrudRepository<Client, Long> {


    @Query("SELECT c.clientCode FROM Client c")
    List<Long> findAllClientId();

    @Query("SELECT c.salary FROM Client c where c.clientCode between :ini and :end")
    List<Double> findClientSalaryRange(@Param("ini") long ini, @Param("end") long end);

}
