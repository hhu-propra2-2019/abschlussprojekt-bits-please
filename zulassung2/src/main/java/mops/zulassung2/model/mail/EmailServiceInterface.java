package mops.zulassung2.model.mail;

import java.io.File;

public interface EmailServiceInterface {

  public void sendSimpleMessage(String to, String subject, String text);

  public void sendMessageWithAttachment(String to, String subject, String text, File attachment, String filename);
}
