package mops.zulassung2;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class KeyConverter {

  public SecretKey getPrivateKey(String privateKeyAsString) {
    byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKeyAsString);
    SecretKey secretPrivateKey = new SecretKeySpec(decodedPrivateKey, 0, decodedPrivateKey.length, "RSA");
    return secretPrivateKey;
  }

  public SecretKey getPublicKey(String publicKeyAsString) {
    byte[] decodedPublicKey = Base64.getDecoder().decode(publicKeyAsString);
    SecretKey secretPublicKey = new SecretKeySpec(decodedPublicKey, 0, decodedPublicKey.length, "RSA");
    return secretPublicKey;
  }

}
