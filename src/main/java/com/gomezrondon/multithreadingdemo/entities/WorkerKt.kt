package com.gomezrondon.fakedata.entities

import com.github.javafaker.Faker
import com.gomezrondon.multithreadingdemo.entities.Client
import com.gomezrondon.multithreadingdemo.entities.Worker
import com.gomezrondon.multithreadingdemo.repository.ClientRepository

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

class WorkerKt(val rowsPerBranch: Int = 0,
               val repository: ClientRepository
)  {

    fun work()  =  runBlocking {
     //   val faker = Faker(Locale.US)
        val lastRecord = rowsPerBranch -1
        val atomicId = AtomicLong(0)

        val time = measureTimeMillis {
            val jobs = (0..lastRecord).map {
                GlobalScope.async {
                    val salary = getRandomSalary(46000, 250000)
                    val client = Client(atomicId.getAndIncrement(), "13987482", "javier", salary)
                    client
                }//globalScope.async
            }

            val toList = jobs.awaitAll().toList()
            repository.saveAll(toList)
        }

        println("total time C: $time")

    }

    private fun getRandomSalary(min: Int, max: Int): Double {
        return (Math.random() * (max - min) + min)
    }


}