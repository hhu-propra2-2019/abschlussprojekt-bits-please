package mops.zulassung2.model.crypto;

import mops.Zulassung2Application;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;

class Encryption implements EncryptionInterface {

  private static final Logger logger = LoggerFactory.getLogger(Zulassung2Application.class);

  public byte[] encrypt(String toEncrypt, PrivateKey privateKey) {
    try {
      SecureRandom secureRandom = new SecureRandom();
      Signature signature = Signature.getInstance("SHA256WithRSA");

      signature.initSign(privateKey, secureRandom);

      byte[] data = Base64.decodeBase64(toEncrypt);
      signature.update(data);

      return signature.sign();

    } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
      logger.error(e.getMessage());
      return null;
    }

  }

  public boolean decrypt(String toDecrypt, String toVerify, PublicKey publicKey) {
    try {
      Signature signature = Signature.getInstance("SHA256WithRSA");

      signature.initVerify(publicKey);

      byte[] data = Base64.decodeBase64(toDecrypt);
      signature.update(data);

      byte[] digitalSignature = Base64.decodeBase64(toVerify);
      return signature.verify(digitalSignature);

    } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
      logger.error(e.getMessage());
      return false;
    }
  }

}
