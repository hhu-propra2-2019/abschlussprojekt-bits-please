package mops.zulassung2.model.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class Account {
  private final String name;
  private final String email;
  private final String image;
  private final Set<String> roles;
}
