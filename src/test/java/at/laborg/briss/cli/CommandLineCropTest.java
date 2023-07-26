package at.laborg.briss.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.nio.file.Files;

import at.laborg.briss.Briss;
import at.laborg.briss.cli.testutils.CompareNumericLists;
import at.laborg.briss.cli.testutils.PdfArrayNumbersToListBigDecimal;

@TestInstance(Lifecycle.PER_CLASS)
class CommandLineCropTest {
	private java.nio.file.Path outputTempFilePath;

	private static final int NUMBER_OF_PAGES_IN_TEST = 28;
	private static final String PATH_TO_TEST_PDF = "src/test/resources/pdfs/CREATIVE_COMMONS.pdf";
	private static final BigDecimal PERMITTED_ERROR = new BigDecimal("0.2");

	private String[] args;

	private File getTempFileName(File sourceDocument) {
		final String testFileEnding = "_manual_arg_cropped_test.pdf";

		String originalName = sourceDocument.getAbsolutePath();
		String cropped_file_name = originalName.substring(0, originalName.length() - 4) + testFileEnding;
		return new File(cropped_file_name);
	}

	@Test
	public void testCropAsExpected() {
		try {
			Briss.main(getArgs());
			PdfReader reader = new PdfReader(getOutputTempFilePath().toAbsolutePath().toString());
			List<Float> expectedOddCoordinates = List.of(50.0F, 165F, 960F, 538.0F);
			List<Float> expectedEvenCoordinates = List.of(69.0F, 163.0F, 881F, 535.0F);
			Set<Integer> expectedExcludes = Set.of(3, 12);

			IntStream.rangeClosed(1, NUMBER_OF_PAGES_IN_TEST).forEach((pageNumber) -> {
				System.out.format("Page: %s\n", pageNumber);
				PdfDictionary pageDictionary = reader.getPageN(pageNumber);
				PdfArray mediaBox = (PdfArray) pageDictionary.get(PdfName.MEDIABOX);

				List<BigDecimal> mediaBoxList = PdfArrayNumbersToListBigDecimal.getBigDecimalList(mediaBox);

				System.out.format("Mediabox is %s\n", mediaBoxList);

				List<Float> expectedForOddOrEven = pageNumber % 2 == 0
						? expectedEvenCoordinates
						: expectedOddCoordinates;
				System.out.format("expectedForOddOrEven: %s\n", expectedForOddOrEven);
				Boolean matchesForOddOrEven = CompareNumericLists.numberListsEqual(mediaBoxList, expectedForOddOrEven,
						PERMITTED_ERROR);

				if (expectedExcludes.contains(pageNumber)) {
					Assertions.assertFalse(matchesForOddOrEven);
				} else {
					Assertions.assertTrue(matchesForOddOrEven);
				}
			});

		} catch (IOException ex) {
			Assertions.fail("Exception occurred during test", ex);
			ex.printStackTrace();
		}
	}

	public String[] getArgs() {
		return args;
	}

	public java.nio.file.Path getOutputTempFilePath() {
		return outputTempFilePath;
	}

	@BeforeAll
	void setup() {
		try {
			java.nio.file.Path sourceDocument = Paths.get(PATH_TO_TEST_PDF);
			java.nio.file.Path tempDirectory = Files.createTempDirectory(CommandLineCropTest.class.getCanonicalName());
			File outputFileWithTempSuffix = getTempFileName(sourceDocument.toFile());

			this.outputTempFilePath = tempDirectory.resolve(outputFileWithTempSuffix.getName());

			this.args = new String[]{"-s", sourceDocument.toString(), "-d",
					tempDirectory.resolve(getOutputTempFilePath()).toString(), "--odd-rects", "50.0,165,960,538.0",
					"--even-rects", "69.0,163.0,881,535.0", "--exclude-pages", "3,12"};
		} catch (IOException ex) {
			Assertions.fail("Couldn't initialize test");
			ex.printStackTrace();

		}

	}

}
