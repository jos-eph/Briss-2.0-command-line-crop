package at.laborg.briss.utils;

import java.util.List;

import com.itextpdf.text.Rectangle;

public class RectanglePrinter {

	public static List<Float> rectangleToList(Rectangle rectangle) {
		Float llx = rectangle.getLeft();
		Float lly = rectangle.getBottom();
		Float urx = rectangle.getRight();
		Float ury = rectangle.getTop();
		return List.of(llx, lly, urx, ury);
	}

	public static String rectangleToString(Rectangle rectangle) {
		return String.format("%s\n", RectanglePrinter.rectangleToList(rectangle).toString());
	}
}
