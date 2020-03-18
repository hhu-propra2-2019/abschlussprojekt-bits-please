package mops.zulassung2.model.minio;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BucketObject {
  private String bucketName;
  private String objectName;
}
