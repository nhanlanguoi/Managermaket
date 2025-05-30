package com.tiemtaphoa.warehouseservice.service;

import com.tiemtaphoa.warehouseservice.model.BranchInventory;
import com.tiemtaphoa.warehouseservice.model.Product;
import com.tiemtaphoa.warehouseservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID; 
import java.util.stream.Collectors;

@Service
public class ProductService {


    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Tạo sản phẩm mới. Nếu sản phẩm chưa có id, tạo một UUID ngẫu nhiên làm ID.
    public Product createProduct(Product product) {
        
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString()); 
        }
        
        return productRepository.save(product);
    }

    // lấy sản phẩm bằng id từ document của elasticsearch
    public Optional<Product> getProductByInternalId(String internalId) {
        // Tìm theo ID document của Elasticsearch (trường @Id)
        return productRepository.findById(internalId);
    }


    public Optional<Product> getProductByProductId(String productId) {
        // Tìm theo trường productId mà bạn định nghĩa
        return productRepository.findByProductId(productId);
    }


    public List<Product> getAllProducts() {
        // Iterable to List conversion
        List<Product> productList = new java.util.ArrayList<>();
        productRepository.findAll().forEach(productList::add);
        return productList;
    }

    public List<Product> getProductsByBranch(String branchCode) {
        logger.info("Đang lấy sản phẩm từ repository cho mã chi nhánh: {}", branchCode);
        List<Product> productsFound = productRepository.findByBranchIdInInventory(branchCode); // Gọi query đã sửa
        logger.info("Tìm thấy {} sản phẩm từ repository cho mã chi nhánh {}", productsFound.size(), branchCode);

        return productsFound.stream().map(product -> {
            Product productForBranch = new Product();
            productForBranch.setId(product.getId());
            productForBranch.setProductId(product.getProductId());
            productForBranch.setName(product.getName());
            productForBranch.setDescription(product.getDescription());
            productForBranch.setBasePrice(product.getBasePrice());
            productForBranch.setCategory(product.getCategory());

            // Vì query đã lọc sản phẩm có branchCode này, chúng ta chỉ cần lấy inventory đó
            product.getBranchesInventory().stream()
                    .filter(inv -> branchCode.equals(inv.getBranchId()))
                    .findFirst()
                    .ifPresentOrElse(
                            inv -> productForBranch.setBranchesInventory(List.of(inv)),
                            () -> productForBranch.setBranchesInventory(new ArrayList<>()) // Trường hợp này không nên xảy ra nếu query đúng
                    );
            return productForBranch;
        }).collect(Collectors.toList());
    }


    @Transactional
    public Optional<Product> updateProduct(String internalId, Product productDetails) {
        return productRepository.findById(internalId).map(existingProduct -> {
            existingProduct.setProductId(productDetails.getProductId());
            existingProduct.setName(productDetails.getName());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setBasePrice(productDetails.getBasePrice());
            existingProduct.setCategory(productDetails.getCategory());

            if (productDetails.getBranchesInventory() != null) {
                
                List<BranchInventory> newInventories = new ArrayList<>();
                // Sử dụng một Map để dễ dàng kiểm tra và cập nhật branchId đã tồn tại
                java.util.Map<String, BranchInventory> inventoryMap = new java.util.HashMap<>();

                
                
                // Xử lý các inventory được gửi từ form
                for (BranchInventory updatedInv : productDetails.getBranchesInventory()) {
                    if (updatedInv.getBranchId() != null && !updatedInv.getBranchId().trim().isEmpty()) {
                        
                        if (!inventoryMap.containsKey(updatedInv.getBranchId().trim())) {
                            inventoryMap.put(updatedInv.getBranchId().trim(), updatedInv);
                        } else {
                 
                            inventoryMap.put(updatedInv.getBranchId().trim(), updatedInv); // Ghi đè nếu trùng, lấy cái cuối cùng
                        }
                    }
                }
                newInventories.addAll(inventoryMap.values());
                existingProduct.setBranchesInventory(newInventories);

            } else {
                existingProduct.setBranchesInventory(new ArrayList<>());
            }

            logger.info("Cập nhật sản phẩm với ID nội bộ: {}. Danh sách tồn kho chi nhánh mới: {}", internalId, existingProduct.getBranchesInventory());
            return productRepository.save(existingProduct);
        });
    }

    @Transactional
    public boolean deleteProduct(String internalId) {
        if (productRepository.existsById(internalId)) {
            productRepository.deleteById(internalId);
            logger.info("Đã xóa sản phẩm với ID nội bộ: {}", internalId);
            return true;
        }
        logger.warn("Thất bại khi xóa sản phẩm không tồn tại với ID nội bộ: {}", internalId);
        return false;
    }

    public static class DecrementInventoryRequest {
        private String productId; // productId của sản phẩm
        private String branchId;  // branchId của chi nhánh
        private int quantityToDecrement;

        // Getters and Setters
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getBranchId() { return branchId; }
        public void setBranchId(String branchId) { this.branchId = branchId; }
        public int getQuantityToDecrement() { return quantityToDecrement; }
        public void setQuantityToDecrement(int quantityToDecrement) { this.quantityToDecrement = quantityToDecrement; }
    }

    @Transactional
    public Product decrementInventory(String productId, String branchId, int quantityToDecrement) {
        logger.info("Đang giảm tồn kho cho sản phẩm ID: {}, chi nhánh ID: {}, số lượng: {}", productId, branchId, quantityToDecrement);

        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy sản phẩm với productId: {}", productId);
                    return new IllegalArgumentException("Không tìm thấy sản phẩm với id: " + productId);
                });

        logger.info("Tìm sản phẩm: {}. Xem sản phẩm của chi nhánh: {}", product.getProductId(), branchId);
        if (product.getBranchesInventory() != null) {
            product.getBranchesInventory().forEach(inv -> logger.info("Tồn kho hiện có của sản phẩm {}: chi nhánh ID={}, số lượng={}", product.getProductId(), inv.getBranchId(), inv.getQuantity()));
        } else {
            logger.warn("Sản phẩm {} có danh sách tồn kho chi nhánh là null.", product.getProductId());
        }

        BranchInventory inventoryToUpdate = product.getBranchesInventory().stream()
                .filter(inv -> {
                    boolean match = branchId.equals(inv.getBranchId()); // So sánh branchId nhận từ request với branchId trong inventory
                    if (!match) {
                        logger.info("Tồn kho của chi nhánh ID '{}' trong sản phẩm {} không khớp với chi nhánh ID được yêu cầu '{}'", inv.getBranchId(), productId, branchId);
                    }
                    return match;
                })
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy chi nhánh: {} và id sản phẩm: {}", branchId, productId);
                    return new IllegalArgumentException("không tìm thấy chi nhánh: " + branchId + " và id sản phẩm: " + productId);
                });

        logger.info("Đã tìm và cập nhật sản phẩm {} tại chi nhánh {}: số lượng={}", productId, branchId, inventoryToUpdate.getQuantity());

        if (inventoryToUpdate.getQuantity() < quantityToDecrement) {
            String errorMessage = "Không đủ hàng cho sản phẩm " + productId + " tại chi nhánh " + branchId + ". Hiện có: " + inventoryToUpdate.getQuantity() + ", Yêu cầu: " + quantityToDecrement;
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        inventoryToUpdate.setQuantity(inventoryToUpdate.getQuantity() - quantityToDecrement);
       logger.info("Sản phẩm {} tại chi nhánh {} có số lượng mới sau khi giảm: {}", productId, branchId, inventoryToUpdate.getQuantity());
        return productRepository.save(product);
    }

    
}