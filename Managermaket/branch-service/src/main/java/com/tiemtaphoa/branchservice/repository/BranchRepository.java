package com.tiemtaphoa.branchservice.repository;

import com.tiemtaphoa.branchservice.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    // JpaRepository cung các phương thức truy vấn có sẵn .và các phương thức tự tạo để ánh xa đến database
    Optional<Branch> findByBranchCode(String branchCode);
    boolean existsByBranchCode(String branchCode);
    Optional<Branch> findByBranchCodeAndActiveTrue(String branchCode); 
    List<Branch> findAllByActiveTrue(); 
}