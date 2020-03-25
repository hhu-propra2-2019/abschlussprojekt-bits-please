package mops.zulassung2.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class OrgaUploadCSVForm {

  private MultipartFile multipartFile;
  private String subject;
  private String semester;
  private Date deadline;


}
