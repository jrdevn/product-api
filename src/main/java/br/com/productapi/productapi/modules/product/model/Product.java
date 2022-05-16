package br.com.productapi.productapi.modules.product.model;

import br.com.productapi.productapi.modules.category.model.Category;
import br.com.productapi.productapi.modules.product.dto.ProductRequest;
import br.com.productapi.productapi.modules.supplier.model.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="PRODUCT")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name="NAME", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "FK_CATEGORY", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "FK_SUPPLIER", nullable = false)
    private Supplier supplier;

    @Column(name="QUANTITYAVAILABLE", nullable = false)
    private Integer quantityAvailable;

    @Column(name="CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public static Product of(ProductRequest productRequest, Supplier supplier, Category category) {
        return Product.
                builder()
                .name(productRequest.getName())
                .quantityAvailable(productRequest.getQuantityAvailable())
                .supplier(supplier)  // irá retornar o supplierResponse e o categoryResponse object.
                .category(category)
                .build();
    }

    public void updateStock(Integer quantity) {
        this.quantityAvailable = quantityAvailable - quantity;
    }
}
