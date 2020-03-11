package mops.zulassung2;

import mops.zulassung2.model.fileparsing.CustomCSVLineParser;
import mops.zulassung2.model.fileparsing.CustomValidator;
import mops.zulassung2.model.fileparsing.FileParser;
import mops.zulassung2.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParseFileTests {

  private Student tim = new Student("2727912", "tigeu100@hhu.de", "geuer", "tim");
  private Student markus = new Student("2757144", "masie@hhu.de", "siewert", "markus");
  private String header = "matriculationnumber,email,name,forname\n";
  private String student1Data = "2727912,tigeu100@hhu.de,geuer,tim\n";
  private String student2Data = "2757144,masie@hhu.de,siewert,markus\n";

  @Test
  public void testParsingStudentsFromCSV() {
    // Arrange
    List<Student> students = new ArrayList<>();

    students.add(tim);
    students.add(markus);

    String data = header + student1Data + student2Data;
    FileParser parser = new FileParser(new CustomValidator(), new CustomCSVLineParser());
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    String name = "testFile.csv";
    String fileName = "test_students.csv";
    MockMultipartFile csvFile = new MockMultipartFile(name, fileName, "text/csv", bytes);

    // Act
    List<Student> parsedStudents = parser.processCSV(csvFile);

    // Assert
    assertThat(parsedStudents).isEqualTo(students);
  }

  @Test
  public void testParsingInvalidCSVFile() {
    // Arrange
    String data = student1Data + student2Data;
    FileParser parser = new FileParser(new CustomValidator(), new CustomCSVLineParser());
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    String name = "testFile.csv";
    String fileName = "test_students.csv";
    MockMultipartFile csvFile = new MockMultipartFile(name, fileName, "text/csv", bytes);

    // Act
    List<Student> parsedStudents = parser.processCSV(csvFile);

    // Assert
    assertThat(parsedStudents).isEqualTo(null);
  }

  @Test
  public void testParsingCSVFileWithIncorrectlyNamedCollumns() {
    // Arrange
    String wrongNamedCollumnsHeader = "matricl,mail,nam,fornam\n";
    String data = wrongNamedCollumnsHeader + student1Data + student2Data;
    FileParser parser = new FileParser(new CustomValidator(), new CustomCSVLineParser());
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    String name = "testFile.csv";
    String fileName = "test_students.csv";
    MockMultipartFile csvFile = new MockMultipartFile(name, fileName, "text/csv", bytes);

    // Act
    List<Student> parsedStudents = parser.processCSV(csvFile);

    // Assert
    assertThat(parsedStudents).isEqualTo(null);
  }

  @Test
  public void testParsingCSVFileWithIncorrectSequence() {
    // Arrange
    String incorrectSequenceHeader = "forname,name,email,matriculationnumber\n";
    String data = incorrectSequenceHeader + student1Data + student2Data;
    FileParser parser = new FileParser(new CustomValidator(), new CustomCSVLineParser());
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    String name = "testFile.csv";
    String fileName = "test_students.csv";
    MockMultipartFile csvFile = new MockMultipartFile(name, fileName, "text/csv", bytes);

    // Act
    List<Student> parsedStudents = parser.processCSV(csvFile);

    // Assert
    assertThat(parsedStudents).isEqualTo(null);
  }

  @Test
  public void testSizeOfStudentList() {
    // Arrange
    String data = header;
    FileParser parser = new FileParser(new CustomValidator(), new CustomCSVLineParser());
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    String name = "testFile.csv";
    String fileName = "test_students.csv";
    MockMultipartFile csvFile = new MockMultipartFile(name, fileName, "text/csv", bytes);

    // Act
    List<Student> parsedStudents = parser.processCSV(csvFile);

    // Assert
    assertThat(parsedStudents).isEqualTo(null);
  }

}
