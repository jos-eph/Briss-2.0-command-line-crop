package at.laborg.briss.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;
import java.nio.file.Files;

import at.laborg.briss.Briss;

@TestInstance(Lifecycle.PER_CLASS)
class CommandLineCropTest {
	private java.nio.file.Path outputTempFilePath;

	private static final int NUMBER_OF_PAGES_IN_TEST = 28;
	private static final String PATH_TO_TEST_PDF = "src/test/resources/pdfs/CREATIVE_COMMONS.pdf";

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
			PdfReader reader = new PdfReader(getOutputTempFilePath().toAbsolutePath().toString());
			List<Float> expectedOddCoordinates = List.of(50.0F, 165F, 960F, 538.0F);
			List<Float> expectedEvenCoordinates = List.of(69.0F, 163.0F, 881F, 535.0F);

			IntStream.rangeClosed(1, NUMBER_OF_PAGES_IN_TEST).forEach((pageNumber) -> {
				PdfDictionary pageDictionary = reader.getPageN(pageNumber);
				pageDictionary.get(PdfName.CROPBOX); // figure out what to do with these
				pageDictionary.get(PdfName.MEDIABOX);
			});

		} catch (IOException ex) {
			Assertions.fail("Exception occurred during test", ex);
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
					"--even-rects", "69.0,163.0,881,535.0", "--exclude-pages", "3"};
		} catch (IOException ex) {
			Assertions.fail("Couldn't initialize test");

		}

	}

}
