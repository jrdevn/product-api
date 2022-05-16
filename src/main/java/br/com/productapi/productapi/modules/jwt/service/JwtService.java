package br.com.productapi.productapi.modules.jwt.service;

import br.com.productapi.productapi.config.exception.AuthenticationException;
import br.com.productapi.productapi.modules.jwt.dto.JwtResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class JwtService {

    private static final String EMPTY_SPACE = " ";
    private static final Integer TOKEN_INDEX = 1;

    @Value("${app-config.secrets.api-secret}") // pega do application.yml

    private String apiSecret;

    public void validateAthorization(String token) {
        var accessToken = extractToken(token);
        try {
            var claims = Jwts
                    .parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(apiSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(accessToken) // o signingket com o secret do auth api que é a chave de 64 bits
                    .getBody();

            var user = JwtResponse.getUser(claims);
            if (isEmpty(user) || isEmpty(user.getId())) {
                throw new AuthenticationException("User is not valid.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AuthenticationException("Error while trying to proccess the access token");
        }
    }

   private String extractToken(String token) {
        if (isEmpty(token)) {
            throw new AuthenticationException("Access token was not informed!");
        }
        if (token.toLowerCase().contains(EMPTY_SPACE)) {
            return token.split(EMPTY_SPACE)[TOKEN_INDEX];
        }
        return token;
    }
}
