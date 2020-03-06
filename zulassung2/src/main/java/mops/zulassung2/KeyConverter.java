package mops.zulassung2;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyConverter {

  public PrivateKey getPrivateKey(String privateKeyAsString) throws NoSuchAlgorithmException, InvalidKeySpecException {
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKeyAsString);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
    return keyFactory.generatePrivate(keySpec);
  }

  public PublicKey getPublicKey(String publicKeyAsString) throws NoSuchAlgorithmException, InvalidKeySpecException {
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    byte[] decodedPublicKey = Base64.getDecoder().decode(publicKeyAsString);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedPublicKey);
    return keyFactory.generatePublic(keySpec);
  }

}
