package com.test.repository;


import com.test.entity.FileUploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileUploadStatusRepository extends JpaRepository<FileUploadStatus, Long> {

    // Find a file upload status record by the file name
    Optional<FileUploadStatus> findByFileName(String fileName);

    // Find all file upload status records by a specific status (e.g., "PENDING", "COMPLETED")
    List<FileUploadStatus> findByStatus(String status);
}

