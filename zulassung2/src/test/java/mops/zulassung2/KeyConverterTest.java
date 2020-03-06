package mops.zulassung2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;

class KeyConverterTest {

  @Value("${dev_private_key}")
  String privateKeyAsString;

  @Test
  void testThatPublicKeyIsConvertedCorrectlyFromString() {
    // Arrange
    KeyConverter keyConverter = new KeyConverter();

    // Act
    SecretKey privateKey = keyConverter.getPrivateKey(privateKeyAsString);

    // Assert
    String test = privateKey.
        getFormat();

    assertThat(privateKeyAsString).isEqualTo(privateKey.toString());


  }

}
