package mops.zulassung2.model.crypto;

import mops.Zulassung2Application;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;

public class Encryption implements EncryptionInterface {

  private static final Logger logger = LoggerFactory.getLogger(Zulassung2Application.class);

  /**
   * Signs a given String with a given PrivateKey.
   *
   * @param toEncrypt  String to sign
   * @param privateKey Key to encrypt signature
   * @return signature of toEncrypt
   */
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

  /**
   * Checks the validity of the given string together with its
   * signature by using the given PublicKey for decryption.
   *
   * @param toDecrypt String to verify together with its signature
   * @param toVerify  signature of the String to be verified
   * @param publicKey Key to decrypt signature
   * @return true if the pair of text and signature is valid, otherwise false
   */
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
