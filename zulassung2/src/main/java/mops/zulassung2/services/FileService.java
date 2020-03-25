package mops.zulassung2.services;

import mops.zulassung2.model.crypto.Receipt;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.fileparsing.CSVLineParser;
import mops.zulassung2.model.fileparsing.FileParser;
import mops.zulassung2.model.fileparsing.Validator;
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

  private NameCreatorInterface nameCreatorInterface;
  private MinIoImplementation minIo;
  private SignatureService signatureService;

  @Value("${endpoint}")
  private String endpoint;
  @Value("${access_key}")
  private String accessKey;
  @Value("${secret_key}")
  private String secretKey;

  public FileService(SignatureService signatureService) {
    nameCreatorInterface = new NameCreator(new BucketNameValidator());
    this.signatureService = signatureService;
  }

  /**
   * Extracts list of students from given CSV file.
   *
   * @param file CSV File that contains the students
   * @return List of students
   */
  public List<Student> processCSVUpload(MultipartFile file) {
    FileParser csvParser = new FileParser(new Validator(), new CSVLineParser());
    return csvParser.processCSV(file);
  }

  /**
   * Extracts lines from given TXT File to List.
   *
   * @param file TXT File that contains the receipt
   * @return List of lines
   */
  public List<String> processTXTUpload(MultipartFile file) {
    FileParser txtParser = new FileParser(new Validator());
    return txtParser.processTXT(file);
  }

  /**
   * Reads the data from the data String of the receipt.
   *
   * @param receiptContent Data of the receipt
   * @param signature      Signature of the receipt
   * @return Object with the extracted data of the receipt
   */
  public ReceiptDataInterface readReceiptContent(String receiptContent, String signature) {

    String[] dataObjects = receiptContent.split(" ");
    Student student = new Student(
        dataObjects[0].split(":")[1], // Matriculationnumber
        dataObjects[1].split(":")[1], // Email
        dataObjects[2].split(":")[1], // Name
        dataObjects[3].split(":")[1]); // Forename

    ReceiptDataInterface receiptDataInterface = new ReceiptData(student,
        dataObjects[4].split(":")[1], // Module
        dataObjects[5].split(":")[1], // Semester
        signature);                         // Signature

    return receiptDataInterface;
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
      NameCreatorInterface nameCreatorInterface = new NameCreator(new BucketNameValidator());
      minIo = new MinIoImplementation(repo, nameCreatorInterface);
    }
    String bucketName = nameCreatorInterface.createBucketName(student);
    if (!minIo.bucketExists(bucketName)) {
      minIo.makeBucket(bucketName);
    }

    minIo.putObject(bucketName, file.getName(), file.getPath(), file.length(),
        new HashMap<String, String>(), ".txt");
  }

  /**
   * creates a File from a MultiPartFile that was uploaded by user.
   *
   * @param receiptDataInterface Student Information
   * @return redirect
   */
  public File createFileFromSubmittedReceipt(ReceiptDataInterface receiptDataInterface) {
    String studentData = receiptDataInterface.create();
    File userFile = new File(System.getProperty("user.dir")
        + "token_" + receiptDataInterface.getModule()
        + "_" + receiptDataInterface.getName() + ".txt");
    FileWriter writer;

    try {
      writer = new FileWriter(userFile, StandardCharsets.UTF_8);
      writer.write(studentData + "\n");
      writer.write(receiptDataInterface.getSignature());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return userFile;
  }

  /**
   * Diese Methode wird vom OrganisatorController (Methode: sendMail) aufgerufen.
   * *
   * Diese Methode erstellt benutzerdefinierte Files und ruft sendMessage auf.
   */

  public File createFile(Student student, String currentSubject, String currentSemester) {
    ReceiptDataInterface receiptDataInterface = new ReceiptData(student, currentSubject, currentSemester);
    String studentData = receiptDataInterface.create();
    Receipt receipt = signatureService.sign(studentData);
    File userFile = new File(System.getProperty("user.dir")
        + "token_" + receiptDataInterface.getModule()
        + "_" + receiptDataInterface.getName() + ".txt");
    FileWriter writer;

    try {
      writer = new FileWriter(userFile, StandardCharsets.UTF_8);
      writer.write(studentData + "\n");
      writer.write(receipt.getSignature());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return userFile;
  }

}
