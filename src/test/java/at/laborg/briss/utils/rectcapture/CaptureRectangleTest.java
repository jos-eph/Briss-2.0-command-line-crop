package at.laborg.briss.utils.rectcapture;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.itextpdf.text.Rectangle;

import at.laborg.briss.utils.RectangleInfo;

@TestInstance(Lifecycle.PER_CLASS)
class CaptureRectangleTest {

	CaptureRectangle testCaptureRectangle;

	private CaptureRectangle buildTestCaptureRectangle() {
		CaptureRectangle populatedCaptureRectangle = new CaptureRectangle();

		populatedCaptureRectangle.storePageRectangle(1, new Rectangle(2.6F, 5.2F, 100F, 500F));
		populatedCaptureRectangle.storePageRectangle(1, new Rectangle(5.14F, 4.2F, 400F, 300F));
		populatedCaptureRectangle.storePageRectangle(3, new Rectangle(2.6F, 4.2F, 400F, 500F));
		populatedCaptureRectangle.storePageRectangle(4, new Rectangle(600F, 650F, 250F, 350F));
		populatedCaptureRectangle.storePageRectangle(4, new Rectangle(300F, 350F, 750F, 200F));
		populatedCaptureRectangle.storePageRectangle(6, new Rectangle(300F, 350F, 750F, 350F));
		populatedCaptureRectangle.storePageRectangle(7, new Rectangle(2000F, 2500F, 2502F, 1000F));

		populatedCaptureRectangle.computeBiggestRects();

		return populatedCaptureRectangle;
	}

	@Test
	void testBiggestRectsFound() {
		Map<Integer, List<Float>> expected = Map.of(1, List.of(2.6F, 4.2F, 400F, 500F), 3,
				List.of(2.6F, 4.2F, 400F, 500F), 4, List.of(300F, 350F, 750F, 350F), 6, List.of(300F, 350F, 750F, 350F),
				7, List.of(2000F, 2500F, 2502F, 1000F));
		Map<Integer, List<Float>> result = testCaptureRectangle.getBiggestPageRects();

		Assertions.assertEquals(expected, result);
	}

	@Test
	void testUniqueBiggestRectsFound() {
		Map<List<Float>, List<Integer>> expected = Map.of(List.of(2.6F, 4.2F, 400F, 500F), List.of(1, 3),
				List.of(300F, 350F, 750F, 350F), List.of(4, 6), List.of(2000F, 2500F, 2502F, 1000F), List.of(7));

		Map<List<Float>, List<Integer>> result = testCaptureRectangle.getUniqueBiggestRects();

		Assertions.assertEquals(expected, result);
	}

	@BeforeAll
	void setup() {
		testCaptureRectangle = buildTestCaptureRectangle();
	}
}