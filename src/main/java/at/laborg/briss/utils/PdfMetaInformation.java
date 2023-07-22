package at.laborg.briss.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

public class PdfMetaInformation {

	private final int sourcePageCount;
	private final HashMap<String, String> sourceMetaInfoMap;
	private final List<HashMap<String, Object>> sourceBookmarks;
	private final String password;

	public String getPassword() {
		return password;
	}

	public PdfMetaInformation(final File source, String password) throws IOException {
		PdfReader reader = PDFReaderUtil.getPdfReader(source.getAbsolutePath(), password);
		this.sourcePageCount = reader.getNumberOfPages();
		this.sourceMetaInfoMap = reader.getInfo();
		this.sourceBookmarks = SimpleBookmark.getBookmark(reader);
		this.password = password;
		reader.close();
	}

	public int getSourcePageCount() {
		return sourcePageCount;
	}

	public HashMap<String, String> getSourceMetaInfoMap() {
		return sourceMetaInfoMap;
	}

	public List<HashMap<String, Object>> getSourceBookmarks() {
		return sourceBookmarks;
	}
}