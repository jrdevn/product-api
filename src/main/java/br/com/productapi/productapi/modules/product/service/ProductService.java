package br.com.productapi.productapi.modules.product.service;

import br.com.productapi.productapi.config.exception.SuccessResponse;
import br.com.productapi.productapi.config.exception.ValidationException;
import br.com.productapi.productapi.modules.category.service.CategoryService;
import br.com.productapi.productapi.modules.product.dto.*;
import br.com.productapi.productapi.modules.product.model.Product;
import br.com.productapi.productapi.modules.product.repository.ProductRepository;
import br.com.productapi.productapi.modules.sales.client.SalesClient;
import br.com.productapi.productapi.modules.sales.dto.SalesConfirmationDTO;
import br.com.productapi.productapi.modules.sales.dto.SalesProductResponse;
import br.com.productapi.productapi.modules.sales.enums.SalesStatus;
import br.com.productapi.productapi.modules.sales.rabbitmq.SalesConfirmationSender;
import br.com.productapi.productapi.modules.supplier.service.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;   
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.productapi.productapi.config.RequestUtil.getCurrentRequest;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class ProductService {

    private static final String TRANSACTION_ID = "transactionid";
    private static final String SERVICE_ID = "serviceid";

    private static final Integer ZERO = 0;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SalesConfirmationSender salesConfirmationSender;

    @Autowired
    private SalesClient salesClient;

    public List<ProductResponse> findByName(String name) {
        if (isEmpty(name)) {
            throw new ValidationException("The product name must be informed!");
        }
        return productRepository
                .findByName(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findAll() {
        return productRepository
                .findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findByIdResponse(Integer id) {
        return ProductResponse.of(findById(id));
    }

    public Product findById(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("The product Id was not informed");
        }
        return productRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Product not found!"));
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId) {
        if (isEmpty(supplierId)) {
            throw new ValidationException("The supplier Id must be informed!");
        }
        return productRepository
                .findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryId) {
        if (isEmpty(categoryId)) {
            throw new ValidationException("The category Id must be informed!");
        }
        return productRepository
                .findByCategoryId(categoryId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse save(ProductRequest productRequest) {
        validateProductDataInformed(productRequest);
        validateCategoryAndSupplierId(productRequest);
        var category = categoryService.findById(productRequest.getCategoryId());
        var supplier = supplierService.findById(productRequest.getSupplierId());
        var product = productRepository.save(Product.of(productRequest, supplier, category));
        return ProductResponse.of(product); // irá retornar o product response conforme o produto salvo no banco.
    }

    private void validateProductDataInformed(ProductRequest productRequest) {
        if (isEmpty(productRequest.getName())) {
            throw new ValidationException("The product name was not informed!");
        }

        if (isEmpty(productRequest.getQuantityAvailable())) {
            throw new ValidationException("The quantity was not informed!");
        }

        if ((productRequest.getQuantityAvailable()) <= ZERO) {
            throw new ValidationException("The quantity should not be less or equal to zero!");
        }
    }

    private void validateCategoryAndSupplierId(ProductRequest productRequest) {
        if (isEmpty(productRequest.getCategoryId())) {
            throw new ValidationException("The category id was not informed!");
        }

        if (isEmpty(productRequest.getSupplierId())) {
            throw new ValidationException("The supplier id was not informed!");
        }
    }

    public Boolean existsByCategoryId(Integer categoryId) {
        return productRepository.existsByCategoryId(categoryId);
    }

    public Boolean existsBySupplierId(Integer supplierId) {
        return productRepository.existsBySupplierId(supplierId);
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if (!productRepository.existsById(id)) {
            throw new ValidationException("Product does not exists!");
        }
        var sales = getSalesByProductId(id);
        if (!isEmpty(sales.getSalesIds())) {
            throw new ValidationException("The product cannot be deleted. There are sales for it!");
        }
        productRepository.deleteById(id);
        return SuccessResponse.create("The product was deleted!");
    }

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("The Product Id must be informed!");
        }
    }

    public ProductResponse update(ProductRequest productRequest, Integer id) {
        validateProductDataInformed(productRequest);
        validateCategoryAndSupplierId(productRequest);
        validateInformedId(id);
        var category = categoryService.findById(productRequest.getCategoryId());
        var supplier = supplierService.findById(productRequest.getSupplierId());
        var product = Product.of(productRequest, supplier, category);
        product.setId(id);
        productRepository.save(product);
        return ProductResponse.of(product);
    }

    @Transactional
    private void updateStock(ProductStockDTO products) {
        var productsForUpdate = new ArrayList<Product>();
        products.getProducts()
                .forEach(salesProduct -> {
                    var existingProduct = findById(salesProduct.getProductId()); // produto do bd
                    validateQuantitiyInStock(salesProduct, existingProduct);
                    existingProduct.updateStock(salesProduct.getQuantity());
                    productsForUpdate.add(existingProduct);
                });
        if (!isEmpty(productsForUpdate)) {
            productRepository.saveAll(productsForUpdate);
            var approvedMessage = new SalesConfirmationDTO(products.getSalesId(), SalesStatus.APROVED, products.getTransactionid());
            salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
        }
    }

    private void validateQuantitiyInStock(ProductQuantityDTO salesProduct, Product existingProduct) {
        if (salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
            throw new ValidationException(
                    String.format("The product %s is out of stock", existingProduct.getId())
            );
        }
    }

    public void updateProductStock(ProductStockDTO products) {
        try {
            validateStockUpdateData(products);
            updateStock(products);
        } catch (Exception ex) {
            log.error("Error while trying to update stock for message with error: {}", ex.getMessage(), ex);
            var rejectedMessage = new SalesConfirmationDTO(products.getSalesId(), SalesStatus.REJECTED, products.getTransactionid());
            salesConfirmationSender.sendSalesConfirmationMessage(rejectedMessage);
        }
    }

    private void validateStockUpdateData(ProductStockDTO product) {
        if (isEmpty(product) || isEmpty(product.getSalesId())) {
            throw new ValidationException("The product data or sales Id cannot be null.");
        }
        if (isEmpty(product.getProducts())) {
            throw new ValidationException("The sales products cannot be null");
        }
        product
            .getProducts()
            .forEach(salesProduct -> {
                if (isEmpty(salesProduct.getProductId()) || isEmpty(salesProduct.getQuantity())) {
                    throw new ValidationException("The productId or quantity must be informed!");
                }
            });
    }

    public ProductSalesResponse findProductSales(Integer id) { // serviço que irá conectar com o serviço externo
        var product = findById(id);
        var sales = getSalesByProductId(product.getId());
        return ProductSalesResponse.of(product, sales.getSalesIds());
    }

    private SalesProductResponse getSalesByProductId(Integer productId) {
        try {
            var currentRequest = getCurrentRequest();
            var transactionId = currentRequest.getHeader(TRANSACTION_ID);
            var serviceId = currentRequest.getAttribute(SERVICE_ID);
            log.info("Sending GET request to orders by productId with data: {} | [transactionId: {} | serviceId: {}]",
                    productId, transactionId, serviceId);
            var response =   salesClient
                    .findSalesByProductId(productId)
                    .orElseThrow(() -> new ValidationException("The sales was not found by this product."));
            log.info("Receive response from orders by productId with data: {} | [transactionId: {} | serviceId: {}]",
                    new ObjectMapper().writeValueAsString(response), transactionId, serviceId);
            return response;
        } catch (Exception ex) {
            throw new ValidationException("There was an error trying to get the product's sales");
        }
    }
    
    public SuccessResponse checkProductStock(ProductCheckStockRequest request) {
        try {
            var currentRequest = getCurrentRequest();
            var transactionId = currentRequest.getHeader(TRANSACTION_ID);
            var serviceId = currentRequest.getAttribute(SERVICE_ID);
            log.info("Request to POST with product stock data: {} | [transactionId: {} | serviceId: {}]",
                    new ObjectMapper().writeValueAsString(request), transactionId, serviceId);
            if (isEmpty(request)) {
                throw new ValidationException("The request data must be informed!");
            }
            request
                    .getProducts()
                    .forEach(this::validateStock); // pra cada elemento no array ele entra na função
            var response  =  SuccessResponse.create("The stock is ok!");
            log.info("Response to POST with product stock data: {} | [transactionId: {} | serviceId: {}]",
                    new ObjectMapper().writeValueAsString(response), transactionId, serviceId);
            return response;
        } catch (Exception ex ){
            throw new ValidationException(ex.getMessage());
        }

    }

    private void validateStock(ProductQuantityDTO productQuantity) {
        if (isEmpty(productQuantity.getProductId()) || isEmpty(productQuantity.getQuantity())) {
            throw new ValidationException("Product ID or quantity must be informed!");
        }
        var product = findById(productQuantity.getProductId());
        if (productQuantity.getQuantity() > product.getQuantityAvailable()) {
            throw new ValidationException(String.format("The product %s is out of stock!", product.getId()));
        }
    }
}
