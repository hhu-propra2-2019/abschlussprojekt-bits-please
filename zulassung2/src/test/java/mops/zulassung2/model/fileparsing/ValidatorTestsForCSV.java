package mops.zulassung2.model.fileparsing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidatorTestsForCSV {

  private static CustomValidator customValidator;

  @BeforeAll
  static void setup() {
    customValidator = new CustomValidator();
  }

  @Test
  public void checkValidCSV() throws IOException {
    final CSVParser parser = CSVParser.parse("9999999\ntest@uni-duesseldorf.de\nmeier\nmax",
        CSVFormat.DEFAULT.withHeader("matriculationnumber", "email", "name", "forename"));

    assertThat(customValidator.validateCSV(parser)).isTrue();
  }

  @Test
  public void checkInvalidMatriculationHeaderCSV() throws IOException {
    final CSVParser invalidMatriculation = CSVParser.parse(
        "9999999\ntest@uni-duesseldorf.de\nmeier\nmax",
        CSVFormat.DEFAULT.withHeader("matriculationumber", "email", "name", "forename"));

    assertThat(customValidator.validateCSV(invalidMatriculation)).isFalse();
  }

  @Test
  public void checkInvalidEmailHeaderCSV() throws IOException {
    final CSVParser invalidEmail = CSVParser.parse("9999999\ntest@uni-duesseldorf.de\nmeier\nmax",
        CSVFormat.DEFAULT.withHeader("matriculationnumber", "eail", "name", "forename"));

    assertThat(customValidator.validateCSV(invalidEmail)).isFalse();
  }

  @Test
  public void checkInvalidNameHeaderCSV() throws IOException {
    final CSVParser invalidName = CSVParser.parse("9999999\ntest@uni-duesseldorf.de\nmeier\nmax",
        CSVFormat.DEFAULT.withHeader("matriculationnumber", "email", "ame", "forename"));

    assertThat(customValidator.validateCSV(invalidName)).isFalse();
  }

  @Test
  public void checkInvalidForenameHeaderCSV() throws IOException {
    final CSVParser invalidForename = CSVParser.parse(
        "9999999\ntest@uni-duesseldorf.de\nmeier\nmax",
        CSVFormat.DEFAULT.withHeader("matriculationnumber", "email", "name", "frename"));

    assertThat(customValidator.validateCSV(invalidForename)).isFalse();
  }
}