package br.com.productapi.productapi.modules.category.service;

import br.com.productapi.productapi.config.exception.SuccessResponse;
import br.com.productapi.productapi.config.exception.ValidationException;
import br.com.productapi.productapi.modules.category.dto.CategoryRequest;
import br.com.productapi.productapi.modules.category.dto.CategoryResponse;
import br.com.productapi.productapi.modules.category.model.Category;
import br.com.productapi.productapi.modules.category.repository.CategoryRepository;
import br.com.productapi.productapi.modules.product.service.ProductService;
import br.com.productapi.productapi.modules.supplier.dto.SupplierRequest;
import br.com.productapi.productapi.modules.supplier.dto.SupplierResponse;
import br.com.productapi.productapi.modules.supplier.model.Supplier;
import br.com.productapi.productapi.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;


@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    public List<CategoryResponse> findByDescription(String description) {
        if (isEmpty(description)) {
            throw new ValidationException("The category description must be informed!");
        }
        return categoryRepository
                .findByDescription(description)
                .stream()
                .map(CategoryResponse::of) // collection na lista para mapear cada Category da lista para um CategoryResponse
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository
                .findAll()
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public CategoryResponse findByIdResponse(Integer id) {
        return CategoryResponse.of(findById(id));
    }

    public Category findById(Integer id) {
        validateInformedId(id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Category not found!"));
    }

    public CategoryResponse save(CategoryRequest categoryRequest) {
        validateCategoryDescriptionInformed(categoryRequest);
        var category = categoryRepository.save(Category.of(categoryRequest));
        return CategoryResponse.of(category);
    }

    private void validateCategoryDescriptionInformed(CategoryRequest categoryRequest) {
        if (isEmpty(categoryRequest.getDescription())) {
            throw new ValidationException("The category description was not informed!");
        }
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if (productService.existsByCategoryId(id)) {
            throw new ValidationException("You cannot delete this category because it exists.");
        } else {
            categoryRepository.deleteById(id);
            return SuccessResponse.create("The category was delete!");
        }
    }

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("The Category Id must be informed!");
        }
    }

    public CategoryResponse update(CategoryRequest categoryRequest, Integer id) {
        validateCategoryDescriptionInformed(categoryRequest);
        validateInformedId(id);
        var category = Category.of(categoryRequest);
        category.setId(id); // seta o id para ver q já existe e só faz upload
        categoryRepository.save(category);
        return CategoryResponse.of(category);
    }



}
