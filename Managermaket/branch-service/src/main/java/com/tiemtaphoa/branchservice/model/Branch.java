package com.tiemtaphoa.branchservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity //đánh dấu class này là entity (thực thể) dùng để thể hiện đây là điểm đối chiếu với cơ sở dữ liệu 
@Table(name = "branches") // đặt tên bảng 
@Data // lombok tự sinh get set 
@NoArgsConstructor // lombok
@AllArgsConstructor //lombok
public class Branch {

    @Id //đánh dấu trường này là khóa chính 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tăng id tự động
    private Long id;

    @Column(unique = true, nullable = false)// không cho phép trống và null 
    private String branchCode; 

    @Column(nullable = false) 
    private String name; 
    private String address;
    private String city;
    private String phoneNumber;

    @Column(nullable = false)
    private boolean active = true; 
}