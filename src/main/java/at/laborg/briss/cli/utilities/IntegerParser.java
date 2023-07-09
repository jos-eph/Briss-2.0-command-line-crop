package at.laborg.briss.cli.utilities;

import java.util.ArrayList;
import java.util.List;

public class IntegerParser {

	public static List<Integer> parseIntsFromDelimitedString(String delimitedIntString) {
		String[] stringInts = delimitedIntString.split(",");
		List<Integer> intList = new ArrayList<>();
		for (String stringInt : stringInts) {
			intList.add(Integer.valueOf(stringInt));
		}
		return intList;
	}

	public static List<Integer> parseIntsFromDelimitedString(String delimitedIntString, Integer numberRequired) {
		List<Integer> integers = parseIntsFromDelimitedString(delimitedIntString);
		if (integers.size() != numberRequired) {
			throw new IllegalArgumentException("Missing required number of arguments for integer list argument");
		}
		return integers;
	}

}
