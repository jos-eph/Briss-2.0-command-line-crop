package at.laborg.briss.cli.testutils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfNumber;

public class PdfArrayNumbersToListBigDecimal {
	private PdfArrayNumbersToListBigDecimal() {

	}

	public static List<BigDecimal> getBigDecimalList(PdfArray pdfArrayOfNumbers) {
		List<BigDecimal> mutableCollectedList = new ArrayList<>();
		pdfArrayOfNumbers.forEach((pdfObject) -> {
			PdfNumber pdfNumber = (PdfNumber) pdfObject;
			mutableCollectedList.add(BigDecimal.valueOf(pdfNumber.doubleValue()));
		});
		return Collections.unmodifiableList(mutableCollectedList);
	}
}
