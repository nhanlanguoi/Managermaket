package com.tiemtaphoa.branchservice.controller;

import com.tiemtaphoa.branchservice.model.Branch;
import com.tiemtaphoa.branchservice.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController // @Controller and @ResponseBody
@RequestMapping("/api/branches") //  tất cả request đến /api/branches/** đều được xử lý trong controller này
@CrossOrigin(origins = "*") //Cho phép chấp nhận các request từ domain khác
public class BranchController {
    private static final Logger logger = LoggerFactory.getLogger(BranchController.class);
    private final BranchService branchService;

    @Autowired
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping // Thường dùng để tạo mới tài nguyên 
    public ResponseEntity<?> createBranch(@RequestBody Branch branch) {
        logger.info("CONTROLLER: Received request to create branch with code: {}", branch.getBranchCode());
        try {
            Branch createdBranch = branchService.createBranch(branch);
            logger.info("CONTROLLER: Branch {} created successfully with id: {}", createdBranch.getBranchCode(), createdBranch.getId());
            return new ResponseEntity<>(createdBranch, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("CONTROLLER: Error creating branch with code: {}. Error: {}", branch.getBranchCode(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping 
    public ResponseEntity<List<Branch>> getAllBranches(@RequestParam(required = false) Boolean active) {
        if (active != null && active) {
            return ResponseEntity.ok(branchService.getAllActiveBranches());
        }
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Branch> getBranchById(@PathVariable Long id) {
        return branchService.getBranchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/by-code/{branchCode}")
    public ResponseEntity<Branch> getBranchByBranchCode(@PathVariable String branchCode,
                                                        @RequestParam(required = false) Boolean activeOnly) {
        Optional<Branch> branchOptional;
        if (activeOnly != null && activeOnly) {
            branchOptional = branchService.getActiveBranchByBranchCode(branchCode);
        } else {
            branchOptional = branchService.getBranchByBranchCode(branchCode);
        }
        return branchOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}") // dùng cập nhật dữ liệu 
    public ResponseEntity<?> updateBranch(@PathVariable Long id, @RequestBody Branch branchDetails) {
        try {
            Branch updatedBranch = branchService.updateBranch(id, branchDetails);
            return ResponseEntity.ok(updatedBranch);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}") // Dùng để xóa tài nguyên
    public ResponseEntity<?> deleteBranch(@PathVariable Long id) {
        try {
            branchService.deleteBranch(id); 
            return ResponseEntity.ok("Branch with id " + id + " deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}