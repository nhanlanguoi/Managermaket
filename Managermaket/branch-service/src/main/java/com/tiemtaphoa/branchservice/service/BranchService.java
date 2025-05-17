package com.tiemtaphoa.branchservice.service;

import com.tiemtaphoa.branchservice.model.Branch;
import com.tiemtaphoa.branchservice.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    // kiểm tra chi nhánh đã tồn tại hay chưa
    public Branch createBranch(Branch branch) {
        if (branchRepository.existsByBranchCode(branch.getBranchCode())) {
            throw new IllegalArgumentException("Branch code " + branch.getBranchCode() + " already exists.");
        }
        return branchRepository.save(branch);
    }

    // trả về địa chỉ chi nhánh giựa theo id
    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    // trả về Optional<Branch> nếu tìm được theo branchcode
    public Optional<Branch> getBranchByBranchCode(String branchCode) {
        return branchRepository.findByBranchCode(branchCode);
    }

    // chỉ trả về chi nhánh đang hoạt động 
    public Optional<Branch> getActiveBranchByBranchCode(String branchCode) {
        return branchRepository.findByBranchCodeAndActiveTrue(branchCode);
    }

    // lấy toàn bộ chi nhánh
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    // lấy toàn bộ chi nhánh đang hoạt động 
    public List<Branch> getAllActiveBranches() {
        return branchRepository.findAllByActiveTrue();
    }

    // tìm kiếm chi nhanh theo id và save và kiểm tra xem có trùng với chi nhánh nào không ném ra lỗi 
    public Branch updateBranch(Long id, Branch branchDetails) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found with id: " + id));

        if (!branch.getBranchCode().equals(branchDetails.getBranchCode()) &&
                branchRepository.existsByBranchCode(branchDetails.getBranchCode())) {
            throw new IllegalArgumentException("New branch code " + branchDetails.getBranchCode() + " already exists for another branch.");
        }

        branch.setBranchCode(branchDetails.getBranchCode());
        branch.setName(branchDetails.getName());
        branch.setAddress(branchDetails.getAddress());
        branch.setCity(branchDetails.getCity());
        branch.setPhoneNumber(branchDetails.getPhoneNumber());
        branch.setActive(branchDetails.isActive()); // Cho phép cập nhật trạng thái active
        return branchRepository.save(branch);
    }

    // tìm chi nhánh theo id nếu không có thì báo lỗi và thực hiện việc xoá chi nhánh
    public void deleteBranch(Long id) { // Xóa ở đây có thể là xóa mềm (đặt active=false) hoặc xóa cứng tùy logic
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found with id: " + id));
        branchRepository.deleteById(id);
    }
}