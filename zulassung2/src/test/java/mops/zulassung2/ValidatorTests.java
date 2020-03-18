package mops.zulassung2;
import static org.junit.jupiter.api.Assertions.*;

import mops.zulassung2.model.fileparsing.CustomValidator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class ValidatorTests {
  private CustomValidator customValidator;

  @Test
  public void validateCSVTest() throws IOException {

      final CSVParser parser = CSVParser.parse("9999999\ntest@uni-duesseldorf.de\nmeier\nmax",
          CSVFormat.DEFAULT.withHeader("matriculationnumber", "email", "name","forename"));

    final Map<String, Integer> headerMap = parser.getHeaderMap();
    final Iterator<String> columnNames = headerMap.keySet().iterator();
    assertEquals("matriculationnumber", columnNames.next());
    assertEquals("email", columnNames.next());
    assertEquals("name", columnNames.next());
    assertEquals("forename", columnNames.next());
    final Iterator<CSVRecord> records = parser.iterator();

    // Parse to make sure getHeaderMap did not have a side-effect.
    for (int i = 0; i < 4; i++) {
      assertTrue(records.hasNext());
      final CSVRecord record = records.next();
      assertEquals(record.get(0), record.get("matriculationnumber"));
      assertEquals(record.get(1), record.get("email"));
      assertEquals(record.get(2), record.get("name"));
      assertEquals(record.get(3), record.get("forename"));
    }

    assertFalse(records.hasNext());
  }

  }











