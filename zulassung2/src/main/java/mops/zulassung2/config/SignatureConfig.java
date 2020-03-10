package mops.zulassung2.config;

import mops.zulassung2.model.crypto.KeyConverter;
import mops.zulassung2.model.crypto.KeyConverterInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

@Configuration
public class SignatureConfig {

  @Value("${dev_private_key}")
  String privateKey;
  @Value("${dev_public_key}")
  String publicKey;
  KeyConverterInterface keyConverterInterface;

  @Bean
  PublicKey getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
    keyConverterInterface = new KeyConverter();
    return keyConverterInterface.getPublicKey(publicKey);
  }

  @Bean
  PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
    keyConverterInterface = new KeyConverter();
    return keyConverterInterface.getPrivateKey(privateKey);
  }
}
