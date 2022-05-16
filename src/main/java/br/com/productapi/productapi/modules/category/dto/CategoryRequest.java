package br.com.productapi.productapi.modules.category.dto;

import lombok.Data;


// Request que é colhida do client.
@Data
public class CategoryRequest {

    private String description;
}
