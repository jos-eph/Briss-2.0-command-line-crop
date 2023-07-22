package at.laborg.briss.cli.utilities;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NumberParserTest {

	String sourceString = "1,6,20,24";

	@Test
	public void testNumberParser() {

		List<Float> expected = List.of(1F, 6F, 20F, 24F);
		List<Float> result = NumberParser.parseFloatsFromDelimitedString(sourceString, 4);
		Assertions.assertEquals(expected, result);
	}

	@Test
	public void testNumberParserErrorWhenWrongNumberArgs() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> NumberParser.parseFloatsFromDelimitedString(sourceString, 5));
	}

}
