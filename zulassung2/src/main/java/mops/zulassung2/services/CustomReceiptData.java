package mops.zulassung2.services;

import lombok.Getter;
import lombok.Setter;
import mops.zulassung2.model.Student;

@Getter
public class CustomReceiptData implements ReceiptData {

  private String matriculationNumber;
  private String email;
  private String name;
  private String foreName;
  private String module;
  private String signature;
  @Setter
  private boolean valid;

  public CustomReceiptData(Student student, String currentSubject) {
    this.matriculationNumber = student.getMatriculationNumber();
    this.email = student.getEmail();
    this.name = student.getName();
    this.foreName = student.getForeName();
    this.module = currentSubject;
  }

  public CustomReceiptData(Student student, String currentSubject, String signature) {
    this.matriculationNumber = student.getMatriculationNumber();
    this.email = student.getEmail();
    this.name = student.getName();
    this.foreName = student.getForeName();
    this.module = currentSubject;
    this.signature = signature;
  }

  @Override
  public String create() {
    String data = "matriculationnumber:" + matriculationNumber
        + " email:" + email
        + " name:" + name
        + " forename:" + foreName
        + " module:" + module;
    return data;
  }
}
