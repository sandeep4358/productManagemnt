package com.test.service;

import com.test.entity.ErrorLog;
import com.test.entity.FileUploadStatus;
import com.test.entity.Product;
import com.test.repository.ErrorLogRepository;
import com.test.repository.FileUploadStatusRepository;
import com.test.repository.ProductRepository;
import com.test.service.asyncPooling.AsyncJobStorage;
import org.springframework.transaction.annotation.Transactional;;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ErrorLogRepository errorLogRepository;
    private final FileUploadStatusRepository fileUploadStatusRepository;

    @Async("taskExecutor")
    public CompletableFuture<List<Product>> saveProduct(MultipartFile file,String processId,String [] fileName) {
        log.info("Importing products from Excel - Process ID: {}", processId);

        return CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();

            try {
                // Parse CSV file
                List<Product> productList = parseCsvFile(file);
                log.info("Parsed {} products in thread: {}", productList.size(), Thread.currentThread().getName());

                // Save to database (transactional method)
                List<Product> savedProducts = saveAllProducts(productList);

                long end = System.currentTimeMillis();
                log.info("Total time taken for product save: {} ms", (end - start));
                String finalFileName = String.join(",", Arrays.asList(fileName));
                FileUploadStatus fileUploadStatus = FileUploadStatus.builder()
                        .processId(processId)
                        .description("File upload is in COMPLETED")
                        .status("COMPLETED")
                        .updatedAt(OffsetDateTime.now())
                        .build();
                fileUploadStatusRepository.save(fileUploadStatus);
                return savedProducts;
            } catch (Exception ex) {
                log.error("Error while processing file {}: {}", file.getOriginalFilename(), ex.getMessage());
                throw new RuntimeException("Failed to process file: " + ex.getMessage());            }
        });


    }

    @Transactional  // Runs in the main thread to ensure proper transaction handling
    public List<Product> saveAllProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

    /**
     * Fetch all products from the database
     * @return
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> findAllProducts(String jobId) {
        log.info("Fetching all products from the database - Job ID: {}", jobId);
        CompletableFuture.runAsync(() -> {
            try {
                try{
                    log.info("Sleeping for 90 seconds");
                    Thread.sleep(90_000);
                    log.info("Waking up after 90 seconds");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                List<Product> products = productRepository.findAll();
                AsyncJobStorage.completeJob(jobId, products); // once the search is complete it will store in the map
            } catch (Exception ex) {
                log.error("Error while fetching products: {}", ex.getMessage());
                AsyncJobStorage.failJob(jobId, ex.getMessage()); // once the search is complete it will store in the map
                throw new RuntimeException("Failed to fetch products: " + ex.getMessage());
            }
        });

        return CompletableFuture.completedFuture(null);

    }

    /*public List<Product> parseCsvFile(final MultipartFile file) {
        final List<Product> products = new ArrayList<>();
        try(final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int i=1;
            while ((line = br.readLine()) != null) {
                if (i>1) {
                final String[] data = line.split(",");
                final Product product = new Product();
                product.setName(data[0]);
                 product.setDescription(data[1]);
                product.setCategory(data[2]);
                product.setBrand(data[3]);
                product.setCreatedAt(OffsetDateTime.now());
                product.setUpdatedAt(OffsetDateTime.now());
                products.add(product);
                }
                i++;

            }
            return products;

        } catch (IOException e) {
            log.error("Failed to parse CSV file: {}", file.getOriginalFilename(), e);
            errorLogRepository.save(ErrorLog.builder()
                    .errorMessage("Failed to parse file: " + file.getOriginalFilename())
                    .errorCode("FILE_READ_ERROR")
                    .build());
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }


    }*/

    public List<Product> parseCsvFile(final MultipartFile file) {
        List<Product> products = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(br, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                try {
                    Product product = new Product();
                    product.setName(record.get("name"));  // Use column names instead of indexes
                    product.setDescription(record.get("description"));
                    product.setCategory(record.get("category"));
                    product.setBrand(record.get("brand"));
                    product.setCreatedAt(OffsetDateTime.now());
                    product.setUpdatedAt(OffsetDateTime.now());
                    products.add(product);
                } catch (Exception e) {
                    log.error("Error processing row: {}", record, e);
                    errorLogRepository.save(ErrorLog.builder()
                            .errorMessage("Error processing row: " + record.toString())
                            .errorCode("CSV_PARSE_ERROR")
                            .build());
                }
            }
            return products;

        } catch (IOException e) {
            log.error("Failed to parse CSV file: {}", file.getOriginalFilename(), e);
            errorLogRepository.save(ErrorLog.builder()
                    .errorMessage("Failed to parse file: " + file.getOriginalFilename())
                    .errorCode("FILE_READ_ERROR")
                    .build());
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }
}
