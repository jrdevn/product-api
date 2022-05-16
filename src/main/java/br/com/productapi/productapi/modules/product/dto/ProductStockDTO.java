package br.com.productapi.productapi.modules.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductStockDTO {

    private String salesId; // isso vem da api de sales
    private List<ProductQuantityDTO> products;
    private String transactionid;

}
