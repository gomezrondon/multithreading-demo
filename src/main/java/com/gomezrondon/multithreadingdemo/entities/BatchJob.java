package com.gomezrondon.multithreadingdemo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="batch_work")
@IdClass(BatchJobId.class)
public class BatchJob implements Serializable {

    @Id
    @Column(name = "batch_code")
    private Long batchId;
    @Id
    @Column(name = "thread_id")
    private Long threadId;
    @Column(name = "batch_desc")
    private String description;
    @Column(name = "init")
    private Long rangeIni;
    @Column(name = "endvalue")
    private Long rangeEnd;
    @Column(name = "last_element")
    private Long lastElement;
    @Column(name = "record_count")
    private Long recordCount;
    @Column(name = "salary")
    private BigDecimal salaryTotalSum;
    @Column(name = "status")
    private String status;

    public BatchJob() {
        this.salaryTotalSum = BigDecimal.ZERO;
        this.recordCount = 0l;
    }

    public BatchJob(Long batchId, Long threadId, String description) {
        this();
        this.batchId = batchId;
        this.threadId = threadId;
        this.description = description;
    }

    public BatchJob(Long batchId, Long threadId, String description, Long rangeIni, Long rangeEnd, String status) {
        this(batchId, threadId, description);
        this.rangeIni = rangeIni;
        this.rangeEnd = rangeEnd;
        this.status = status;
    }

    public BatchJob(Long batchId, Long threadId, String description, Long rangeIni, Long rangeEnd, Long lastElement, String status) {
        this(batchId, threadId, description, rangeIni, rangeEnd, status);
        this.lastElement = lastElement;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    public BigDecimal getSalaryTotalSum() {
        return salaryTotalSum;
    }

    public void setSalaryTotalSum(BigDecimal sum) {
        this.salaryTotalSum = sum;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRangeIni() {
        return rangeIni;
    }

    public void setRangeIni(Long rangeIni) {
        this.rangeIni = rangeIni;
    }

    public Long getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(Long rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public Long getLastElement() {
        return lastElement;
    }

    public void setLastElement(Long lastElement) {
        this.lastElement = lastElement;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BatchJob{" +
                "batchId=" + batchId +
                ", threadId=" + threadId +
                ", description='" + description + '\'' +
                ", rangeIni=" + rangeIni +
                ", rangeEnd=" + rangeEnd +
                ", lastElement=" + lastElement +
                ", recordCount=" + recordCount +
                ", salaryTotalSum=" + salaryTotalSum +
                ", status='" + status + '\'' +
                '}';
    }
}
