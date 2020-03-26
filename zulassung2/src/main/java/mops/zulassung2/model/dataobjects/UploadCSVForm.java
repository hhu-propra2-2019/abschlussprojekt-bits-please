package mops.zulassung2.model.dataobjects;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadCSVForm {

  private MultipartFile multipartFile;
  private String subject;
  private String semester;
  private String deadline;


}
