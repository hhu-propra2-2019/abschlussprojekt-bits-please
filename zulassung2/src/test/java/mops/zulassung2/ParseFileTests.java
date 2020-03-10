package mops.zulassung2;

import mops.zulassung2.model.FileParser;
import mops.zulassung2.model.Studentin;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParseFileTests {

  @Test
  public void testParsingStudentsFromCSV() {
    // Arrange
    List<Studentin> students = new ArrayList<>();
    Studentin tim = new Studentin("2727912", "tigeu100@hhu.de", "geuer", "tim");
    Studentin markus = new Studentin("2757144", "masie@hhu.de", "siewert", "markus");
    students.add(tim);
    students.add(markus);

    String data = "2727912,tigeu100@hhu.de,geuer,tim\n2757144,masie@hhu.de,siewert,markus\n";
    FileParser parser = new FileParser("");
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    MockMultipartFile csvFile = new MockMultipartFile("testFile.csv", "test_students.csv", "text/csv", bytes);

    // Act
    List<Studentin> parsedStudents = parser.processCSV(csvFile);

    // Assert
    assertThat(parsedStudents).isEqualTo(students);
  }
}
