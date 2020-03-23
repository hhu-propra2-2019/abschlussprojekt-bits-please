package mops.zulassung2.services;

import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.fileparsing.CustomCSVLineParser;
import mops.zulassung2.model.fileparsing.CustomValidator;
import mops.zulassung2.model.fileparsing.FileParser;
import mops.zulassung2.model.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

@Service
public class FileService {

  private NameCreator nameCreator;
  private MinIoImplementation minIo;
  @Value("${endpoint}")
  private String endpoint;
  @Value("${access_key}")
  private String accessKey;
  @Value("${secret_key}")
  private String secretKey;

  public FileService() {
    nameCreator = new CustomNameCreator();
  }

  /**
   * Extracts list of students from given CSV file.
   *
   * @param file CSV File that contains the students
   * @return List of students
   */
  public List<Student> processCSVUpload(MultipartFile file) {
    FileParser csvParser = new FileParser(new CustomValidator(), new CustomCSVLineParser());
    return csvParser.processCSV(file);
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

  /**
   * *  stores receipt for given student.
   *
   * @param student student whose receipt needs to be stored
   * @param file    receipt that needs to be stored
   */

  public void storeReceipt(Student student, File file) {

    if (minIo == null) {
      MinIoRepositoryInterface repo = new MinIoRepository(endpoint, accessKey, secretKey);
      NameCreator nameCreator = new CustomNameCreator();
      minIo = new MinIoImplementation(repo, nameCreator);
    }
    String bucketName = nameCreator.createBucketName(student);
    if (!minIo.bucketExists(bucketName)) {
      minIo.makeBucket(bucketName);
    }

    minIo.putObject(bucketName, file.getName(), file.getPath(), file.length(),
        new HashMap<String, String>(), ".txt");
  }

  /**
   * creates a File from a MultiPartFile that was uploaded by user.
   *
   * @param receiptData Student Information
   * @return redirect
   */

  public File createFileFromSubmittedReceipt(ReceiptData receiptData) {
    String data = receiptData.create();
    File file = new File(System.getProperty("user.dir")
        + "token_" + receiptData.getModule()
        + "_" + receiptData.getName() + ".txt");
    FileWriter writer;

    try {
      writer = new FileWriter(file, StandardCharsets.UTF_8);
      writer.write(data + "\n");
      writer.write(receiptData.getSignature());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
  }
}
