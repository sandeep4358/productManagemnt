package com.test.controller;

import com.test.entity.FileUploadStatus;
import com.test.entity.Product;
import com.test.repository.FileUploadStatusRepository;
import com.test.repository.IBBODBRepository;
import com.test.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/readFile")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final IBBODBRepository ibbodbRepository;
    private final FileUploadStatusRepository fileUploadStatusRepository;

    @PostMapping(value = "/product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity<Object> saveProduct(@RequestParam(value = "files") MultipartFile[] files, @RequestParam(value = "fileName") String[] fileName) throws ExecutionException, InterruptedException {
        String processId = ibbodbRepository.getProcessId();
        // Store async tasks
        List<CompletableFuture<List<Product>>> futures = new ArrayList<>();

        for (MultipartFile file : files) {
            CompletableFuture<List<Product>> futureTask = productService.saveProduct(file, processId).exceptionally(ex -> {
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
                    .status("File upload successfully completed")
                    .createdAt(OffsetDateTime.now())
                    .fileName(finalFileName)
                    .updatedAt(OffsetDateTime.now())
                    .build();
            fileUploadStatusRepository.save(fileUploadStatus);
            log.info("FileUploadStatus saved for processId: {}", processId);
        });

        return ResponseEntity.status(HttpStatus.CREATED).body("File processing started");
    }

    @PostMapping(value = "/products", produces = "application/json")
    public CompletableFuture<ResponseEntity> findAllProduct() {
        return productService.findAllProducts().thenApply(ResponseEntity::ok);
    }
}
