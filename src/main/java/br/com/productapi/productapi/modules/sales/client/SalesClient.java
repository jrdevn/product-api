package br.com.productapi.productapi.modules.sales.client;

import br.com.productapi.productapi.modules.sales.dto.SalesProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
// interface que chama um serviço externo, ou seja, se chamar esse metodo da interface é possivel navegar para achar as vendas
// pelo productid.
@FeignClient(
        name = "salesClient",
        contextId = "salesClient",
        url = "${app-config.services.sales}"
)
public interface SalesClient {
    @GetMapping("/api/orders/product/{productId}")
    Optional<SalesProductResponse> findSalesByProductId(@PathVariable Integer productId);
}
