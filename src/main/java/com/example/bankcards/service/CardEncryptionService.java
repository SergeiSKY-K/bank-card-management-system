package com.example.bankcards.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Service
public class CardEncryptionService {

    private static final String ALGORITHM = "AES";

    @Value("${encryption.secret-key}")
    private String secretKey;

    public String encrypt(String cardNumber) {
        try {
            SecretKeySpec key = new SecretKeySpec(
                    secretKey.getBytes(),
                    ALGORITHM
            );

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(cardNumber.getBytes());

            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting card number", e);
        }
    }
}