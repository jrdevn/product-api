package br.com.productapi.productapi.modules.supplier.service;

import br.com.productapi.productapi.config.exception.SuccessResponse;
import br.com.productapi.productapi.config.exception.ValidationException;
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
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductService productService;


    public Supplier findById(Integer id) {
        validateInformedId(id);
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Supplier not found!"));
    }

    public List<SupplierResponse> findByName(String name) {
        if (isEmpty(name)) {
            throw new ValidationException("The supplier name must be informed!");
        }
        return supplierRepository
                .findByName(name)
                .stream()
                .map(SupplierResponse::of) // collection na lista para mapear cada Category da lista para um CategoryResponse
                .collect(Collectors.toList());
    }


    public List<SupplierResponse> findAll() {
        return supplierRepository
                .findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse findByIdResponse(Integer id) {
        return SupplierResponse.of(findById(id));
    }


    public SupplierResponse save(SupplierRequest supplierRequest) {
        validateSupplierNameInformed(supplierRequest);
        var supplier = supplierRepository.save(Supplier.of(supplierRequest));
        return SupplierResponse.of(supplier);
    }

    private void validateSupplierNameInformed(SupplierRequest supplierRequest) {
        if (isEmpty(supplierRequest.getName())) {
            throw new ValidationException("The supplier name was not informed!");
        }
    }

    public SuccessResponse delete (Integer id) {
        validateInformedId(id);
        if (productService.existsBySupplierId(id)) {
            throw new ValidationException("You cannot delete this supplier because it exists.");
        } else {
            supplierRepository.deleteById(id);
            return SuccessResponse.create("The supplier was delete!");
        }
    }

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("The Supplier Id must be informed!");
        }
    }

    public SupplierResponse update(SupplierRequest supplierRequest, Integer id) {
        validateSupplierNameInformed(supplierRequest);
        validateInformedId(id);
        var supplier = Supplier.of(supplierRequest);
        supplier.setId(id);
        supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }

}
