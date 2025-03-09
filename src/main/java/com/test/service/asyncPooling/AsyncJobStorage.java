package com.test.service.asyncPooling;

import com.test.dto.JobStatus;
import com.test.entity.Product;
import lombok.Data;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.test.entity.Product;

@Data
public class AsyncJobStorage  {
    private static final ConcurrentMap<String, JobStatus> jobs = new ConcurrentHashMap<>();

    public static void createJob(String jobId) {
        jobs.put(jobId, new JobStatus(jobId, "PROCESSING", null));
    }

    public static void completeJob(String jobId, List<Product> productList) {
        jobs.put(jobId, new JobStatus(jobId, "COMPLETED", productList));
    }

    public static void failJob(String jobId, String errorMessage) {
        jobs.put(jobId, new JobStatus(jobId, "FAILED", null, errorMessage));
    }

    public static JobStatus getJobStatus(String jobId) {
        return jobs.get(jobId);
    }
}
