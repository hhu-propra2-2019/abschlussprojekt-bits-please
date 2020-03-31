package mops.zulassung2.model.fileparsing;

import mops.zulassung2.model.dataobjects.Student;
import org.apache.commons.csv.CSVRecord;

public class CustomCSVLineParser implements CSVLineParser {

  @Override
  public Student parseLine(CSVRecord csvRecord) {
    String matrNr = csvRecord.get(0);
    String email = csvRecord.get(1);
    String name = csvRecord.get(2);
    String forename = csvRecord.get(3);

    Student student = new Student(matrNr, email, name, forename);

    return student;
  }
}
