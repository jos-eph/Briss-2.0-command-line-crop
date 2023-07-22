package at.laborg.briss.cli;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import at.laborg.briss.cli.utilities.CommandValues;
import at.laborg.briss.cli.utilities.models.CropPreferences;
import at.laborg.briss.cli.utilities.models.PdfFileWithMetaInfo;
import at.laborg.briss.utils.PDFReaderUtil;
import at.laborg.briss.utils.PdfMetaInformation;
import static at.laborg.briss.utils.CreateScaledBoxArray.createScaledBoxArray;

import java.nio.file.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CommandLineCrop {
	private CommandLineCrop() {
	}

	private static File getTempFile(File sourceFile) throws IOException {
		File tempFile = null;
		System.out.println("Creating tempfile...");
		tempFile = File.createTempFile("exactCopy", ".pdf");
		System.out.println("Copying source to tempfile...");
		Files.copy(sourceFile.toPath(), tempFile.toPath());
		return tempFile;
	}

	private static PdfFileWithMetaInfo getWrappedSource(File sourceFile, String password) {
		PdfFileWithMetaInfo copiedSourceWithMetaInfo = new PdfFileWithMetaInfo();

		try {
			copiedSourceWithMetaInfo.setSourceFile(getTempFile(sourceFile));
			copiedSourceWithMetaInfo.setPdfMetaInformation(new PdfMetaInformation(sourceFile, password));
		} catch (IOException ex) {
			System.err.println("Error opening source file. Exiting...");
			System.exit(-1);
		}

		return copiedSourceWithMetaInfo;
	}

	private static Rectangle floatListToRect(List<Float> coordinates) {
		if (coordinates.size() != 4) {
			throw new IllegalArgumentException("Rectangles must have 4 coordinates");
		}

		return new Rectangle(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3));

	}

	private static void cropFromArguments(PdfFileWithMetaInfo sourcePdf, CropPreferences cropPreferences,
			File destinationFile) {

		Rectangle evenRectangle = floatListToRect(cropPreferences.getEvenRects());
		Rectangle oddRectangle = floatListToRect(cropPreferences.getOddRects());

		try {
			PdfReader reader = PDFReaderUtil.getPdfReader(sourcePdf.getSourceFile().getAbsolutePath(),
					sourcePdf.getSourceMetaInformation().getPassword());
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destinationFile));
			stamper.setMoreInfo(sourcePdf.getSourceMetaInformation().getSourceMetaInfoMap());
			PdfDictionary pageDict;

			for (int sourcePageNumber = 1; sourcePageNumber <= sourcePdf.getSourceMetaInformation()
					.getSourcePageCount(); sourcePageNumber++) {

				pageDict = reader.getPageN(sourcePageNumber);

				if (cropPreferences.getExcludedPages().contains(sourcePageNumber)) {
					continue; // is the original Rect preserved or not??
				}

				Rectangle sourceRect = sourcePageNumber % 2 == 0 ? evenRectangle : oddRectangle;
				PdfArray scaledBoxArray = createScaledBoxArray(sourceRect);

				pageDict.put(PdfName.CROPBOX, scaledBoxArray);
				pageDict.put(PdfName.MEDIABOX, scaledBoxArray);
			}

			// range, bookmarks - look at DocumentCropper

		} catch (IOException | DocumentException ex) {
			System.err.println("I/O or PDF error during command-line cropping; exiting...");
			System.exit(-1);
		}

	}

	public static void cropFromCommandLine(CommandValues parsedArgs) {
		if (!CommandValues.isValidJob(parsedArgs)) {
			System.err.println("Invalid arguments passed with crop request; exiting");
			return;
		}

		Set<Integer> excludedPages = parsedArgs.getExcludePages().stream().collect(Collectors.toSet());

		PdfFileWithMetaInfo copiedSourceFile = getWrappedSource(parsedArgs.getSourceFile(), parsedArgs.getPassword());
		CropPreferences cropPreferences = new CropPreferences(parsedArgs.getOddRects(), parsedArgs.getEvenRects(),
				parsedArgs.getExcludePages());

		cropFromArguments(copiedSourceFile, cropPreferences, parsedArgs.getDestFile());

	}

}
