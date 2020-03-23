package mops.zulassung2.model.minio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

  /**
   * Creates a date out of a given String.
   *
   * @param date String that needs to be converted to date
   * @return newDate
   */

  public static Date parseDateFromString(String date) {
    Date newDate = null;
    try {
      newDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return newDate;
  }
}
