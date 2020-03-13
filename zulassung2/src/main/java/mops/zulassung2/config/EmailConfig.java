package mops.zulassung2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

  @Value("${spring.mail.host}")
  String host;
  @Value("${spring.mail.port}")
  int port;
  @Value("${spring.mail.username}")
  String username;
  @Value("${spring.mail.password}")
  String password;

  /**
   * Configures settings for sending emails via SMTP.
   *
   * @return JavaMailSender
   */
  @Primary
  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);

    mailSender.setUsername(username);
    mailSender.setPassword(password);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.ssl.enable", "true");
    props.put("mail.debug", "true");

    return mailSender;
  }

}
