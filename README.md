# multithreading-demo
A Multithreading java demo with Spring boot, JPA, H2, JavaFaker and Kotlin

## 1 - Clone this repo to your machine
## 2 - Install gradle to run the commands

### inside the project create the folder "backup"

## 3 - To do a normal run, generate 100k fake clients and calculate their avg salary
 
```
gradle -Dprofile=normal -Drecords=100000 bootRun
```
### This will generate the next results
```
============ Core Count:    8    ============    // number of cores of the machine to split the work
============ Total Records: 100000 ============  
============ Chunk size: 1250.0    ============  // size of the chuck to per commit
============ Restore backup: false    ============  // if false it will generate fake data, if true it will read from the last backup generated
============ Trigger Error: false    ============ // triggers an error to test recovery

 =============== Split Work =========    

 WorkRange{iniRange=0, endRange=12499}
 WorkRange{iniRange=12500, endRange=24999}
 WorkRange{iniRange=25000, endRange=37499}
 WorkRange{iniRange=37500, endRange=49999}
 WorkRange{iniRange=50000, endRange=62499}
 WorkRange{iniRange=62500, endRange=74999}
 WorkRange{iniRange=75000, endRange=87499}
 WorkRange{iniRange=87500, endRange=99999}
 
 =============== Assign Work =========   // creates a work table with status 'I'
BatchJob{batchId=1, threadId=1, description='processing clients', rangeIni=0, rangeEnd=12499, lastElement=null, recordCount=12500, salaryTotalSum=0.00, status='I'}
BatchJob{batchId=1, threadId=2, description='processing clients', rangeIni=12500, rangeEnd=24999, lastElement=null, recordCount=12500, salaryTotalSum=0.00, status='I'}
BatchJob{batchId=1, threadId=3, description='processing clients', rangeIni=25000, rangeEnd=37499, lastElement=null, recordCount=12500, salaryTotalSum=0.00, status='I'}
BatchJob{batchId=1, threadId=4, description='processing clients', rangeIni=37500, rangeEnd=49999, lastElement=null, recordCount=12500, salaryTotalSum=0.00, status='I'}
BatchJob{batchId=1, threadId=5, description='processing clients', rangeIni=50000, rangeEnd=62499, lastElement=null, recordCount=12500, salaryTotalSum=0.00, status='I'}
BatchJob{batchId=1, threadId=6, description='processing clients', rangeIni=62500, rangeEnd=74999, lastElement=null, recordCount=12500, salaryTotalSum=0.00, status='I'}
BatchJob{batchId=1, threadId=7, description='processing clients', rangeIni=75000, rangeEnd=87499, lastElement=null, recordCount=12500, salaryTotalSum=0.00, status='I'}
BatchJob{batchId=1, threadId=8, description='processing clients', rangeIni=87500, rangeEnd=99999, lastElement=null, recordCount=12500, salaryTotalSum=0.00, status='I'}

 =============== Start Work =========  // each thread take its payload and start to work

Working Thread: pool-1-thread-4 Calculating avg salary...
Working Thread: pool-1-thread-5 Calculating avg salary...
Working Thread: pool-1-thread-8 Calculating avg salary...
Working Thread: pool-1-thread-1 Calculating avg salary...
Working Thread: pool-1-thread-7 Calculating avg salary...
Working Thread: pool-1-thread-3 Calculating avg salary...
Working Thread: pool-1-thread-6 Calculating avg salary...
Working Thread: pool-1-thread-2 Calculating avg salary...


========= Multi-Thread Calculation AVG Salary:  147903.18   // This is the result of all the workers threads. all ended with status "F" (Finalized)
BatchJob{batchId=1, threadId=2, description='processing clients', rangeIni=12500, rangeEnd=24999, lastElement=24999, recordCount=12500, salaryTotalSum=1841765740.47, status='F'}
BatchJob{batchId=1, threadId=3, description='processing clients', rangeIni=25000, rangeEnd=37499, lastElement=37499, recordCount=12500, salaryTotalSum=1849026845.22, status='F'}
BatchJob{batchId=1, threadId=4, description='processing clients', rangeIni=37500, rangeEnd=49999, lastElement=49999, recordCount=12500, salaryTotalSum=1847224787.82, status='F'}
BatchJob{batchId=1, threadId=5, description='processing clients', rangeIni=50000, rangeEnd=62499, lastElement=62499, recordCount=12500, salaryTotalSum=1851035622.29, status='F'}
BatchJob{batchId=1, threadId=6, description='processing clients', rangeIni=62500, rangeEnd=74999, lastElement=74999, recordCount=12500, salaryTotalSum=1840724546.17, status='F'}
BatchJob{batchId=1, threadId=7, description='processing clients', rangeIni=75000, rangeEnd=87499, lastElement=87499, recordCount=12500, salaryTotalSum=1850983382.51, status='F'}
BatchJob{batchId=1, threadId=8, description='processing clients', rangeIni=87500, rangeEnd=99999, lastElement=99999, recordCount=12500, salaryTotalSum=1850290113.79, status='F'}

========= Validating Multi-Thread Calculation total: 147903.18  // this is a validation reading all the salaries directly and averaging

*************** Finishing backup of tables **********************  // a backup is done to provide a fast estar next time if needed.

```

## 4 - To test the backup 
 
```
gradle -Dprofile=normal -Drecords=100000 -Drestore=true bootRun
```
### This will generate the next results
```
=========== Restoring Batch table  =========
```


## 5 - To test an Error
 
