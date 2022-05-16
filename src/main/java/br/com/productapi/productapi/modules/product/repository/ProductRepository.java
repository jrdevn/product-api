package br.com.productapi.productapi.modules.product.repository;

import br.com.productapi.productapi.modules.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByName(String name); // ele já reconhece o campo 'Name' do bd description e faz a query automaticamente.

    List<Product> findByCategoryId(Integer id);

    List<Product> findBySupplierId(Integer id);

    Boolean existsByCategoryId(Integer id);

    Boolean existsBySupplierId(Integer id);

}
