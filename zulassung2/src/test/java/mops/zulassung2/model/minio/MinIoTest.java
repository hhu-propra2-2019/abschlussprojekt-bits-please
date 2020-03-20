package mops.zulassung2.model.minio;

import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.junit.jupiter.api.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MinIoTest {

  private String bucketName = "test-bucket-";
  private String objectName = "test-object-";

  private List<BucketObject> createBucketObjects(int bucketAmount, int objectAmount) {
    List<BucketObject> bucketObjects = new ArrayList<>();
    for (int i = 0; i < bucketAmount; i++) {
      for (int j = 0; j < objectAmount; j++) {
        BucketObject bucketObject = new BucketObject(getBucketName(i), getObjectName(j));
        bucketObjects.add(bucketObject);
      }
    }

    return bucketObjects;
  }

  private List<Result<Item>> createObjects(int amount) {
    List<Result<Item>> objects = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      objects.add(createObject(i));
    }

    return objects;
  }

  private Result<Item> createObject(int number) {
    Item item = null;
    try {
      item = new Item(getObjectName(number), false);
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    }
    return new Result<>(item, new Exception());
  }

  private String getBucketName(int number) {
    return bucketName + number;
  }

  private String getObjectName(int number) {
    return objectName + number;
  }

  @Test
  void testMinIoGetAllObjects_oneBucketOneObject() {
    // Arrange
    List<BucketObject> expectedObjects = createBucketObjects(1, 1);
    List<Result<Item>> objects = createObjects(1);
    MinIoRepositoryInterface repo = mock(MinIoRepository.class);
    Bucket bucket = mock(Bucket.class);
    List<Bucket> buckets = new ArrayList<>();
    buckets.add(bucket);

    when(bucket.name()).thenReturn(getBucketName(0));
    when(repo.listBuckets()).thenReturn(buckets);
    when(repo.listObjects(getBucketName(0))).thenReturn(objects);
    when(repo.getObjectName(objects.get(0))).thenReturn(getObjectName(0));

    MinIoImplementation minIo = new MinIoImplementation(repo);

    // Act
    List<BucketObject> allObjects = minIo.getAllObjects();

    // Assert
    assertThat(allObjects).isEqualTo(expectedObjects);
  }

  @Test
  void testMinIoGetAllObjects_oneBucketTwoObject() {
    // Arrange
    List<BucketObject> expectedObjects = createBucketObjects(1, 2);
    List<Result<Item>> objects = createObjects(2);
    MinIoRepositoryInterface repo = mock(MinIoRepository.class);
    Bucket bucket = mock(Bucket.class);
    List<Bucket> buckets = new ArrayList<>();
    buckets.add(bucket);

    when(bucket.name()).thenReturn(getBucketName(0));
    when(repo.listBuckets()).thenReturn(buckets);
    when(repo.listObjects(getBucketName(0))).thenReturn(objects);
    when(repo.getObjectName(objects.get(0))).thenReturn(getObjectName(0));
    when(repo.getObjectName(objects.get(1))).thenReturn(getObjectName(1));

    MinIoImplementation minIo = new MinIoImplementation(repo);

    // Act
    List<BucketObject> allObjects = minIo.getAllObjects();

    // Assert
    assertThat(allObjects).isEqualTo(expectedObjects);
  }

  @Test
  void testMinIoGetAllObjects_twoBucketsOneObject() {
    // Arrange
    List<BucketObject> expectedObjects = createBucketObjects(2, 1);
    List<Result<Item>> bucketObjects = createObjects(1);
    List<Result<Item>> bucket2Objects = createObjects(1);
    MinIoRepositoryInterface repo = mock(MinIoRepository.class);
    Bucket bucket = mock(Bucket.class);
    Bucket bucket2 = mock(Bucket.class);
    List<Bucket> buckets = new ArrayList<>();
    buckets.add(bucket);
    buckets.add(bucket2);

    when(bucket.name()).thenReturn(getBucketName(0));
    when(bucket2.name()).thenReturn(getBucketName(1));
    when(repo.listBuckets()).thenReturn(buckets);
    when(repo.listObjects(getBucketName(0))).thenReturn(bucketObjects);
    when(repo.listObjects(getBucketName(1))).thenReturn(bucket2Objects);
    when(repo.getObjectName(bucketObjects.get(0))).thenReturn(getObjectName(0));
    when(repo.getObjectName(bucket2Objects.get(0))).thenReturn(getObjectName(0));

    MinIoImplementation minIo = new MinIoImplementation(repo);

    // Act
    List<BucketObject> allObjects = minIo.getAllObjects();

    // Assert
    assertThat(allObjects).isEqualTo(expectedObjects);
  }

  @Test
  void testMinIoGetAllObjects_twoBucketsTwoObjects() {
    // Arrange
    List<BucketObject> expectedObjects = createBucketObjects(2, 2);
    List<Result<Item>> bucketObjects = createObjects(2);
    List<Result<Item>> bucket2Objects = createObjects(2);
    MinIoRepositoryInterface repo = mock(MinIoRepository.class);
    Bucket bucket = mock(Bucket.class);
    Bucket bucket2 = mock(Bucket.class);
    List<Bucket> buckets = new ArrayList<>();
    buckets.add(bucket);
    buckets.add(bucket2);

    when(bucket.name()).thenReturn(getBucketName(0));
    when(bucket2.name()).thenReturn(getBucketName(1));
    when(repo.listBuckets()).thenReturn(buckets);
    when(repo.listObjects(getBucketName(0))).thenReturn(bucketObjects);
    when(repo.listObjects(getBucketName(1))).thenReturn(bucket2Objects);
    when(repo.getObjectName(bucketObjects.get(0))).thenReturn(getObjectName(0));
    when(repo.getObjectName(bucketObjects.get(1))).thenReturn(getObjectName(1));
    when(repo.getObjectName(bucket2Objects.get(0))).thenReturn(getObjectName(0));
    when(repo.getObjectName(bucket2Objects.get(1))).thenReturn(getObjectName(1));

    MinIoImplementation minIo = new MinIoImplementation(repo);

    // Act
    List<BucketObject> allObjects = minIo.getAllObjects();

    // Assert
    assertThat(allObjects).isEqualTo(expectedObjects);
  }

  @Test
  void testMinIoIsBucketEmpty_oneBucketEmpty() {
    // Arrange
    Bucket bucket = mock(Bucket.class);
    List<Bucket> buckets = new ArrayList<>();
    buckets.add(bucket);

    List<Result<Item>> objects = createObjects(0);
    MinIoRepositoryInterface repo = mock(MinIoRepository.class);
    MinIoImplementation minIo = new MinIoImplementation(repo);
    when(bucket.name()).thenReturn(getBucketName(0));
    when(repo.listBuckets()).thenReturn(buckets);
    when(repo.listObjects(getBucketName(0))).thenReturn(objects);
    boolean expected = true;

    // Act
    boolean isBucketEmpty = minIo.isBucketEmpty(getBucketName(0));

    // Assert
    assertThat(isBucketEmpty).isEqualTo(expected);
  }

  @Test
  void testMinIoIsBucketEmpty_oneBucketOneObject() {
    // Arrange
    Bucket bucket = mock(Bucket.class);
    List<Bucket> buckets = new ArrayList<>();
    buckets.add(bucket);

    List<Result<Item>> objects = createObjects(1);
    MinIoRepositoryInterface repo = mock(MinIoRepository.class);
    MinIoImplementation minIo = new MinIoImplementation(repo);
    when(bucket.name()).thenReturn(getBucketName(0));
    when(repo.listBuckets()).thenReturn(buckets);
    when(repo.listObjects(getBucketName(0))).thenReturn(objects);
    boolean expected = false;

    // Act
    boolean isBucketEmpty = minIo.isBucketEmpty(getBucketName(0));

    // Assert
    assertThat(isBucketEmpty).isEqualTo(expected);
  }

  @Test
  void testMinIoIsBucketEmpty_oneBucketTwoObject() {
    // Arrange
    Bucket bucket = mock(Bucket.class);
    List<Bucket> buckets = new ArrayList<>();
    buckets.add(bucket);

    List<Result<Item>> objects = createObjects(2);
    MinIoRepositoryInterface repo = mock(MinIoRepository.class);
    MinIoImplementation minIo = new MinIoImplementation(repo);
    when(bucket.name()).thenReturn(getBucketName(0));
    when(repo.listBuckets()).thenReturn(buckets);
    when(repo.listObjects(getBucketName(0))).thenReturn(objects);
    boolean expected = false;

    // Act
    boolean isBucketEmpty = minIo.isBucketEmpty(getBucketName(0));

    // Assert
    assertThat(isBucketEmpty).isEqualTo(expected);
  }
}
