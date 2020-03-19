package mops.zulassung2.services;

import lombok.Getter;
import lombok.Setter;
import mops.zulassung2.model.dataobjects.Student;

@Getter
public class CustomReceiptData implements ReceiptData {

  private String matriculationNumber;
  private String email;
  private String name;
  private String foreName;
  private String module;
  private String semester;
  private String signature;
  @Setter
  private boolean valid;

  /**
   * Saves the data of the receipt.
   *
   * @param student         Contains the data of the student
   * @param currentSubject  Contains subject of the student
   * @param currentSemester Contains semester of the student
   */
  public CustomReceiptData(Student student, String currentSubject, String currentSemester) {
    this.matriculationNumber = student.getMatriculationNumber();
    this.email = student.getEmail();
    this.name = student.getName();
    this.foreName = student.getForeName();
    this.module = currentSubject;
    this.semester = currentSemester;
  }

  /**
   * Saves the data of the receipt including the signature.
   *
   * @param student         Contains the data of the student
   * @param currentSubject  Contains subject of the student
   * @param currentSemester Contains semester of the student
   * @param signature       Contains signature of the receipt
   */
  public CustomReceiptData(Student student, String currentSubject,
                           String currentSemester, String signature) {
    this.matriculationNumber = student.getMatriculationNumber();
    this.email = student.getEmail();
    this.name = student.getName();
    this.foreName = student.getForeName();
    this.module = currentSubject;
    this.semester = currentSemester;
    this.signature = signature;
  }

  @Override
  public String create() {
    String data = "matriculationnumber:" + matriculationNumber
        + " email:" + email
        + " name:" + name
        + " forename:" + foreName
        + " module:" + module
        + " semester:" + semester;
    return data;
  }
}
