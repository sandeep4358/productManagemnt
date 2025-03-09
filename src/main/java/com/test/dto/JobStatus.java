package com.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.test.entity.Product;

import java.util.List;

@Data
@AllArgsConstructor
public class JobStatus {
    private String jobId;
    private String status; // PROCESSING, COMPLETED, FAILED
    private List<Product> productList;
    private String errorMessage;

    public JobStatus(String jobId, String status, List<Product> productList) {
        this.jobId = jobId;
        this.status = status;
        this.productList = productList;
    }
}
