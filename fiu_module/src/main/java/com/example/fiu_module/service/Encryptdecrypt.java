package com.example.fiu_module.service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// import com.example.fiu_module.Config.KafkaProducer;

import java.security.MessageDigest;
import java.util.Base64;

@Service
public class Encryptdecrypt {

    // private static final Logger logger =
    // LoggerFactory.getLogger(Encryptdecrypt.class);

    final String SECRET_KEY = System.getenv("SECRET_KEY");
    final String ALGORITHM = "AES";
    final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    final int IV_SIZE = 16;

    // @Autowired
    // private KafkaProducer kafkaProducer;

    private String errorTopic = "aa_redirection_error";
    private String responseTopic = "aa_redirection_response";

    public String encryption(String strToEncrypt, String salt) {
        try {
            IvParameterSpec iv = new IvParameterSpec(new byte[IV_SIZE]);
            SecretKeySpec keySpec = generateKeyFromPassword(SECRET_KEY, salt);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));

            String encryptedString = Base64.getEncoder().encodeToString(encrypted);
            // kafkaProducer.sendMessage(responseTopic, encryptedString);
            // logger.info("Encryption successful: {}", encryptedString);
            return encryptedString;
        } catch (Exception e) {
            String error = "Encryption error: " + e.getMessage();
            // kafkaProducer.sendMessage(errorTopic, error);
            // logger.error(errorMessage, e);
            return null;
        }
    }

    public String decryption(String strToDecrypt, String salt) {
        try {
            IvParameterSpec iv = new IvParameterSpec(new byte[IV_SIZE]);
            SecretKeySpec keySpec = generateKeyFromPassword(SECRET_KEY, salt);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));

            String decryptedString = new String(decryptedBytes, "UTF-8");
            // kafkaProducer.sendMessage(responseTopic, decryptedString);
            // logger.info("Decryption successful: {}", decryptedString);
            return decryptedString;
        } catch (Exception e) {
            String error = "Decryption error: " + e.getMessage();
            // kafkaProducer.sendMessage(errorTopic, error);
            // logger.error(errorMessage, e);
            return null;
        }
    }

    private SecretKeySpec generateKeyFromPassword(String password, String salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = new byte[32];
        digest.update((password + salt).getBytes("UTF-8"));
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        // logger.info("Generated key from password and salt");
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