```
gradle -Dprofile=normal -Drecords=100000 -Derror=true bootRun
```
### This will generate the next results
```
2021-03-13 09:47:55.536  INFO 6928 --- [           main] c.g.multithreadingdemo.appconfig.Config  : =============== Start Work =========
2021-03-13 09:47:55.536  INFO 6928 --- [           main] c.g.multithreadingdemo.appconfig.Config  :
2021-03-13 09:47:56.555  INFO 6928 --- [pool-1-thread-5] c.g.m.entities.ProcessSalaryWorker       : Working Thread: pool-1-thread-5 Calculating avg salary...
2021-03-13 09:47:56.555  INFO 6928 --- [pool-1-thread-1] c.g.m.entities.ProcessSalaryWorker       : Working Thread: pool-1-thread-1 Calculating avg salary...
2021-03-13 09:47:56.558  INFO 6928 --- [pool-1-thread-8] c.g.m.entities.ProcessSalaryWorker       : Working Thread: pool-1-thread-8 Calculating avg salary...
2021-03-13 09:47:56.590  INFO 6928 --- [pool-1-thread-7] c.g.m.entities.ProcessSalaryWorker       : Working Thread: pool-1-thread-7 Calculating avg salary...
2021-03-13 09:47:56.615  INFO 6928 --- [pool-1-thread-6] c.g.m.entities.ProcessSalaryWorker       : Working Thread: pool-1-thread-6 Calculating avg salary...
2021-03-13 09:47:56.643  INFO 6928 --- [pool-1-thread-4] c.g.m.entities.ProcessSalaryWorker       : Working Thread: pool-1-thread-4 Calculating avg salary...
2021-03-13 09:47:56.664  INFO 6928 --- [pool-1-thread-3] c.g.m.entities.ProcessSalaryWorker       : Working Thread: pool-1-thread-3 Calculating avg salary...
java.util.concurrent.ExecutionException: java.lang.RuntimeException: Something when wrong!!
        at java.base/java.util.concurrent.FutureTask.report(FutureTask.java:122)
*
*
*************** One or More thread ended with Error  ********************** // thread id = 2 end with status "Error"


BatchJob{batchId=1, threadId=1, description='processing clients', rangeIni=0, rangeEnd=12499, lastElement=12499, recordCount=12500, salaryTotalSum=1852951109.46, status='F'}
BatchJob{batchId=1, threadId=2, description='processing clients', rangeIni=12500, rangeEnd=24999, lastElement=13750, recordCount=12500, salaryTotalSum=185147460.22, status='E'}
BatchJob{batchId=1, threadId=3, description='processing clients', rangeIni=25000, rangeEnd=37499, lastElement=37499, recordCount=12500, salaryTotalSum=1852911304.41, status='F'}
BatchJob{batchId=1, threadId=4, description='processing clients', rangeIni=37500, rangeEnd=49999, lastElement=49999, recordCount=12500, salaryTotalSum=1843411251.58, status='F'}
BatchJob{batchId=1, threadId=5, description='processing clients', rangeIni=50000, rangeEnd=62499, lastElement=62499, recordCount=12500, salaryTotalSum=1845300235.93, status='F'}
BatchJob{batchId=1, threadId=6, description='processing clients', rangeIni=62500, rangeEnd=74999, lastElement=74999, recordCount=12500, salaryTotalSum=1861115126.18, status='F'}
BatchJob{batchId=1, threadId=7, description='processing clients', rangeIni=75000, rangeEnd=87499, lastElement=87499, recordCount=12500, salaryTotalSum=1853271593.78, status='F'}
BatchJob{batchId=1, threadId=8, description='processing clients', rangeIni=87500, rangeEnd=99999, lastElement=99999, recordCount=12500, salaryTotalSum=1841103768.54, status='F'}

*************** Finishing backup of tables **********************

```

## 5 - Then we Retry the last Run
 
```
gradle -Dprofile=retry -Drecords=100000 bootRun
```
### This will generate the next results
```
=============== Start Work =========   // this time only 1 thread started

Working Thread: pool-1-thread-1 Calculating avg salary...

========= Multi-Thread Calculation AVG Salary:  147940.0096761   // the work was correctly calculated

BatchJob{batchId=1, threadId=1, description='processing clients', rangeIni=0, rangeEnd=12499, lastElement=12499, recordCount=12500, salaryTotalSum=1852951109.46, status='F'}
BatchJob{batchId=1, threadId=2, description='processing clients', rangeIni=13751, rangeEnd=24999, lastElement=24999, recordCount=12500, salaryTotalSum=1843936577.73, status='F'} // correct status
BatchJob{batchId=1, threadId=3, description='processing clients', rangeIni=25000, rangeEnd=37499, lastElement=37499, recordCount=12500, salaryTotalSum=1852911304.41, status='F'}
BatchJob{batchId=1, threadId=4, description='processing clients', rangeIni=37500, rangeEnd=49999, lastElement=49999, recordCount=12500, salaryTotalSum=1843411251.58, status='F'}
BatchJob{batchId=1, threadId=5, description='processing clients', rangeIni=50000, rangeEnd=62499, lastElement=62499, recordCount=12500, salaryTotalSum=1845300235.93, status='F'}
BatchJob{batchId=1, threadId=6, description='processing clients', rangeIni=62500, rangeEnd=74999, lastElement=74999, recordCount=12500, salaryTotalSum=1861115126.18, status='F'}
BatchJob{batchId=1, threadId=7, description='processing clients', rangeIni=75000, rangeEnd=87499, lastElement=87499, recordCount=12500, salaryTotalSum=1853271593.78, status='F'}
BatchJob{batchId=1, threadId=8, description='processing clients', rangeIni=87500, rangeEnd=99999, lastElement=99999, recordCount=12500, salaryTotalSum=1841103768.54, status='F'}

========= Validating Multi-Thread Calculation total: 147940.01

```
