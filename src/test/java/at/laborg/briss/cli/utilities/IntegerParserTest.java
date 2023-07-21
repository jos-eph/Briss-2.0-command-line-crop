package at.laborg.briss.cli.utilities;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IntegerParserTest {

	String sourceString = "1,6,20,24";

	@Test
	public void testIntegerParser() {

		List<Integer> expected = List.of(1, 6, 20, 24);
		List<Integer> result = IntegerParser.parseIntsFromDelimitedString(sourceString, 4);
		Assertions.assertEquals(expected, result);
	}

	@Test
	public void testIntegerParserErrorWhenWrongNumberArgs() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> IntegerParser.parseIntsFromDelimitedString(sourceString, 5));
	}

}
