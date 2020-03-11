package mops.zulassung2.model.fileparsing;

import mops.zulassung2.model.Student;
import org.apache.commons.csv.CSVRecord;

public interface CSVLineParser {
  Student parseLine(CSVRecord csvRecord);
}
