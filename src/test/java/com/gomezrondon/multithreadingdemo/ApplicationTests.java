package com.gomezrondon.multithreadingdemo;

import com.gomezrondon.fakedata.utils.BackupServiceKt;
import com.gomezrondon.multithreadingdemo.entities.Client;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

//@SpringBootTest
class ApplicationTests {

    @Test
    public void test4() {

        List<Client> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(new Client(1l, "4521-4152", "javier gomez", 451264.2, "S"));
        }

        BackupServiceKt.manageH2BackupWrite("test",list );
        List<Client> clientList =  BackupServiceKt.manageH2BackupRead("test" );

        Assert.assertEquals("1000 iguales 1000", clientList.size(), 1000 );
    }


    @Test
    @DisplayName("Testing deleting backups files")
    public void testDeletingBackups() {

        //setup
        List<Client> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(new Client(1l, "4521-4152", "javier gomez", 451264.2, "S"));
        }
        BackupServiceKt.manageH2BackupWrite("test",list );

        //having
        List<Boolean> test = BackupServiceKt.deleteFilesStartWith("test");

        //then
        long count = test.stream().filter(exist -> !exist).count();

        Assert.assertEquals("1000 iguales 1000", 10, count );
    }

}
