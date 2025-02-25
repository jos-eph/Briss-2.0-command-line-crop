package at.laborg.briss.cli.utilities;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import at.laborg.briss.utils.BrissFileHandling;

public class CommandValues {

	private class RectDeclaration {
		List<Float> oddRects = Collections.emptyList();
		List<Float> evenRects = Collections.emptyList();
		List<Float> rects = Collections.emptyList();

	}

	private CommandValues() {
		super();
	}

	private static final String SOURCE_FILE_CMD = "-s";
	private static final String DEST_FILE_CMD = "-d";

	private static final String SPLIT_COLUMN_CMD = "--split-col"; // ignored for command-line crop
	private static final String SPLIT_ROW_CMD = "--split-row"; // ignored for command-line crop

	private static final String FILE_PASSWORD_CMD = "-p";

	private static final String ODD_RECTS = "--odd-rects";
	private static final String EVEN_RECTS = "--even-rects";
	private static final String RECTS = "--rects";
	private static final String EXCLUDE_PAGES = "--exclude-pages";

	private File sourceFile = null;
	private File destFile = null;

	private boolean splitColumns = false;
	private boolean splitRows = false;

	private String password;

	private RectDeclaration rectDeclaration = new RectDeclaration();

	private List<Integer> excludePages = Collections.emptyList();

	private void setExcludePages(List<Integer> pagesToExclude) {
		this.excludePages = pagesToExclude;
	}

	public List<Integer> getExcludePages() {
		return this.excludePages;
	}

	public static CommandValues parseToWorkDescription(final String[] args) {

		CommandValues commandValues = new CommandValues();
		commandValues.rectDeclaration = commandValues.new RectDeclaration();
		int i = 0;
		while (i < args.length) {
			String arg = args[i].trim();
			if (arg.equalsIgnoreCase(SOURCE_FILE_CMD)) {
				if (i < (args.length - 1)) {
					commandValues.setSourceFile(new File(args[i + 1]));
				}
			} else if (arg.equalsIgnoreCase(DEST_FILE_CMD)) {
				if (i < (args.length - 1)) {
					commandValues.setDestFile(new File(args[i + 1]));
				}
			} else if (arg.equalsIgnoreCase(SPLIT_COLUMN_CMD)) {
				commandValues.setSplitColumns();
			} else if (arg.equalsIgnoreCase(SPLIT_ROW_CMD)) {
				commandValues.setSplitRows();
			} else if (arg.equalsIgnoreCase(FILE_PASSWORD_CMD)) {
				commandValues.password = args[i + 1];
			} else if (arg.equalsIgnoreCase(ODD_RECTS)) {
				List<Float> oddRects = NumberParser.parseFloatsFromDelimitedString(args[i + 1], 4);
				commandValues.rectDeclaration.oddRects = oddRects;
			} else if (arg.equalsIgnoreCase(EVEN_RECTS)) {
				List<Float> evenRects = NumberParser.parseFloatsFromDelimitedString(args[i + 1], 4);
				commandValues.rectDeclaration.evenRects = evenRects;
			} else if (arg.equalsIgnoreCase(RECTS)) {
				List<Float> rects = NumberParser.parseFloatsFromDelimitedString(args[i + 1], 4);
				commandValues.rectDeclaration.rects = rects;
			} else if (arg.equalsIgnoreCase(EXCLUDE_PAGES)) {
				if (i < (args.length - 1)) {
					List<Integer> exclude_pages = NumberParser.parseIntegersFromDelimitedString(args[i + 1]);
					commandValues.setExcludePages(exclude_pages);
				}
			}

			i++;
		}

		return commandValues;
	}

	public static boolean isValidJob(final CommandValues job) {
		if (job.getSourceFile() == null) {
			System.out.println("No source file submitted: try \"java -jar <<briss-jar-name>> -s filename.pdf\"");
			return false;
		}
		if (!job.getSourceFile().exists()) {
			System.out.println("File: " + job.getSourceFile() + " doesn't exist");
			return false;
		}
		if (job.getDestFile() == null) {
			File recommendedDest = BrissFileHandling.getRecommendedDestination(job.getSourceFile());
			job.setDestFile(recommendedDest);
			System.out.println("Since no destination was provided destination will be set to  : "
					+ recommendedDest.getAbsolutePath());
		}
		try {
			BrissFileHandling.checkValidStateAndCreate(job.getDestFile());
		} catch (IllegalArgumentException e) {
			System.out.println("Destination file couldn't be created!");
			return false;
		} catch (IOException e) {
			System.out.println("IO Error while creating destination file.");
			e.getStackTrace();
			return false;
		}

		return true;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(final File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public File getDestFile() {
		return destFile;
	}

	public void setDestFile(final File destFile) {
		this.destFile = destFile;
	}

	public void setSplitColumns() {
		this.splitColumns = true;
	}

	public boolean isSplitColumns() {
		return splitColumns;
	}

	public void setSplitRows() {
		this.splitRows = true;
	}

	public boolean isSplitRows() {
		return splitRows;
	}

	public String getPassword() {
		return password;
	}

	public Boolean rectangleDeclared() {
		Boolean separateRectsDeclared = (!rectDeclaration.evenRects.isEmpty() && !rectDeclaration.oddRects.isEmpty());
		Boolean mainRectsDeclared = !rectDeclaration.rects.isEmpty();

		return separateRectsDeclared || mainRectsDeclared;
	}

	private Boolean oddEvenRectsFullySpecified() {
		if (!rectangleDeclared()) {
			throw new IllegalStateException();
		}

		return (!rectDeclaration.evenRects.isEmpty() && !rectDeclaration.oddRects.isEmpty());
	}

	public List<Float> getEvenRects() {
		if (!rectangleDeclared()) {
			throw new IllegalStateException();
		}

		return oddEvenRectsFullySpecified() ? rectDeclaration.evenRects : rectDeclaration.rects;
	}

	public List<Float> getOddRects() {
		if (!rectangleDeclared()) {
			throw new IllegalStateException();
		}

		return oddEvenRectsFullySpecified() ? rectDeclaration.oddRects : rectDeclaration.rects;
	}

}