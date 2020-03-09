package mops.zulassung2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

  /**
   * Configures settings for sending emails via SMTP.
   *
   * @return JavaMailSender
   */
  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("securesmtp.t-online.de");
    mailSender.setPort(465);

    mailSender.setUsername("mops.zulassung2@magenta.de");
    mailSender.setPassword("zulassung2");

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.ssl.enable", "true");
    props.put("mail.debug", "true");

    return mailSender;
  }
}
