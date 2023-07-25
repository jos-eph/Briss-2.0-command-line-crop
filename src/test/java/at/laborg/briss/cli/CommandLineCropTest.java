package at.laborg.briss.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import at.laborg.briss.Briss;

class CommandLineCropTest {
	private static final String RECOMMENDED_ENDING = "_manual_arg_cropped_test.pdf";

	private java.nio.file.Path outputTempFilePath;

	private Path getOutputTempFilePath() {
		return outputTempFilePath;
	}

	private File getTempFileName(File sourceDocument) {
		String originalName = sourceDocument.getAbsolutePath();
		String cropped_file_name = originalName.substring(0, originalName.length() - 4) + RECOMMENDED_ENDING;
		return new File(cropped_file_name);
	}

	private String[] getTestArgs() throws IOException {
		java.nio.file.Path sourceDocument = Paths.get("src/test/resources/pdfs/CREATIVE_COMMONS.pdf");
		java.nio.file.Path tempDirectory = Files.createTempDirectory(CommandLineCropTest.class.getCanonicalName());
		File outputFileWithTempSuffix = getTempFileName(sourceDocument.toFile());

		this.outputTempFilePath = tempDirectory.resolve(outputFileWithTempSuffix.getName());

		return new String[]{"-s", sourceDocument.toString(), "-d",
				tempDirectory.resolve(getOutputTempFilePath()).toString(), "--odd-rects", "50.0,165,960,538.0",
				"--even-rects", "69.0,163.0,881,535.0", "--exclude-pages", "3"};
	}

	@Test
	public void testCropAsExpected() {
        try {
            Briss.main(getTestArgs());
            System.out.println(getOutputTempFilePath());            
        } catch (IOException ex) {
            Assertions.fail("Exception occurred during test", ex);
        }


	}

}
