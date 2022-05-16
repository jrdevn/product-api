package br.com.productapi.productapi.modules.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductRequest {
    private String name;

    @JsonProperty("quantity_available")
    private Integer quantityAvailable;

    private Integer supplierId;
    private Integer categoryId;

    @JsonProperty("created_at")
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

}
