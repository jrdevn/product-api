package br.com.productapi.productapi.modules.supplier.controller;

import br.com.productapi.productapi.config.exception.SuccessResponse;
import br.com.productapi.productapi.modules.category.dto.CategoryRequest;
import br.com.productapi.productapi.modules.category.dto.CategoryResponse;
import br.com.productapi.productapi.modules.supplier.dto.SupplierRequest;
import br.com.productapi.productapi.modules.supplier.dto.SupplierResponse;
import br.com.productapi.productapi.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping()
    public SupplierResponse save(@RequestBody SupplierRequest supplierRequest) {
        var supplierResult =  supplierService.save(supplierRequest);
        return supplierResult;
    }

    @GetMapping("")
    public List<SupplierResponse> findAll() {
        return supplierService.findAll();
    }

    @GetMapping("{id}")
    public SupplierResponse findById(@PathVariable Integer id) {
        return supplierService.findByIdResponse(id);
    }

    @GetMapping("name/{name}")
    public List<SupplierResponse> findByName(@PathVariable String name) {
        return supplierService.findByName(name.toString());
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return supplierService.delete(id);
    }

    @PutMapping("{id}")
    public SupplierResponse update(@PathVariable Integer id, @RequestBody SupplierRequest supplierRequest) {
        return supplierService.update(supplierRequest, id);
    }

}
