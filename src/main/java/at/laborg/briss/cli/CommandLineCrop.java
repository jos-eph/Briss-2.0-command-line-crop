package at.laborg.briss.cli;

import at.laborg.briss.cli.utilities.CommandValues;

public class CommandLineCrop {

	private CommandLineCrop() {
	}

	public static void cropFromCommandLine(CommandValues parsedArgs) {
		if (!CommandValues.isValidJob(parsedArgs)) {
			System.err.println("Invalid arguments passed with crop request; exiting");
			return;
		}

	}

}
