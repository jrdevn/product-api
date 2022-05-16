package br.com.productapi.productapi.modules.category.controller;

import br.com.productapi.productapi.config.exception.SuccessResponse;
import br.com.productapi.productapi.modules.category.dto.CategoryRequest;
import br.com.productapi.productapi.modules.category.dto.CategoryResponse;
import br.com.productapi.productapi.modules.category.service.CategoryService;
import br.com.productapi.productapi.modules.supplier.dto.SupplierRequest;
import br.com.productapi.productapi.modules.supplier.dto.SupplierResponse;
import br.com.productapi.productapi.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping()
    public CategoryResponse save(@RequestBody CategoryRequest categoryRequest) {
        var categoryResponse =  categoryService.save(categoryRequest);
        return categoryResponse;
    }

    @GetMapping("") // é o get padrão da requisição
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("{id}") // no mapeamento é só colocar api/category/1001
    public CategoryResponse findById(@PathVariable Integer id) {
        return categoryService.findByIdResponse(id);
    }

    @GetMapping("description/{description}") // nao pode colocar apenas description, tem que por um router na api ex: description/"oi"
    public List<CategoryResponse> findByDescription(@PathVariable String description) {
        return categoryService.findByDescription(description.toString());
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return categoryService.delete(id);
    }

    @PutMapping("{id}")
    public CategoryResponse update(@PathVariable Integer id, @RequestBody CategoryRequest categoryRequest) {
        return categoryService.update(categoryRequest, id);
    }


}
