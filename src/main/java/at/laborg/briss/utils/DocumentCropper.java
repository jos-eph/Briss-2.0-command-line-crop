// $Id: SingleCluster.java 55 2011-02-22 21:45:59Z laborg $
/**
 * Copyright 2010 Gerhard Aigner
 * <p>
 * This file is part of BRISS.
 * <p>
 * BRISS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * BRISS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * BRISS. If not, see http://www.gnu.org/licenses/.
 */
package at.laborg.briss.utils;

import at.laborg.briss.exception.CropException;
import at.laborg.briss.model.CropDefinition;
import at.laborg.briss.utils.rectcapture.CaptureRectangle;

import static at.laborg.briss.utils.CreateScaledBoxArray.createScaledBoxArray;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.itextpdf.text.pdf.SimpleNamedDestination;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public final class DocumentCropper {

	private DocumentCropper() {
	}

	public static File crop(final CropDefinition cropDefinition, String password)
			throws IOException, DocumentException, CropException {

		// check if everything is ready
		if (!BrissFileHandling.checkValidStateAndCreate(cropDefinition.getDestinationFile()))
			throw new IOException("Destination file not valid");

		// read out necessary meta information
		PdfMetaInformation pdfMetaInformation = new PdfMetaInformation(cropDefinition.getSourceFile(), password);

		// first make a copy containing the right amount of pages
		File intermediatePdf = copyToMultiplePages(cropDefinition, pdfMetaInformation, password);

		// now crop all pages according to their ratios
		cropMultipliedFile(cropDefinition, intermediatePdf, pdfMetaInformation, password); // calls the method we use
																							// interactively
		return cropDefinition.getDestinationFile();
	}

	private static File copyToMultiplePages(final CropDefinition cropDefinition,
			final PdfMetaInformation pdfMetaInformation, String password) throws IOException, DocumentException {
		/*
		 * Adds extra pages
		 */

		PdfReader reader = PDFReaderUtil.getPdfReader(cropDefinition.getSourceFile().getAbsolutePath(), password);
		HashMap<String, String> map = SimpleNamedDestination.getNamedDestination(reader, false);
		Document document = new Document();

		File resultFile = File.createTempFile("cropped", ".pdf");
		PdfSmartCopy pdfCopy = new PdfSmartCopy(document, new FileOutputStream(resultFile));
		document.open();

		Map<Integer, List<String>> pageNrToDestinations = new HashMap<Integer, List<String>>();
		for (String single : map.keySet()) {
			StringTokenizer st = new StringTokenizer(map.get(single), " ");
			if (st.hasMoreElements()) {
				String pageNrString = (String) st.nextElement();
				int pageNr = Integer.parseInt(pageNrString);
				List<String> singleList = (pageNrToDestinations.get(pageNr));
				if (singleList == null) {
					singleList = new ArrayList<String>();
					singleList.add(single);
					pageNrToDestinations.put(pageNr, singleList);
				} else {
					singleList.add(single);
				}
			}
		}

		int outputPageNumber = 0;
		for (int pageNumber = 1; pageNumber <= pdfMetaInformation.getSourcePageCount(); pageNumber++) {

			PdfImportedPage pdfPage = pdfCopy.getImportedPage(reader, pageNumber); // original page

			pdfCopy.addPage(pdfPage);
			outputPageNumber++;
			List<String> destinations = pageNrToDestinations.get(pageNumber);
			if (destinations != null) {
				for (String destination : destinations)
					pdfCopy.addNamedDestination(destination, outputPageNumber, new PdfDestination(PdfDestination.FIT));
			}
			List<float[]> rectangles = cropDefinition.getRectanglesForPage(pageNumber);
			for (int j = 1; j < rectangles.size(); j++) { // only executes for extra rectangles
				pdfCopy.addPage(pdfPage); // copies the original page to the resultant file
				outputPageNumber++;
			}
		}
		document.close();
		pdfCopy.close();
		reader.close();
		return resultFile;
	}

	// this method is operating on an object, not returning it
	private static void cropMultipliedFile(final CropDefinition cropDefinition, final File multipliedDocument,
			final PdfMetaInformation pdfMetaInformation, String password) throws DocumentException, IOException {

		CaptureRectangle captureRectangle = new CaptureRectangle();

		PdfReader reader = PDFReaderUtil.getPdfReader(multipliedDocument.getAbsolutePath(), password);

		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(cropDefinition.getDestinationFile()));
		stamper.setMoreInfo(pdfMetaInformation.getSourceMetaInfoMap());

		PdfDictionary pageDict;
		int newPageNumber = 1;

		for (int sourcePageNumber = 1; sourcePageNumber <= pdfMetaInformation
				.getSourcePageCount(); sourcePageNumber++) {

			List<float[]> rectangleList = cropDefinition.getRectanglesForPage(sourcePageNumber);

			// if no crop was selected do nothing
			if (rectangleList.isEmpty()) {
				newPageNumber++;
				continue;
			}

			for (float[] ratios : rectangleList) {
				System.out.println();

				pageDict = reader.getPageN(newPageNumber);

				List<Rectangle> boxes = new ArrayList<Rectangle>();
				boxes.add(reader.getBoxSize(newPageNumber, "media"));
				boxes.add(reader.getBoxSize(newPageNumber, "crop"));
				int rotation = reader.getPageRotation(newPageNumber);

				Rectangle scaledBox = RectangleHandler.calculateScaledRectangle(boxes, ratios, rotation);
				System.out.format("Rectangle %s for newPageNumber %s, sourcePageNumber %s calculated\n",
						RectangleInfo.rectangleToString(scaledBox), newPageNumber, sourcePageNumber);
				captureRectangle.storePageRectangle(sourcePageNumber, scaledBox);

				PdfArray scaleBoxArray = createScaledBoxArray(scaledBox);

				pageDict.put(PdfName.CROPBOX, scaleBoxArray); // doing the work here
				pageDict.put(PdfName.MEDIABOX, scaleBoxArray); // doing the work here

				// increment the pagenumber
				newPageNumber++;

			}
			int[] range = new int[2];
			range[0] = newPageNumber - 1;
			range[1] = pdfMetaInformation.getSourcePageCount() + (newPageNumber - sourcePageNumber);
			SimpleBookmark.shiftPageNumbers(pdfMetaInformation.getSourceBookmarks(), rectangleList.size() - 1, range);
		}
		stamper.setOutlines(pdfMetaInformation.getSourceBookmarks());

		captureRectangle.debugPrintOutMappings();

		stamper.close();
		reader.close();
	}
}
