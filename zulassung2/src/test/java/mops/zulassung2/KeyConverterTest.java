package mops.zulassung2;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


class KeyConverterTest {

  @Test
  void testEncryptionAndDecryption()
      throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException,
      InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
      UnsupportedEncodingException {

    //Arrange
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024);
    KeyPair keyPair = keyGen.generateKeyPair();
    String testPrivateKeyAsString = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
    String testPublicKeyAsString = Base64.encodeBase64String(keyPair.getPublic().getEncoded());

    KeyConverterInterface keyConverter = new KeyConverter();
    String message = "Das ist ein Test.";

    // Act
    PrivateKey privateKey = keyConverter.getPrivateKey(testPrivateKeyAsString);
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.ENCRYPT_MODE, privateKey);
    String encodedMessage = Base64.encodeBase64String(cipher.doFinal(message.getBytes("UTF-8")));

    PublicKey publicKey = keyConverter.getPublicKey(testPublicKeyAsString);
    cipher.init(Cipher.DECRYPT_MODE, publicKey);
    String resultMessage = new String(cipher.doFinal(Base64.decodeBase64(encodedMessage)), "UTF-8");

    // Assert
    assertThat(message).isEqualTo(resultMessage);
  }
}
