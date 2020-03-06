package mops.zulassung2;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class KeyConverter {

  @Value("${dev_private_key}")
  private String devPrivateKeyAsString;

  @Value("${dev_public_key}")
  private String devPublicKeyAsString;

  public SecretKey getPrivateKey() {
    byte[] decodedPrivateKey = Base64.getDecoder().decode(devPrivateKeyAsString);
    SecretKey secretPrivateKey = new SecretKeySpec(decodedPrivateKey, 0, decodedPrivateKey.length, "RSA");
    return secretPrivateKey;
  }

  public SecretKey getPublicKey() {
    byte[] decodedPublicKey = Base64.getDecoder().decode(devPublicKeyAsString);
    SecretKey secretPublicKey = new SecretKeySpec(decodedPublicKey, 0, decodedPublicKey.length, "RSA");
    return secretPublicKey;
  }

}
