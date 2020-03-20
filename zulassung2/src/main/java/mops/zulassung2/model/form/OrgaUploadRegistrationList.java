package mops.zulassung2.model.form;

import lombok.Data;
import mops.zulassung2.model.dataobjects.Student;

@Data
public class OrgaUploadRegistrationList {
  private boolean zulassung;

  public boolean test2(Student studentObj, String subject) {
    return false;
  }
}