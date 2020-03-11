package mops.zulassung2.model.fileparsing;

import org.apache.commons.csv.CSVParser;

public interface Validator {
  boolean validateCSV(CSVParser csvParser);

  boolean validateTXT();
}
