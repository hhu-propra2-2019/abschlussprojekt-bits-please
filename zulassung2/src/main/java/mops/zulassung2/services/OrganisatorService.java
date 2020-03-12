package mops.zulassung2.services;

import mops.zulassung2.model.Student;
import mops.zulassung2.model.fileparsing.CustomCSVLineParser;
import mops.zulassung2.model.fileparsing.CustomValidator;
import mops.zulassung2.model.fileparsing.FileParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganisatorService {

  /**
   * Extracts list of students from given CSV file.
   *
   * @param file CSV File that contains the students
   * @return List of students
   */
  public List<Student> processCSVUpload(MultipartFile file) {
    FileParser csvParser = new FileParser(new CustomValidator(), new CustomCSVLineParser());
    List<Student> students = csvParser.processCSV(file);
    if (students == null) {
      students = new ArrayList<>();
    }

    return students;
  }

  /**
   * Extracts lines from given TXT File to List.
   *
   * @param file TXT File that contains the receipt
   * @return List of lines
   */
  public List<String> processTXTUpload(MultipartFile file) {
    FileParser txtParser = new FileParser(new CustomValidator());
    List<String> lines = txtParser.processTXT(file);
    if (lines == null) {
      return null;
    }

    return lines;
  }
}
