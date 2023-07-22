package at.laborg.briss.cli;

import java.util.List;

import at.laborg.briss.cli.utilities.CommandValues;

public class CommandLineCrop {

	private CommandLineCrop() {
	}

	public static void cropFromCommandLine(CommandValues parsedArgs) {
		if (!CommandValues.isValidJob(parsedArgs)) {
			System.err.println("Invalid arguments passed with crop request; exiting");
			return;
		}

		List<Float> evenRects = parsedArgs.getEvenRects();
		List<Float> oddRects = parsedArgs.getOddRects();
		List<Integer> excludedPages = parsedArgs.getExcludePages();

		// copy crop from autocrop, plug in page values

	}

}
