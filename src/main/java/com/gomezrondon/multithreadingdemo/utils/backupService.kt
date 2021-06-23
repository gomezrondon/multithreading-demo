package com.gomezrondon.fakedata.utils

import com.gomezrondon.multithreadingdemo.appconfig.Config
import com.gomezrondon.multithreadingdemo.entities.BatchJob
import com.gomezrondon.multithreadingdemo.entities.Client
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.*

private val log = LoggerFactory.getLogger(Config::class.java)


val bactchBackUp = "backup/"+"Batch-backup.bcp"


fun splitListInChunks(list: List<Long>, chunk: Int): List<List<Long>> {
   return list.chunked(chunk)
}

fun deleteFilesStartWith(fileName: String): List<Boolean> {

    val toList = File("backup").walk().filter { it.isFile }.filter { it.name.startsWith(fileName) }
        .map {
            it.delete()
            it.exists()
        }.toList()

    return toList
}

fun manageH2BackupWrite(fileName: String, list: List<Client>) = runBlocking  {

    deleteFilesStartWith(fileName)

    val cunck = list.size/10
    val mapIndexed = list.chunked(cunck).mapIndexed { index, list ->
        GlobalScope.async {
            val name = "backup/" + fileName + "-" + index + ".bcp"
            list.map { it.clientCode.toString() + "," + it.id + "," + it.fullName + "," + it.salary + "," + it.status }
                .forEach {
                    File(name).appendText(it + "\n")
                }

            "done"
        }
    }
    mapIndexed.awaitAll()

}

fun manageH2BackupRead(fileName: String): List<Client> = runBlocking  {
    val list = (0..9).map { index ->
        async {
            val fileName1 = fileName + "-" + index + ".bcp"
            val clientCSV = restoreH2ClientCSV(fileName1)
     //       println(fileName1)
            clientCSV
        }
    }

    list.awaitAll().flatMap { it }.toList()
}


fun restoreH2ClientCSV(fileName: String):  List<Client> {
    val toList = File("backup/"+fileName).readLines()
        .filter { it.isNotBlank() }
        .map { it.split(",") }
        .map {
            Client(it.get(0).toLong(), it.get(1), it.get(2), it.get(3).toDouble(), it.get(4))
        }.toList()

    return toList
}


fun backUpH2BatchJob(list: List<BatchJob>) {
    val path = System.getProperty("user.dir")
    println("Working Directory = $path")

    val f = File(path, "backup")
    if (!f.exists()) {
        f.mkdir()
    }

    val fout = FileOutputStream(bactchBackUp)
    val oos = ObjectOutputStream(fout)
    oos.writeObject(list)
}

fun restoreH2BatchJob(): List<BatchJob> {
    var list = emptyList<BatchJob>()

     try {
         val finput = FileInputStream(bactchBackUp)
        val iis = ObjectInputStream(finput)
         list = iis.readObject() as List<BatchJob>
    } catch (e: FileNotFoundException) {
        log.error("File $bactchBackUp not found!!")
    }

    return list
}
