package mops.zulassung2;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
class Account {
  private final String name;
  private final String email;
  private final String image;
  private final Set<String> roles;
}
