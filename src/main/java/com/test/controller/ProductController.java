package com.test.controller;

import com.test.dto.JobStatus;
import com.test.entity.FileUploadStatus;
import com.test.entity.Product;
import com.test.repository.FileUploadStatusRepository;
import com.test.repository.IBBODBRepository;
import com.test.response.ProductJobResponse;
import com.test.response.ProductUploadResponse;
import com.test.response.StatusMessage;
import com.test.service.ProductService;
import com.test.service.asyncPooling.AsyncJobStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/readFile")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final IBBODBRepository ibbodbRepository;
    private final FileUploadStatusRepository fileUploadStatusRepository;

    @PostMapping(value = "/product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity<ProductUploadResponse> saveProduct(@RequestParam(value = "files") MultipartFile[] files, @RequestParam(value = "fileName") String[] fileName) throws ExecutionException, InterruptedException {
        String processId = ibbodbRepository.getProcessId();
        // Store async tasks
        List<CompletableFuture<List<Product>>> futures = new ArrayList<>();

        for (MultipartFile file : files) {
            CompletableFuture<List<Product>> futureTask = productService.saveProduct(file, processId,fileName).exceptionally(ex -> {
                String finalFileName = String.join(",", Arrays.asList(fileName));
                FileUploadStatus fileUploadStatus = FileUploadStatus.builder()
                        .processId(processId)
                        .description("File upload is in Failed")
                        .status("FAILED")
                        .createdAt(OffsetDateTime.now())
                        .fileName(finalFileName)
                        .updatedAt(OffsetDateTime.now())
                        .build();
                fileUploadStatusRepository.save(fileUploadStatus);
                log.error("Error processing file {}: {}", file.getOriginalFilename(), ex.getMessage());

                return List.of(); // Return empty list on failure

            });
            futures.add(futureTask);
        }

        // Wait for all async operations to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

       /*
       if you use below then it will collect the result of all the futures and return the list of list of products untill then there will be no return statement
       // Collect results after all tasks complete
        CompletableFuture<List<List<Product>>> resultFuture = allFutures.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join) // Collect results
                        .collect(Collectors.toList())
        );
        System.out.println(resultFuture.get()); // Output: [Apple, Banana, Cherry]
*/
        // When all async tasks are done, save FileUploadStatus
        allFutures.thenRun(() -> {
            String finalFileName = String.join(",", Arrays.asList(fileName));
            FileUploadStatus fileUploadStatus = FileUploadStatus.builder()
                    .processId(processId)
                    .description("File upload is in progress")
                    .status("IN_PROGRESS")
                    .createdAt(OffsetDateTime.now())
                    .fileName(finalFileName)
                    .updatedAt(OffsetDateTime.now())
                    .build();
            fileUploadStatusRepository.save(fileUploadStatus);
            log.info("FileUploadStatus saved for processId: {}", processId);
        });
        ProductUploadResponse response = ProductUploadResponse.builder().processId(processId).statusMessage(StatusMessage.builder().message("File processing initiated :").status("success").build()).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/products-search-async", produces = "application/json")
    public CompletableFuture<ResponseEntity<ProductJobResponse>> findAllProduct() {
        String jobId = UUID.randomUUID().toString(); // Generate a Unique Job ID
        AsyncJobStorage.createJob(jobId); // Initialize the job status
        productService.findAllProducts(jobId); // Start the async task
        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.ACCEPTED).body(ProductJobResponse.builder().jobId(jobId).statusMessage(StatusMessage.builder().message("Job Accepted").status("ACCEPTED").build()).build()));
    }

    //  Step 2: Poll for Job Status
    @GetMapping("/status/{jobId}")
    public JobStatus getJobStatus(@PathVariable String jobId) {
        log.info("Fetching job status for Job status: {}", AsyncJobStorage.getJobStatus(jobId));
        return AsyncJobStorage.getJobStatus(jobId);
    }

    // Step 3: Fetch Results Once Completed
    @GetMapping("/result/{jobId}")
    public List<Product> getJobResult(@PathVariable String jobId) {
        JobStatus jobStatus = AsyncJobStorage.getJobStatus(jobId);
        if (jobStatus != null && "COMPLETED".equals(jobStatus.getStatus())) {
            return jobStatus.getProductList();
        }
        throw new RuntimeException("Job not completed yet!");
    }
}
