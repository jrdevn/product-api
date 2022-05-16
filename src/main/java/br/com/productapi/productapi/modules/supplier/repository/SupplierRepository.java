package br.com.productapi.productapi.modules.supplier.repository;

import br.com.productapi.productapi.modules.category.model.Category;
import br.com.productapi.productapi.modules.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    List<Supplier> findByName(String name); // ele já reconhece o campo 'Name' do bd description e faz a query automaticamente.

}
