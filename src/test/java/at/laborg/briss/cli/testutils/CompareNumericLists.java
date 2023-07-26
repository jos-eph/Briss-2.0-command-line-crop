package at.laborg.briss.cli.testutils;

import java.math.BigDecimal;
import java.util.List;

public class CompareNumericLists {
	private CompareNumericLists() {

	}

	public static Boolean numberListsEqual(List<BigDecimal> expected, List<? extends Object> returned,
			BigDecimal permittedError) {
		if (expected.size() != returned.size()) {
			return Boolean.FALSE;
		}

		for (int i = 0; i < expected.size(); i++) {
			BigDecimal resultNumber = new BigDecimal(returned.get(i).toString());
			BigDecimal expectedNumber = expected.get(i);

			BigDecimal difference = expectedNumber.subtract(resultNumber);
			if (difference.abs().compareTo(permittedError) > 0) {
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}
}
