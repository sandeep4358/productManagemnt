package com.test.dto
        ;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private String category;
    private int stock;
    private String brand;
    private String createdAt;
    private String updatedAt;
}