package mops.zulassung2.services;

import mops.zulassung2.model.crypto.Receipt;
import mops.zulassung2.model.dataobjects.ReceiptData;
import mops.zulassung2.model.dataobjects.ReceiptDataInterface;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.fileparsing.CSVLineParser;
import mops.zulassung2.model.fileparsing.FileParser;
import mops.zulassung2.model.fileparsing.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileServiceTests {

  static SignatureService signatureService = mock(SignatureService.class);
  private Student hans = new Student("2444111", "hapet100@hhu.de", "peter", "hans");

  private FileService fileService;
  private Validator validator;
  private CSVLineParser csvLineParser;
  private ReceiptData receiptData;
  private Student max = new Student("2779911", "mamus100@hhu.de", "mustermann", "max");
  private Student paul = new Student("2779912", "pareh100@hhu.de", "reha", "paul");
  private String header = "matriculationnumber,email,name,forename\n";
  private String student1Data = "2727912,tigeu100@hhu.de,geuer,tim\n";
  private String student2Data = "2757144,masie@hhu.de,siewert,markus\n";


  @Test
  public void processCSVUploadTest() throws IOException {
    // Arrange
    fileService = new FileService(mock(SignatureService.class));
    validator = new Validator();
    csvLineParser = new CSVLineParser();
    List<Student> students = new ArrayList<>();
    students.add(max);
    students.add(paul);
    String data = header + student1Data + student2Data;
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    FileParser csvParser = new FileParser(validator, csvLineParser);
    MockMultipartFile file = new MockMultipartFile("testFile.csv", "test_students.csv",
        "text/csv", bytes);
    // Act
    List<Student> student = csvParser.processCSV(file);

    // Assert
    assertThat(fileService.processCSVUpload(file)).isEqualTo(student);
  }


  @Test
  public void checkprocessTXTUploadTest() {
    // Arrange
    fileService = new FileService(mock(SignatureService.class));
    validator = new Validator();
    List<Student> students = new ArrayList<>();
    students.add(max);
    students.add(paul);
    String data = header + student1Data + student2Data;
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    FileParser txtParser = new FileParser(validator);
    MockMultipartFile file = new MockMultipartFile("input-file", "test.txt",
        "input/txt", bytes);
    // Act
    List<String> lines = txtParser.processTXT(file);
    // Assert
    assertThat(fileService.processTXTUpload(file)).isEqualTo(lines);

  }


  @Test
  public void readReceiptContentTest() {
    //Arrange
    fileService = new FileService(mock(SignatureService.class));
    String expectedMatriculationnumber = "9999999";
    String expectedEmail = "test@uni-duesseldorf.de";
    String expectedName = "meier";
    String expectedForename = "max";
    Student student = new Student(expectedMatriculationnumber, expectedEmail, expectedName, expectedForename);
    String subject = "informatik";
    String semester = "zwei";
    String signature = "abcdefghe";
    receiptData = new ReceiptData(student, subject, semester, signature);

    // ACT
    String receiptContent = receiptData.create();

    // Assert
    ReceiptDataInterface actualData = fileService.readReceiptContent(receiptContent, signature);
    assertThat(actualData.getMatriculationNumber()).isEqualTo(expectedMatriculationnumber);
    assertThat(actualData.getEmail()).isEqualTo(expectedEmail);
    assertThat(actualData.getName()).isEqualTo(expectedName);
    assertThat(actualData.getForeName()).isEqualTo(expectedForename);

  }

  @Test
  void test_createFile() throws IOException {
    // Arrange
    fileService = new FileService(signatureService);
    ReceiptDataInterface receiptDataInterface = mock(ReceiptDataInterface.class);
    String data = "matriculationnumber:" + hans.getMatriculationNumber()
        + " email:" + hans.getEmail()
        + " name:" + hans.getName()
        + " forename:" + hans.getForeName()
        + " module:" + "informatik"
        + " semester:" + "2019SoSe"
        + "\n"
        + "signatur";
    Receipt receipt = mock(Receipt.class);
    when(receiptDataInterface.getModule()).thenReturn("informatik");
    when(receiptDataInterface.getName()).thenReturn("blank");
    when(receipt.getSignature()).thenReturn("signatur");
    Receipt receipt1 = new Receipt(data, "signatur");
    when(signatureService.sign(anyString())).thenReturn(receipt1);

    // Act
    File file = fileService.createFileFromUI(hans, "informatik", "2019SoSe");
    String content = Files.readString(file.toPath());

    // Assert
    assertEquals(data, content);
    Files.deleteIfExists(file.toPath());
  }

}
