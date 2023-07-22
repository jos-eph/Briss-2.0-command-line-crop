package at.laborg.briss.cli.utilities.models;

import at.laborg.briss.utils.PdfMetaInformation;
import java.io.File;

public class PdfFileWithMetaInfo {
	private File sourceFile;
	private PdfMetaInformation sourceMetaInformation;

	public File getSourceFile() {
		return sourceFile;
	}
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public PdfMetaInformation getSourceMetaInformation() {
		return sourceMetaInformation;
	}
	public void setPdfMetaInformation(PdfMetaInformation pdfMetaInformation) {
		this.sourceMetaInformation = pdfMetaInformation;
	}

}
