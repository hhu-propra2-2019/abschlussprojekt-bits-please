package mops.zulassung2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class Student {

  private String matriculationNumber;
  private String email;
  private String name;
  private String foreName;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Student studentin = (Student) o;
    return Objects.equals(matriculationNumber, studentin.matriculationNumber)
            && Objects.equals(email, studentin.email)
            && Objects.equals(name, studentin.name)
            && Objects.equals(foreName, studentin.foreName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matriculationNumber, email, name, foreName);
  }
}
