package mops.zulassung2.model.minio;

import org.joda.time.DateTime;

import java.util.Date;

public class CustomDate implements CustomDateInterface {

  public Date getCurrentDate() {
    return DateTime.now().toDate();
  }

}
