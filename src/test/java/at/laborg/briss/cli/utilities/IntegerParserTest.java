package at.laborg.briss.cli.utilities;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import at.laborg.briss.cli.BrissCMD;
import at.laborg.briss.cli.utilities.CommandValues;
import at.laborg.briss.utils.BrissFileHandling;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntegerParserTest {

    String sourceString = "1,6,20,24";

	@Test
	public void testIntegerParser() {

        List<Integer> expected = List.of(1,6,20,24);
        List<Integer> result = IntegerParser.parseIntsFromDelimitedString(sourceString, 4);
        Assertions.assertEquals(expected, result);
	}

    @Test
	public void testIntegerParserErrorWhenWrongNumberArgs() {
        List<Integer> expected = List.of(1,6,20,24);
        List<Integer> result = IntegerParser.parseIntsFromDelimitedString(sourceString, 4);
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> IntegerParser.parseIntsFromDelimitedString(sourceString, 5));
	}

}
