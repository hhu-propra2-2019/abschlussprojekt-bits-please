package mops.zulassung2.model.fileparsing;

import org.apache.commons.csv.CSVParser;

import java.util.List;

public interface Validator {
  boolean validateCSV(CSVParser csvParser);

  boolean validateTXT(List<String> txtContent);
}
