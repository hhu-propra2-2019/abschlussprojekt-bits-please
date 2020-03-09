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

  public List<Studentin> processCSV(MultipartFile file) {
    List<Studentin> studentinList = new ArrayList<>();
    File studentinFile = saveFile(file);

    Scanner scanner = null;
    try {
      scanner = new Scanner(studentinFile, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    scanner.useDelimiter(delimiter);
    parseLine(studentinList, scanner);
    scanner.close();

    return studentinList;
  }

  private File saveFile(MultipartFile file) {
    File studentinFile = new File(file.getName());
    try {
      file.transferTo(studentinFile);
      Files.createFile(studentinFile.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return studentinFile;
  }

  private void parseLine(List<Studentin> studentinList, Scanner scanner) {
    while (scanner.hasNextLine()) {
      String[] values = scanner.next().split(delimiter);
      if (values.length == 4) {
        Studentin studentin = new Studentin(values[0], values[1], values[2], values[3]);
        studentinList.add(studentin);
      }
    }
  }
}
