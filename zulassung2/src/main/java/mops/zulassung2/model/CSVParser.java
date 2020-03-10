package mops.zulassung2.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVParser {

  private String delimiter;

  public CSVParser(String delimiter) {
    this.delimiter = delimiter;
  }

  /**
   * Diese Methode wird vom OrganisatorController aufgerufen, nachdem eine File hochgeladen wurde.
   * Das File wird eingelesen und die enthaltenden Studenten zunächst erzeugt
   * und anschließend in einer ArrayList gespeichert.
   * *
   *
   * @return gibt eine ArrayList zurück, welche die erzeugten Studenten enthält.
   */
  public List<Student> processCSV(MultipartFile file) {
    List<Student> studentList = new ArrayList<>();
    File studentFile = saveFile(file);

    Scanner scanner = null;
    try {
      scanner = new Scanner(studentFile, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    scanner.useDelimiter(delimiter);
    parseLine(studentList, scanner);
    scanner.close();

    return studentList;
  }

  private File saveFile(MultipartFile file) {
    File studentFile = new File(file.getName());
    try {
      file.transferTo(studentFile);
      if (!studentFile.exists()) {
        Files.createFile(studentFile.toPath());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return studentFile;
  }

  private void parseLine(List<Student> studentList, Scanner scanner) {
    while (scanner.hasNextLine()) {
      String[] values = scanner.nextLine().split(delimiter);
      if (values.length == 4) {
        Student student = new Student(values[0], values[1], values[2], values[3]);
        studentList.add(student);
      }
    }
  }
}
