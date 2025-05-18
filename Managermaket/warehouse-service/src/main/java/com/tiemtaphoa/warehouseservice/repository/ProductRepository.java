package com.tiemtaphoa.warehouseservice.repository;

import com.tiemtaphoa.warehouseservice.model.Product;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {
    // ElasticsearchRepository<Product, String> nghĩa là repository này làm việc với
    // document Product và kiểu dữ liệu của ID là String.

    
    Optional<Product> findByProductId(String productId);
    /*  giải thích : Tìm các sản phẩm (Product) mà trong danh sách branchesInventory
     (một trường dạng nested) có một item nào đó có branchId chính xác bằng giá trị truyền vào (branchId)*/
    @Query("{\"nested\": {\"path\": \"branchesInventory\", \"query\": {\"term\": {\"branchesInventory.branchId.keyword\": \"?0\"}}}}")
    List<Product> findByBranchIdInInventory(String branchId); 
}