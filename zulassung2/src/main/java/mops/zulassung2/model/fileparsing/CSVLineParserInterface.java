package mops.zulassung2.model.fileparsing;

import mops.zulassung2.model.dataobjects.Student;
import org.apache.commons.csv.CSVRecord;

public interface CSVLineParserInterface {
  Student parseLine(CSVRecord csvRecord);
}
