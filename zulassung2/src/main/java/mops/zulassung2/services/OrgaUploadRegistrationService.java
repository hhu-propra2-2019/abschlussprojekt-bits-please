package mops.zulassung2.services;


import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrgaUploadRegistrationService {
  private NameCreator nameCreator;
  private MinIoImplementation minIo;
  @Value("${endpoint}")
  private String endpoint;
  @Value("${access_key}")
  private String accessKey;
  @Value("${secret_key}")
  private String secretKey;

  public OrgaUploadRegistrationService() {
    nameCreator = new CustomNameCreator();
  }


  public boolean test(Student student, String subject) {
    if (minIo == null) {
      MinIoRepositoryInterface repo = new MinIoRepository(endpoint, accessKey, secretKey);
      NameCreator nameCreator = new CustomNameCreator();
      minIo = new MinIoImplementation(repo, nameCreator);
    }
    return minIo.isAuthorized(student, subject);
  }
}

