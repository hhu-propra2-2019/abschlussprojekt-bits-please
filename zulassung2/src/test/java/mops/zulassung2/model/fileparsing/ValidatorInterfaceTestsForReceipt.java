package mops.zulassung2.model.fileparsing;

import mops.zulassung2.model.dataobjects.ReceiptData;
import mops.zulassung2.model.dataobjects.ReceiptDataInterface;
import mops.zulassung2.model.dataobjects.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidatorInterfaceTestsForReceipt {

  private File file;
  private FileWriter writer;
  private ValidatorInterface customValidatorInterface;
  private ReceiptDataInterface customReceiptDataInterface;

  @BeforeEach
  void setup() {
    Student student = new Student("9999999", "test@uni-duesseldorf.de", "meier", "max");
    String subject = "informatik";
    String semester = "zwei";

    customValidatorInterface = new Validator();
    customReceiptDataInterface = new ReceiptData(student, subject, semester);
    file = new File("file.txt");
    try {
      writer = new FileWriter(file, UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @AfterEach
  void deleteFile() {
    file.deleteOnExit();
  }

  @Test
  public void checkValidReceipt() throws IOException {

    writer.write(customReceiptDataInterface.create());
    writer.write("\nSignature");
    writer.close();

    String receiptContent = Files.readString(file.toPath(), UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());

    assertThat(customValidatorInterface.validateTXT(lines)).isTrue();
  }

  @Test
  public void checkInvalidIdentifierMatrNumber() throws IOException {

    writer.write(customReceiptDataInterface.create().replace("matriculationnumber", "matriculationnumbe"));
    writer.write("\nSignature");
    writer.close();

    String receiptContent = Files.readString(file.toPath(), UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());

    assertThat(customValidatorInterface.validateTXT(lines)).isFalse();
  }

  @Test
  public void checkInvalidIdentifierEmail() throws IOException {

    writer.write(customReceiptDataInterface.create().replace("email", "emil"));
    writer.write("\nSignature");
    writer.close();

    String receiptContent = Files.readString(file.toPath(), UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());

    assertThat(customValidatorInterface.validateTXT(lines)).isFalse();
  }

  @Test
  public void checkInvalidIdentifierName() throws IOException {

    writer.write(customReceiptDataInterface.create().replace(" name", " nae"));
    writer.write("\nSignature");
    writer.close();

    String receiptContent = Files.readString(file.toPath(), UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());

    assertThat(customValidatorInterface.validateTXT(lines)).isFalse();
  }

  @Test
  public void checkInvalidIdentifierForeName() throws IOException {

    writer.write(customReceiptDataInterface.create().replace("forename", "forname"));
    writer.write("\nSignature");
    writer.close();

    String receiptContent = Files.readString(file.toPath(), UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());

    assertThat(customValidatorInterface.validateTXT(lines)).isFalse();
  }

  @Test
  public void checkInvalidIdentifierModule() throws IOException {

    writer.write(customReceiptDataInterface.create().replace("module", "modul"));
    writer.write("\nSignature");
    writer.close();

    String receiptContent = Files.readString(file.toPath(), UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());

    assertThat(customValidatorInterface.validateTXT(lines)).isFalse();
  }

  @Test
  public void checkInvalidIdentifierSemester() throws IOException {

    writer.write(customReceiptDataInterface.create().replace("semester", "semestr"));
    writer.write("\nSignature");
    writer.close();

    String receiptContent = Files.readString(file.toPath(), UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());

    assertThat(customValidatorInterface.validateTXT(lines)).isFalse();
  }

  @Test
  public void checkInvalidIdentifierColon() throws IOException {

    writer.write(customReceiptDataInterface.create().replace(":", "-"));
    writer.write("\nSignature");
    writer.close();

    String receiptContent = Files.readString(file.toPath(), UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());

    assertThat(customValidatorInterface.validateTXT(lines)).isFalse();
  }

  @Test
  public void checkInvalidIdentifierSpace() throws IOException {

    writer.write(customReceiptDataInterface.create().replace(" ", "-"));
    writer.write("\nSignature");
    writer.close();

    String receiptContent = Files.readString(file.toPath(), UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());

    assertThat(customValidatorInterface.validateTXT(lines)).isFalse();
  }

  @Test
  public void checkInvalidSignatureMissing() throws IOException {

    writer.write(customReceiptDataInterface.create());
    writer.close();

    String receiptContent = Files.readString(file.toPath(), UTF_8);
    List<String> lines = receiptContent.lines().collect(Collectors.toList());

    assertThat(customValidatorInterface.validateTXT(lines)).isFalse();
  }
}