package mops.zulassung2.model.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public interface KeyConverterInterface {
  PrivateKey getPrivateKey(String privateKeyAsString)
      throws NoSuchAlgorithmException, InvalidKeySpecException;

  PublicKey getPublicKey(String publicKeyAsString)
      throws NoSuchAlgorithmException, InvalidKeySpecException;
}
