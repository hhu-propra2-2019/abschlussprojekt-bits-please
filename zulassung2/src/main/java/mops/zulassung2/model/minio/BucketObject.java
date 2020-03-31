package mops.zulassung2.model.minio;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class BucketObject {
  private String bucketName;
  private String objectName;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BucketObject that = (BucketObject) o;
    return Objects.equals(bucketName, that.bucketName)
            && Objects.equals(objectName, that.objectName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bucketName, objectName);
  }
}
