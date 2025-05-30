package com.tiemtaphoa.warehouseservice.controller;

import com.tiemtaphoa.warehouseservice.model.Product;
import com.tiemtaphoa.warehouseservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Đảm bảo import này

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/warehouse/products")
@CrossOrigin(
        origins = {"http://localhost:8000", "http://localhost:8002", "http://localhost:8003"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, // Cho phép các phương thức này
        allowedHeaders = "*" // Cho phép tất cả các header (hoặc chỉ định cụ thể nếu muốn chặt chẽ hơn)
)
public class WarehouseController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(WarehouseController.class);
    @Autowired
    public WarehouseController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // API để lấy sản phẩm theo productId (mã sản phẩm bạn tự đặt)
    @GetMapping("/by-product-id/{productId}")
    public ResponseEntity<Product> getProductByProductId(@PathVariable String productId) {
        Optional<Product> product = productService.getProductByProductId(productId);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // API để lấy sản phẩm theo ID document của Elasticsearch (trường @Id)
    @GetMapping("/{internalId}")
    public ResponseEntity<Product> getProductByInternalId(@PathVariable String internalId) {
        Optional<Product> product = productService.getProductByInternalId(internalId);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{internalId}")
    public ResponseEntity<Product> updateProduct(@PathVariable String internalId, @RequestBody Product productDetails) {
        Optional<Product> updatedProduct = productService.updateProduct(internalId, productDetails);
        return updatedProduct.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{internalId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String internalId) {
        if (productService.deleteProduct(internalId)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<Product>> getProductsByBranchId(@PathVariable String branchId) {
        List<Product> products = productService.getProductsByBranch(branchId); // Bạn sẽ cần tạo phương thức này trong ProductService
        if (products.isEmpty()) {
            // Quyết định trả về danh sách rỗng (200 OK) hay 204 No Content
            // Trả về danh sách rỗng thường tốt hơn cho client dễ xử lý
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(products);
    }



    @PostMapping("/inventory/decrement")
    public ResponseEntity<?> decrementProductInventory(@RequestBody ProductService.DecrementInventoryRequest request) {
        try {
            Product updatedProduct = productService.decrementInventory(
                    request.getProductId(),
                    request.getBranchId(),
                    request.getQuantityToDecrement()
            );
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            // Trả về một đối tượng JSON chứa thông báo lỗi
            // Điều này giúp frontend dễ dàng parse và hiển thị thông báo lỗi cụ thể hơn
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Lỗi khi giảm tồn kho cho sản phẩm: {} tại chi nhánh: {}", request.getProductId(), request.getBranchId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of("message", "Lỗi hệ thống khi giảm tồn kho: " + e.getMessage()));
        }
    }
}