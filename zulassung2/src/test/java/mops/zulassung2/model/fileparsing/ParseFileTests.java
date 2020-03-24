package mops.zulassung2.model.fileparsing;

import mops.zulassung2.model.dataobjects.Student;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParseFileTests {

  static FileParser parser = new FileParser(new Validator(), new CSVLineParser());
  private Student tim = new Student("2727912", "tigeu100@hhu.de", "geuer", "tim");
  private Student markus = new Student("2757144", "masie@hhu.de", "siewert", "markus");
  private String header = "matriculationnumber,email,name,forename\n";
  private String student1Data = "2727912,tigeu100@hhu.de,geuer,tim\n";
  private String student2Data = "2757144,masie@hhu.de,siewert,markus\n";
  private String txt1Data = "matriculationnumber:2727912"
          + " email:tigeu100@hhu.de"
          + " name:geuer"
          + " forename:tim"
          + " module:Propra"
          + " semester:WS2019";

  /**
   * This method provides the test's general information and variables for the CSV parser.
   *
   * @param data Since this value is different for each test,
   *             we pass the "data" method on every call.
   * @return returns the CSV-File.
   */
  public MockMultipartFile csvSetUp(String data) {
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    String name = "testFile.csv";
    String fileName = "test_students.csv";
    return new MockMultipartFile(name, fileName, "text/csv", bytes);
  }

  /**
   * This method provides the test's general information and variables for the TXT parser.
   *
   * @param data Since this value is different for each test,
   *             we pass the "data" method on every call.
   * @return returns the TXT-File.
   */
  public MockMultipartFile txtSetUp(String data) {
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    String name = "testFile.txt";
    String fileName = "testFile.txt";
    return new MockMultipartFile(name, fileName, "text/plain", bytes);
  }

  @Test
  public void testParsingStudentsFromCSV() {
    // Arrange
    List<Student> students = new ArrayList<>();

    students.add(tim);
    students.add(markus);

    String data = header + student1Data + student2Data;

    // Act
    List<Student> parsedStudents = parser.processCSV(csvSetUp(data));

    // Assert
    assertThat(parsedStudents).isEqualTo(students);
  }

  @Test
  public void testParsingInvalidCSVFile() {
    // Arrange
    String data = student1Data + student2Data;

    // Act
    List<Student> parsedStudents = parser.processCSV(csvSetUp(data));

    // Assert
    assertThat(parsedStudents).isEqualTo(null);
  }

  @Test
  public void testParsingCSVFileWithIncorrectlyNamedCollumns() {
    // Arrange
    String wrongNamedCollumnsHeader = "matricl,mail,nam,fornam\n";
    String data = wrongNamedCollumnsHeader + student1Data + student2Data;

    // Act
    List<Student> parsedStudents = parser.processCSV(csvSetUp(data));

    // Assert
    assertThat(parsedStudents).isEqualTo(null);
  }

  @Test
  public void testParsingCSVFileWithIncorrectSequence() {
    // Arrange
    String incorrectSequenceHeader = "forname,name,email,matriculationnumber\n";
    String data = incorrectSequenceHeader + student1Data + student2Data;

    // Act
    List<Student> parsedStudents = parser.processCSV(csvSetUp(data));

    // Assert
    assertThat(parsedStudents).isEqualTo(null);
  }

  @Test
  public void testSizeOfStudentList() {
    // Arrange
    String data = header;

    // Act
    List<Student> parsedStudents = parser.processCSV(csvSetUp(data));

    // Assert
    assertThat(parsedStudents).isEqualTo(null);
  }

  @Test
  public void testParsingLinesFromTXT() {
    // Arrange
    List<String> lines = new ArrayList<>();

    lines.add(txt1Data);
    lines.add("Signature");

    String data = txt1Data + "\nSignature";

    // Act
    List<String> parsedLines = parser.processTXT(txtSetUp(data));

    // Assert
    assertThat(parsedLines).isEqualTo(lines);
  }

  @Test
  public void testParsingInvalidTXTFile() {
    // Arrange
    String data = txt1Data + "Signature";

    // Act
    List<String> parsedLines = parser.processTXT(txtSetUp(data));

    // Assert
    assertThat(parsedLines).isEqualTo(null);
  }
}
