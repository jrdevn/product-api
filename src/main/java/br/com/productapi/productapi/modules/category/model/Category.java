package br.com.productapi.productapi.modules.category.model;

import br.com.productapi.productapi.modules.category.dto.CategoryRequest;
import br.com.productapi.productapi.modules.supplier.dto.SupplierRequest;
import br.com.productapi.productapi.modules.supplier.model.Supplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CATEGORY")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name="DESCRIPTION", nullable = false)
    private String description;

    // É um parse para copiar as propriedades da Request para a Response.
    public static Category of(CategoryRequest categoryRequest) {
        var category = new Category();
        BeanUtils.copyProperties(categoryRequest, category);
        return category;
    }
}
