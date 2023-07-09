package at.laborg.briss;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import at.laborg.briss.cli.BrissCMD;
import at.laborg.briss.cli.utilities.CommandValues;
import at.laborg.briss.utils.BrissFileHandling;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class AutoCropTest {
	private CommandValues toParsedArgs(String[] args) {
		assertDoesNotThrow(() -> CommandValues.parseToWorkDescription(args));
		return CommandValues.parseToWorkDescription(args);
	}

	@Test
	public void testAutocrop() throws Exception {
		Path outputDirectory = Files.createTempDirectory(AutoCropTest.class.getCanonicalName());

		Path documentPath = Paths.get("src/test/resources/pdfs/CREATIVE_COMMONS.pdf");

		File recommended = BrissFileHandling.getRecommendedDestination(documentPath.toFile());

		String[] args = new String[]{"-s", documentPath.toString(), "-d",
				outputDirectory.resolve(recommended.getName()).toString()};

		assertDoesNotThrow(() -> BrissCMD.autoCrop(toParsedArgs(args)));
	}

	@Test
	public void testCrop() throws Exception {
		Path outputDirectory = Files.createTempDirectory(AutoCropTest.class.getCanonicalName());

		Path documentPath = Paths.get("src/test/resources/pdfs/example.pdf");

		String[] jobargs = new String[]{"-s", documentPath.toString(), "-d", outputDirectory.toString()};

		assertDoesNotThrow(() -> BrissCMD.autoCrop(toParsedArgs(jobargs)));
	}

	@Test
	public void testCropWithPasswordProtectedFile() throws Exception {
		Path outputDirectory = Files.createTempDirectory(AutoCropTest.class.getCanonicalName());

		Path documentPath = Paths.get("src/test/resources/pdfs/example-protected.pdf");

		String[] jobargs = new String[]{"-s", documentPath.toString(), "-p", "secret", "-d",
				outputDirectory.toString()};

		assertDoesNotThrow(() -> BrissCMD.autoCrop(toParsedArgs(jobargs)));
	}
}
