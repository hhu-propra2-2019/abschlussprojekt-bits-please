package mops.zulassung2.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileParser {

  private String dir;

  public FileParser(String dir) {
    this.dir = dir;
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
    List<Studentin> studentList = new ArrayList<>();
    File studentFile = saveFile(file);
    Reader reader = null;
    CSVParser csvParser = null;

    try {
      reader = Files.newBufferedReader(studentFile.toPath());
      csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (CSVRecord csvRecord : csvParser) {
      String matrNr = csvRecord.get(0);
      String email = csvRecord.get(1);
      String name = csvRecord.get(2);
      String forename = csvRecord.get(3);

      Studentin currentStudent = new Studentin(matrNr, email, name, forename);
      studentList.add(currentStudent);
    }

    return studentList;
  }

  private File saveFile(MultipartFile file) {
    File studentinFile = new File(dir + file.getName());
    try {
      file.transferTo(studentinFile);
      if (!studentinFile.exists()) {
        Files.createFile(studentinFile.toPath());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return studentinFile;
  }
}
