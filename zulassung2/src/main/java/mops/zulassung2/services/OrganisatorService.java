package mops.zulassung2.services;

import mops.zulassung2.model.dataobjects.Student;
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
    return txtParser.processTXT(file);
  }

  /**
   * Reads the data from the data String of the receipt.
   *
   * @param receiptContent Data of the receipt
   * @param signature      Signature of the receipt
   * @return Object with the extracted data of the receipt
   */
  public ReceiptData readReceiptContent(String receiptContent, String signature) {

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

    return receiptData;
  }
}
