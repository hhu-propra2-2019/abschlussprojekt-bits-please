package mops.zulassung2.tasks;

import mops.zulassung2.model.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;


public class DeleteStorageTaskTest {

  private MinIoHelper minIoHelper;
  private CustomDateInterface customDateInterface;
  private DeleteStorageTask deleteStorageTask;
  private List<BucketObject> bucketObjects;
  private String bucketName = "test-bucket-name";
  private String objectName = "test-object-name";


  @BeforeEach
  private void setBaseUp() {
    minIoHelper = mock(MinIoHelper.class);
    customDateInterface = mock(CustomDate.class);
    deleteStorageTask = new DeleteStorageTask(minIoHelper, 5, customDateInterface);
    bucketObjects = new ArrayList<>();
    BucketObject bucketObject = new BucketObject(bucketName, objectName);
    bucketObjects.add(bucketObject);
  }

  @Test
  void testDeleteStorageTask_withOneObjectAndBucketToBeDeleted() {
    //Arrange

    Date creationDate = DateUtils.parseDateFromString("2015-01-01");
    Date currentDate = DateUtils.parseDateFromString("2021-01-01");

    when(customDateInterface.getCurrentDate()).thenReturn(currentDate);
    when(minIoHelper.getAllObjects()).thenReturn(bucketObjects);
    when(minIoHelper.getCreateTime(bucketName, objectName)).thenReturn(creationDate);
    when(minIoHelper.isBucketEmpty(bucketName)).thenReturn(true);

    //Act
    deleteStorageTask.checkStorageDuration();

    //Assert
    verify(minIoHelper, times(1)).removeObject(bucketName, objectName);
    verify(minIoHelper, times(1)).removeBucket(bucketName);

  }

  @Test
  void testDeleteStorageTask_withNoObjectAndBucketToBeDeleted() {
    //Arrange

    Date creationDate = DateUtils.parseDateFromString("2015-01-01");
    Date currentDate = DateUtils.parseDateFromString("2021-01-01");

    when(customDateInterface.getCurrentDate()).thenReturn(currentDate);
    when(minIoHelper.getAllObjects()).thenReturn(bucketObjects);
    when(minIoHelper.getCreateTime(bucketName, objectName)).thenReturn(creationDate);
    when(minIoHelper.isBucketEmpty(bucketName)).thenReturn(true);

    //Act
    deleteStorageTask.checkStorageDuration();

    //Assert
    verify(minIoHelper, times(1)).removeObject(bucketName, objectName);
    verify(minIoHelper, times(1)).removeBucket(bucketName);

  }

}
