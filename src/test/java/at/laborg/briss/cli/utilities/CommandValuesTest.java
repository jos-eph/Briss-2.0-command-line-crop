package at.laborg.briss.cli.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CommandValuesTest {

	private static Stream<Arguments> provideTestRectCommandValues() {
		return Stream.of(Arguments.of("--odd-rects 1,3,5,7", Boolean.TRUE, null, null),
				Arguments.of("--even-rects 1,3,5,10", Boolean.TRUE, null, null),
				Arguments.of("--odd-rects 1,3,5,7 --even-rects 2,4,10,12", Boolean.FALSE, List.of(1, 3, 5, 7),
						List.of(2, 4, 10, 12)),
				Arguments.of("--odd-rects 1,3,5,7 --rects 10,20,30,40", Boolean.FALSE, List.of(10, 20, 30, 40),
						List.of(10, 20, 30, 40)),
				Arguments.of("--even-rects 1,3,5,7 --rects 10,20,30,40", Boolean.FALSE, List.of(10, 20, 30, 40),
						List.of(10, 20, 30, 40)),
				Arguments.of("--rects 10,20,30,40 --even-rects 1,3,5,7", Boolean.FALSE, List.of(10, 20, 30, 40),
						List.of(10, 20, 30, 40)),
				Arguments.of("--rects 10,20,30,40", Boolean.FALSE, List.of(10, 20, 30, 40), List.of(10, 20, 30, 40)));
	}

	@ParameterizedTest
	@MethodSource("provideTestRectCommandValues")
	void testRectValues(String rectArgs, Boolean expectExceptionOnGetRects, List<Integer> expectedOdd,
			List<Integer> expectedEven) {
		System.out.println("TEST RECT VALUES");
		String[] argArray = rectArgs.split(" ");
		if (Boolean.TRUE.equals(expectExceptionOnGetRects)) {
			CommandValues parsedArgs = CommandValues.parseToWorkDescription(argArray);
			assertThrows(IllegalStateException.class, () -> parsedArgs.getOddRects());
			return;
		}
	}

	@Test
	void testExcludesDetected() {
		String[] inputargs = {"--exclude-pages", "1,2,200,300"};
		CommandValues parsedArgs = CommandValues.parseToWorkDescription(inputargs);

		List<Integer> expected = List.of(1,2,200,300);
		List<Integer> result = parsedArgs.getExcludePages();

		assertEquals(expected, result);
	}

}
