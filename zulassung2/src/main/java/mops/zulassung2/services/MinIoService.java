package mops.zulassung2.services;


import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MinIoService {
  private NameCreator nameCreator;
  private MinIoImplementationInterface minIo;
  @Value("${endpoint}")
  private String endpoint;
  @Value("${access_key}")
  private String accessKey;
  @Value("${secret_key}")
  private String secretKey;

  public MinIoService() {
    nameCreator = new NameCreator(new BucketNameValidator());
  }

  /**
   * This method is responsible for the verification of the exam approval.
   *
   * @param student It gives a List of Students.
   * @param subject The Subject of each Student.
   * @return it returns a boolean which will show if a Student is allowed to take part in the Exam of "subject".
   */
  public boolean test(Student student, String subject) {
    if (minIo == null) {
      MinIoRepositoryInterface repo = new MinIoRepository(endpoint, accessKey, secretKey);
      minIo = new MinIoImplementation(repo, nameCreator);
    }
    return minIo.isAuthorized(student, subject);
  }
}

