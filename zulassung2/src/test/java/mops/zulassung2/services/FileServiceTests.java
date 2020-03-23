package mops.zulassung2.services;

import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.fileparsing.CustomCSVLineParser;
import mops.zulassung2.model.fileparsing.CustomValidator;
import mops.zulassung2.model.fileparsing.FileParser;
import mops.zulassung2.model.minio.CustomNameCreator;
import mops.zulassung2.model.minio.NameCreator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileServiceTests {
  private FileService fileService;
  private CustomValidator customValidator;
  private CustomCSVLineParser customCSVLineParser;
  private FileParser csvParser;
  private NameCreator nameCreator;
  private CustomNameCreator customNameCreator;
  private String receiptContent;
  private String signature;
  private ReceiptData receiptData;
  private Student louis = new Student("2835749", "loalf100@uni-duesseldorf.de", "alfares", "louis");
  private Student markus = new Student("2757144", "masie@hhu.de", "siewert", "markus");
  private String header = "matriculationnumber,email,name,forename\n";
  private String student1Data = "2727912,tigeu100@hhu.de,geuer,tim\n";
  private String student2Data = "2757144,masie@hhu.de,siewert,markus\n";

  @Test
  public void processCSVUploadTest() throws IOException {
    // Arrange
    fileService = new FileService();
    customValidator = new CustomValidator();
    customCSVLineParser = new CustomCSVLineParser();
    List<Student> students = new ArrayList<>();
    students.add(louis);
    students.add(markus);
    String data = header + student1Data + student2Data;
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    FileParser csvParser = new FileParser(customValidator, customCSVLineParser);
    MockMultipartFile file = new MockMultipartFile("testFile.csv", "test_students.csv",
        "text/csv", bytes);
    // Act
    List<Student> student = csvParser.processCSV(file);

    // Assert
    assertThat(fileService.processCSVUpload(file)).isEqualTo(student);
  }

  @Test
  public void checkinvalidCSVFormatTest() {
    // Arrange
    fileService = new FileService();
    customValidator = new CustomValidator();
    customCSVLineParser = new CustomCSVLineParser();
    List<Student> students = new ArrayList<>();
    students.add(louis);
    students.add(markus);
    String data = header + student1Data + student2Data;
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);

    FileParser csvParser = new FileParser(customValidator, customCSVLineParser);
    MockMultipartFile file = new MockMultipartFile("input-file", "test.txt",
        "text/txt", "test/data".getBytes());
    // Act
    List<Student> student = csvParser.processCSV(file);
    // Assert
    assertThat(fileService.processCSVUpload(file)).isEqualTo(student);
  }

  @Test
  public void checkprocessTXTUploadTest() {
    fileService = new FileService();
    customValidator = new CustomValidator();
    FileParser txtParser = new FileParser(customValidator);
    MockMultipartFile file = new MockMultipartFile("input-file", "test.txt",
        "input/txt", "test/data".getBytes());
    List<String> lines = txtParser.processTXT(file);
    assertThat(fileService.processTXTUpload(file)).isEqualTo(lines);

  }

  @Test
  public void readReceiptContentTest() {
    fileService = new FileService();
    String receiptContent = null;
    String[] dataObjects = receiptContent.split(" ");
    Student student = new Student(
        dataObjects[0].split(":")[1], // Matriculationnumber
        dataObjects[1].split(":")[1], // Email
        dataObjects[2].split(":")[1], // Name
        dataObjects[3].split(":")[1]); // Forename
    ReceiptData receiptData = new CustomReceiptData(student,
        dataObjects[4].split(":")[1], // Module
        dataObjects[5].split(":")[1], // Semester
        signature);                         // Signature

    assertThat(fileService.readReceiptContent(receiptContent, signature)).isEqualTo(receiptData);


  }

  @Test
  public void storeReceiptTest() {


  }


}
