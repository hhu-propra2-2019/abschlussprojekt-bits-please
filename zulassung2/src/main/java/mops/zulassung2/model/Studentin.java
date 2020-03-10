package mops.zulassung2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class Studentin {

  private String matrikelnummer;
  private String email;
  private String name;
  private String vorname;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Studentin studentin = (Studentin) o;
    return Objects.equals(matrikelnummer, studentin.matrikelnummer)
            && Objects.equals(email, studentin.email)
            && Objects.equals(name, studentin.name)
            && Objects.equals(vorname, studentin.vorname);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matrikelnummer, email, name, vorname);
  }
}
