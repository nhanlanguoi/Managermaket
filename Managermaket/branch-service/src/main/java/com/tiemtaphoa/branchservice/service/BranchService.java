package com.tiemtaphoa.branchservice.service;

import com.tiemtaphoa.branchservice.model.Branch;
import com.tiemtaphoa.branchservice.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private static final Logger logger = LoggerFactory.getLogger(BranchService.class);
    @Autowired
    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    // kiểm tra chi nhánh đã tồn tại hay chưa
    public Branch createBranch(Branch branch) {
        if (branchRepository.existsByBranchCode(branch.getBranchCode())) {
            throw new IllegalArgumentException("Mã chi nhánh " + branch.getBranchCode() + " đã tồn tại.");
        }
        return branchRepository.save(branch);
    }

    // trả về địa chỉ chi nhánh giựa theo id
    public Optional<Branch> getBranchById(Long id) {
    logger.info("SERVICE: Tìm kiếm chi nhánh với ID: {}", id); // Thêm log
    Optional<Branch> branch = branchRepository.findById(id);
    if (branch.isPresent()) {
        logger.info("SERVICE: Tìm thấy chi nhánh: {} với ID: {}", branch.get().getName(), id); // Thêm log
    } else {
        logger.warn("SERVICE: Không tìm thấy chi nhánh với ID: {}", id); // Thêm log
    }
    return branch;
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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi nhánh với ID: " + id));

        if (!branch.getBranchCode().equals(branchDetails.getBranchCode()) &&
                branchRepository.existsByBranchCode(branchDetails.getBranchCode())) {
            throw new IllegalArgumentException("Mã chi nhánh mới " + branchDetails.getBranchCode() + " đã tồn tại cho một chi nhánh khác.");
        }

        branch.setBranchCode(branchDetails.getBranchCode());
        branch.setName(branchDetails.getName());
        branch.setAddress(branchDetails.getAddress());
        branch.setCity(branchDetails.getCity());
        branch.setPhoneNumber(branchDetails.getPhoneNumber());
        branch.setActive(branchDetails.isActive()); // Cho phép cập nhật trạng thái active
        branch.setPhoneNumber(branchDetails.getPhoneNumber());
        branch.setActive(branchDetails.isActive());
        logger.info("Đang cập nhật chi nhánh ID: {} với thông tin mới...", id);
        Branch updatedBranch = branchRepository.save(branch);
        logger.info("Cập nhật thành công chi nhánh ID: {}", updatedBranch.getId());
        return updatedBranch;
    }

    // tìm chi nhánh theo id nếu không có thì báo lỗi và thực hiện việc xoá chi nhánh
    public void deleteBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("SERVICE: Lỗi khi xóa: Không tìm thấy chi nhánh với ID: {}", id); // Thêm log
                    return new IllegalArgumentException("Không tìm thấy chi nhánh với ID: " + id);
                });
        logger.info("SERVICE: Chuẩn bị xóa chi nhánh ID: {}, Tên: {}", id, branch.getName()); // Thêm log
        branchRepository.deleteById(id);
        logger.info("SERVICE: Đã xóa thành công chi nhánh ID: {}", id); // Thêm log
    }
}