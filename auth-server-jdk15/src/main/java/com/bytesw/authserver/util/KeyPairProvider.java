package com.bytesw.authserver.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Proveedor del par de claves RSA para firmar los JWT.
 * Genera un par de claves RSA de 2048 bits al iniciar la aplicación.
 */
@Component
public class KeyPairProvider {

    @Getter
    private final KeyPair keyPair;

    public KeyPairProvider() {
        try {
            // Genera un par de claves RSA de 2048 bits
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            this.keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generando par de claves RSA", e);
        }
    }
}
