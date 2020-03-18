package mops.zulassung2;

import static java.nio.charset.StandardCharsets.UTF_8;

import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.fileparsing.CustomValidator;
import mops.zulassung2.services.CustomReceiptData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidatorTests {
  private CustomValidator customValidator;
  private Student student;
  private CustomReceiptData customReceiptData;

  @Test
  public void validateTextTest() throws IOException {
    customValidator = new CustomValidator();
    student = new Student("9999999", "test@uni-duesseldorf.de", "meier", "max");
    String currentSubject = "informatik";
    String semster = "zwei";
    customReceiptData = new CustomReceiptData(student, currentSubject, semster);
    File file = new File("test.txt");
    FileWriter writer = null;
    try {
      writer = new FileWriter(file, UTF_8);
      writer.write(customReceiptData.create());
      writer.write("\n.");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      //close resources
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    String receiptContent = Files.readString(file.toPath(), StandardCharsets.UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());
    assertThat(customValidator.validateTXT(lines)).isTrue();
  }

  @Test
  public void validateCSVTest() throws IOException {
    customValidator = new CustomValidator();
    final CSVParser parser = CSVParser.parse("9999999\ntest@uni-duesseldorf.de\nmeier\nmax",
        CSVFormat.DEFAULT.withHeader("matriculationnumber", "email", "name", "forename"));
    assertThat(customValidator.validateCSV(parser)).isTrue();
  }
}