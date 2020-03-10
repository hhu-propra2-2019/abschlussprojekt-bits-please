package mops.zulassung2.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileParser {
  
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
    Reader reader = null;
    CSVParser csvParser = null;

    try {
      Path test = studentFile.toPath();
      reader = Files.newBufferedReader(studentFile.toPath());
      csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (csvParser == null) {
      return null;
    }
    Map<String, Integer> headerMap = csvParser.getHeaderMap();
    if (headerMap.size() != 4) {
      return null;
    }

    for (String key : headerMap.keySet()) {
      Integer index = headerMap.get(key);
      if ((index == 0 && !key.equals("matriculationnumber"))
              || (index == 1 && !key.equals("email"))
              || (index == 2 && !key.equals("name"))
              || (index == 3 && !key.equals("forname"))) {
        return null;
      }
    }

    for (CSVRecord csvRecord : csvParser) {
      String matrNr = csvRecord.get(0);
      String email = csvRecord.get(1);
      String name = csvRecord.get(2);
      String forename = csvRecord.get(3);

      Student currentStudent = new Student(matrNr, email, name, forename);
      studentList.add(currentStudent);
    }

    deleteFile(studentFile);

    return studentList;
  }

  private File saveFile(MultipartFile file) {
    File convFile = null;
    try {
      String fileName = file.getOriginalFilename();
      convFile = new File(fileName);
      FileOutputStream fos = new FileOutputStream(convFile);
      fos.write(file.getBytes());
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return convFile;
  }

  private void deleteFile(File file) {
    try {
      Files.deleteIfExists(file.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
