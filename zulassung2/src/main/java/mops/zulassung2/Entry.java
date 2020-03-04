package mops.zulassung2;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Entry {
	private final String attribute1;
	private final String attribute2;
	private final String attribute3;

	public static List<Entry> generate(int n) {
		return new ArrayList<>();
	}
}
