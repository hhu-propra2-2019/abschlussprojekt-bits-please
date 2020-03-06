package mops.zulassung2.model.crypto;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

class Hasher implements HasherInterface {

  public String getHash(String toHash) {
    return sha256Hex(toHash);
  }
}