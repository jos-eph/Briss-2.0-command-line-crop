package at.laborg.briss.utils;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfNumber;

public class CreateScaledBoxArray {
	public static PdfArray createScaledBoxArray(final Rectangle scaledBox) {
		PdfArray scaleBoxArray = new PdfArray();
		scaleBoxArray.add(new PdfNumber(scaledBox.getLeft()));
		scaleBoxArray.add(new PdfNumber(scaledBox.getBottom()));
		scaleBoxArray.add(new PdfNumber(scaledBox.getRight()));
		scaleBoxArray.add(new PdfNumber(scaledBox.getTop()));
		return scaleBoxArray;
	}
}