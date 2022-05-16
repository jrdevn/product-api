package br.com.productapi.productapi.modules.category.dto;

import br.com.productapi.productapi.modules.category.model.Category;
import br.com.productapi.productapi.modules.supplier.model.Supplier;
import lombok.Data;
import org.springframework.beans.BeanUtils;

// O que irá retornar do result da API.
@Data
public class CategoryResponse {

    private Integer id;

    private String description;

    // É um parse para copiar as propriedades e retornar a response conforme o model base, no model Category também tem o inverso.
    public static CategoryResponse of(Category category) {
        var response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }
}
