package mops.zulassung2.services;

import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.fileparsing.CSVLineParser;
import mops.zulassung2.model.fileparsing.FileParser;
import mops.zulassung2.model.fileparsing.Validator;
import mops.zulassung2.model.minio.MinIoImplementation;
import mops.zulassung2.model.minio.MinIoRepository;
import mops.zulassung2.model.minio.MinIoRepositoryInterface;
import mops.zulassung2.model.minio.NameCreator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileServiceTests {
  private FileService fileService;
  private Validator validator;
  private CSVLineParser csvLineParser;
  private FileParser csvParser;
  private NameCreator nameCreator;
  private String receiptContent;
  private ReceiptData receiptData;
  private Student louis = new Student("2835749", "loalf100@uni-duesseldorf.de", "alfares", "louis");
  private Student markus = new Student("2757144", "masie@hhu.de", "siewert", "markus");
  private String header = "matriculationnumber,email,name,forename\n";
  private String student1Data = "2727912,tigeu100@hhu.de,geuer,tim\n";
  private String student2Data = "2757144,masie@hhu.de,siewert,markus\n";
  private MinIoImplementation minIo;
  @Value("${endpoint}")
  private String endpoint;
  @Value("${access_key}")
  private String accessKey;
  @Value("${secret_key}")
  private String secretKey;


  @Test
  public void processCSVUploadTest() throws IOException {
    // Arrange
    fileService = new FileService();
    validator = new Validator();
    csvLineParser = new CSVLineParser();
    List<Student> students = new ArrayList<>();
    students.add(louis);
    students.add(markus);
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
    fileService = new FileService();
    validator = new Validator();
    List<Student> students = new ArrayList<>();
    students.add(louis);
    students.add(markus);
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
    fileService = new FileService();
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
  public void storeReceiptTest() {
    File file = mock(File.class);
    Student student = new Student("9999999", "test@uni-duesseldorf.de",
        "meier", "max");
    MinIoRepositoryInterface repo = mock(MinIoRepository.class);
    MinIoRepository minIo = mock(MinIoRepository.class);
    NameCreator nameCreator = mock(NameCreator.class);
    fileService = new FileService();
    String bucketName = nameCreator.createBucketName(student);
    when(nameCreator.createBucketName(student)).thenReturn("test-student");
    when(file.getName()).thenReturn("test-file-name");
    when(file.getPath()).thenReturn("");
    when(file.length()).thenReturn(100L);
    // Act
    fileService.storeReceipt(student, file);

// Assert

  }


}
