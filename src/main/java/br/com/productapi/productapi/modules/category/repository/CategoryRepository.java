package br.com.productapi.productapi.modules.category.repository;

import br.com.productapi.productapi.modules.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer>    {

//    List<Category> findByDescriptionIgnoreCaseContaining(String description); // ele já reconhece o campo description e faz a query automaticamente.
      List<Category> findByDescription(String description); // ele já reconhece o campo description e faz a query automaticamente.

}
