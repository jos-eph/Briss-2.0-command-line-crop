package at.laborg.briss.cli.utilities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NumberParser {

	public static List<BigDecimal> parseBigDecimalFromDelimitedString(String delimitedIntString) {
		String[] stringNums = delimitedIntString.split(",");
		List<BigDecimal> numList = new ArrayList<>();
		for (String stringNum : stringNums) {
			numList.add(new BigDecimal(stringNum));
		}
		return numList;
	}

	public static List<BigDecimal> parseBigDecimalFromDelimitedString(String delimitedIntString,
			Integer numberRequired) {
		List<BigDecimal> numbers = parseBigDecimalFromDelimitedString(delimitedIntString);
		if (numbers.size() != numberRequired) {
			throw new IllegalArgumentException("Missing required number of arguments for number list argument");
		}
		return numbers;
	}

	public static List<Float> parseFloatsFromDelimitedString(String delimitedString, Integer numberRequired) {
		List<BigDecimal> numbers = parseBigDecimalFromDelimitedString(delimitedString, numberRequired);

		return numbers.stream().map(bigdecimal -> Float.valueOf(bigdecimal.toString())).toList();
	}

	public static List<Integer> parseIntegersFromDelimitedString(String delimitedString) {
		List<BigDecimal> numbers = parseBigDecimalFromDelimitedString(delimitedString);

		return numbers.stream().map(bigdecimal -> bigdecimal.intValue()).toList();
	}

}
