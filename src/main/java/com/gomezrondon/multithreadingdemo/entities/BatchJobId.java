package com.gomezrondon.multithreadingdemo.entities;


import java.io.Serializable;
import java.util.Objects;

public class BatchJobId implements Serializable {

    private Long batchId;
    private Long threadId;

    public BatchJobId() {
    }

    public BatchJobId(Long batchId, Long threadId) {
        this.batchId = batchId;
        this.threadId = threadId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchJobId that = (BatchJobId) o;
        return batchId.equals(that.batchId) && threadId.equals(that.threadId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchId, threadId);
    }

    @Override
    public String toString() {
        return "BatchJobId{" +
                "batchId=" + batchId +
                ", threadId=" + threadId +
                '}';
    }
}
