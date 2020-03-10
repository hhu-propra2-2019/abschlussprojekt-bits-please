package mops.zulassung2.model.mail;

import java.io.File;

public interface EmailServiceInterface {

  public void sendSimpleMessage(String to, String subject, String text);

  public void sendMessage(String to, String subject, String text, File attach, String filename);
}
