package com.test.repository;

import com.test.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
    // Find all error logs with the specified error code
    List<ErrorLog> findByErrorCode(String errorCode);

    // Find all error logs for a given service
    List<ErrorLog> findByServiceName(String serviceName);

    // Additional query methods can be defined as needed
}
