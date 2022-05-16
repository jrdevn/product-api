package br.com.productapi.productapi.modules.product.rabbitmq;

import br.com.productapi.productapi.modules.product.dto.ProductStockDTO;
import br.com.productapi.productapi.modules.product.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductStockListener {

    @Autowired
    private ProductService productService;

    @RabbitListener(queues = "${app-config.rabbit.queue.product-stock}") // é o que ta vindo de outra aplicação por essa fila
    public void receiveProductStockMessage(ProductStockDTO product) throws JsonProcessingException {
        log.info("Receive message with data: {} and transactionId: {}",
                new ObjectMapper().writeValueAsString(product), product.getTransactionid());
        productService.updateProductStock(product);
    }
}
