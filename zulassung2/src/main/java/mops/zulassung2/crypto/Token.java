package mops.zulassung2.crypto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Token {

  private String data;
  private String signature;
}