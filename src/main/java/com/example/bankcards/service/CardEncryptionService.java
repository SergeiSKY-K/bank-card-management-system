package com.example.bankcards.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class CardEncryptionService {

    private static final String ALGORITHM = "AES";

    private static final String SECRET_KEY = "1234567890123456";

    public String encrypt(String cardNumber) {
        try {
            SecretKeySpec key = new SecretKeySpec(
                    SECRET_KEY.getBytes(),
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
