package com.bytesw.authserver;

import com.bytesw.authserver.util.KeyPairProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@Slf4j
@SpringBootApplication
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

    /**
     * Imprime la clave pública al iniciar
     */
    @Bean
    public CommandLineRunner printPublicKey(KeyPairProvider keyPairProvider, PasswordEncoder passwordEncoder) {
        return args -> {
            RSAPublicKey publicKey = (RSAPublicKey) keyPairProvider.getKeyPair().getPublic();
            String publicKeyPEM = convertToPEM(publicKey);
            
            log.info("\n\n" +
                    "=================================================================\n" +
                    "                    CLAVE PÚBLICA JWT                            \n" +
                    "=================================================================\n" +
                    "{}\n" +
                    "=================================================================\n" +
                    "Copia esta clave y guárdala en: src/main/resources/public-key.pem\n" +
                    "En tu Resource Server (agreement-service, etc.)\n" +
                    "=================================================================\n" +
                    "\n" +
                    "📋 Endpoints disponibles:\n" +
                    "   - POST http://localhost:9000/oauth/token (obtener token)\n" +
                    "   - GET  http://localhost:9000/oauth/token_key (obtener clave pública)\n" +
                    "\n" +
                    "👤 Clientes configurados:\n" +
                    "   - agreement-service:secret (client_credentials, password)\n" +
                    "   - web-app:web-secret (authorization_code)\n" +
                    "\n" +
                    "🔐 Usuario de prueba:\n" +
                    "   - username: admin\n" +
                    "   - password: password\n" +
                    "=================================================================\n",
                    publicKeyPEM);
        };
    }

    private String convertToPEM(RSAPublicKey publicKey) {
        byte[] encoded = publicKey.getEncoded();
        String base64 = Base64.getEncoder().encodeToString(encoded);
        
        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN PUBLIC KEY-----\n");
        
        // Divide en líneas de 64 caracteres
        int index = 0;
        while (index < base64.length()) {
            pem.append(base64, index, Math.min(index + 64, base64.length()));
            pem.append("\n");
            index += 64;
        }
        
        pem.append("-----END PUBLIC KEY-----");
        return pem.toString();
    }
}
