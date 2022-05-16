package br.com.productapi.productapi.config;

import br.com.productapi.productapi.config.exception.ValidationException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {
    public static  HttpServletRequest getCurrentRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder //CASTING NO REQUEST PARA RECUPERAR A REQUEST E A AUTENTICAÇÃO, FEITO PRO SALES - SERVIÇO EXTERNO
                    .getRequestAttributes())
                    .getRequest();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ValidationException("The current request could not be processed!");
        }
    }
}
