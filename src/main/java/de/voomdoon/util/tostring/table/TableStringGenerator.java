package de.voomdoon.util.tostring.table;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.voomdoon.util.commons.string.WhitespaceCounter;

//FEATURE #14: support cells with line break

//FEATURE #16: support configurable alignment

//FEATURE #26: Support correct width calculation for Unicode symbols

//FEATURE #31: Separate column separator from column padding

/**
 * Generates a formatted string representation of a table from a matrix of {@link String} values.
 * <p>
 * <ul>
 * <li>Provides builder-based configuration.</li>
 * <li>Supports configurable column separators, null value representation, and alignment for numbers and text.</li>
 * <li>Partially supports Unicode width (may not be fully accurate for all Unicode symbols).</li>
 * </ul>
 * <p>
 * Usage example:
 * 
 * <pre>
 * {@code
 * String[][] data = { { "A", "1.23" }, { "B", "4.56" } };
 * String result = TableStringGenerator.DEFAULT.toString(data);
 * }
 * </pre>
 *
 * @author André Schulz
 * @since 0.1.0
 */
public class TableStringGenerator {

	/**
	 * Builder for {@link TableStringGenerator}.
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	public static class Builder {

		/**
		 * @since 0.1.0
		 */
		private String columnSeparator = DefaultValues.COLUMN_SEPARATOR;

		/**
		 * @since 0.1.0
		 */
		private String nullValue = DefaultValues.NULL_VALUE;

		/**
		 * Builds a new {@link TableStringGenerator}.
		 * 
		 * @return {@link TableStringGenerator}
		 * 
		 * @since 0.1.0
		 */
		public TableStringGenerator build() {
			return new TableStringGenerator(nullValue, columnSeparator);
		}

		/**
		 * Sets the column separator. Defaults to "{@code  | }".
		 * 
		 * @param columnSeparator
		 *            The {@link String} to use as column separator
		 * @return this {@link Builder}
		 * @since 0.1.0
		 */
		public Builder setColumnSeparator(String columnSeparator) {
			this.columnSeparator = columnSeparator;

			return this;
		}

		/**
		 * @param string
		 *            The {@link String} to use for null values
		 * @return this {@link Builder}
		 * @since 0.1.0
		 */
		public Builder setNullValue(String string) {
			this.nullValue = string;

			return this;
		}
	}

	/**
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private class Context {

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		private class ColumnContext {

			/**
			 * @author André Schulz
			 *
			 * @since 0.1.0
			 */
			private class Initializer {

				/**
				 * @param column
				 *            List of column values as {@link java.util.List}
				 * @return maximum text width as int
				 * @since 0.1.0
				 */
				private int getTextWidth(List<String> column) {
					return column.stream().mapToInt(TableStringGenerator.this::getLength).max().orElse(0);
				}

				/**
				 * @param column
				 *            List of column values as {@link java.util.List}
				 * @since 0.1.0
				 */
				private void initNumberWidths(List<String> column) {
					for (String cell : column) {
						initNumberWidths(cell);
					}
				}

				/**
				 * @param cell
				 *            Cell value to analyze as {@link String}
				 * @since 0.1.0
				 */
				private void initNumberWidths(String cell) {
					// TODO rework
					if (cell == null) {
						return;
					} else if (INTEGER_PATTERN.matcher(cell).matches()) {
						int left = cell.length();
						numberWidthLeft = Math.max(numberWidthLeft, left);
					} else if (REAL_PATTERN.matcher(cell).matches() || DATE_TIME_MS_PATTERN.matcher(cell).matches()) {
						decimalAligned = true;
						int index = cell.indexOf('.');
						int left = index;
						int right = cell.length() - index - 1;

						numberWidthLeft = Math.max(numberWidthLeft, left);
						numberWidthRight = Math.max(numberWidthRight, right);
					}
				}
			}

			/**
			 * @since 0.1.0
			 */
			private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

			/**
			 * @since 0.1.0
			 */
			private static final Pattern DATE_TIME_MS_PATTERN = Pattern
					.compile("(\\d{4}-\\d{2}-\\d{2} )?\\d{2}:\\d{2}:\\d{2}\\.\\d+");

			/**
			 * @since 0.1.0
			 */
			private static final Pattern DATE_TIME_PATTERN = Pattern
					.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}(?::\\d{2}(?:\\.\\d+)?)?");

			private static final Pattern DECIMAL_ALIGN_PATTERN;

			/**
			 * @since 0.1.0
			 */
			private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");

			/**
			 * @since 0.1.0
			 */
			private static final Pattern REAL_PATTERN = Pattern.compile("(\\d?\\.\\d+|\\d+\\.\\d?)");

			/**
			 * @since 0.1.0
			 */
			private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}(:\\d{2})?");

			static {
				DECIMAL_ALIGN_PATTERN = Pattern.compile( //
						REAL_PATTERN.pattern() //
								+ "|" + DATE_TIME_MS_PATTERN.pattern()//
								+ "|" + INTEGER_PATTERN.pattern() //
								+ "|" + TIME_PATTERN.pattern() //
								+ "|" + DATE_PATTERN.pattern() //
								+ "|" + DATE_TIME_PATTERN.pattern());
			}

			/**
			 * @since 0.1.0
			 */
			private boolean decimalAligned = false;

			/**
			 * @since 0.1.0
			 */
			private int numberWidthLeft;

			/**
			 * @since 0.1.0
			 */
			private int numberWidthRight;

			/**
			 * @since 0.1.0
			 */
			private int textWidth;

			/**
			 * @param column
			 * @since 0.1.0
			 */
			public ColumnContext(List<String> column) {
				Initializer initializer = new Initializer();
				textWidth = initializer.getTextWidth(column);
				initializer.initNumberWidths(column);
			}

			/**
			 * @param cell
			 *            cell value as {@link String}
			 * @return {@link Padding} for the cell
			 * @since 0.1.0
			 */
			public Padding getPadding(String cell) {
				if (cell == null) {
					return getTextPadding(cell);
				}

				boolean numberLike = isNumberLike(cell);

				if (decimalAligned && numberLike) {
					return getRealPadding(cell);
				} else if (numberLike) {
					return getRightAlignedPadding(cell);
				}

				return getTextPadding(cell);
			}

			/**
			 * @param cell
			 *            cell value as {@link String}
			 * @return {@link Padding} for real-aligned cell
			 * @since 0.1.0
			 */
			private Padding getRealPadding(String cell) {
				int index = cell.indexOf('.');
				int left;
				int right;

				if (index >= 0) {
					left = index;
					right = cell.length() - index - 1;
				} else {
					left = getLength(cell);
					right = 0;
				}

				int leftPadding = numberWidthLeft - left;
				int rightPadding = numberWidthRight - right;

				int totalWidth = leftPadding + getLength(cell) + rightPadding;
				if (index < 0) {
					totalWidth += 1; // simulate decimal point width
				}

				if (totalWidth < textWidth) {
					leftPadding += textWidth - totalWidth;
				}

				return new Padding(" ".repeat(leftPadding), //
						" ".repeat(rightPadding + (index < 0 ? 1 : 0)) // ← simulate the missing dot visually
				);
			}

			/**
			 * @param cell
			 *            cell value as {@link String}
			 * @return {@link Padding} for right-aligned cell
			 * @since 0.1.0
			 */
			private Padding getRightAlignedPadding(String cell) {
				return new Padding(" ".repeat(textWidth - cell.length()), "");
			}

			/**
			 * @param cell
			 *            cell value as {@link String}
			 * @return {@link Padding} for text cell
			 * @since 0.1.0
			 */
			private Padding getTextPadding(String cell) {
				return new Padding(//
						"", //
						" ".repeat(textWidth - getLength(cell)));
			}

			/**
			 * @param cell
			 *            cell value as {@link String}
			 * @return {@code true} if cell is number-like, {@code false} otherwise
			 * @since 0.1.0
			 */
			private boolean isNumberLike(String cell) {
				return DECIMAL_ALIGN_PATTERN.matcher(cell).matches();
			}
		}

		/**
		 * @since 0.1.0
		 */
		private ColumnContext[] columns;

		/**
		 * @param body
		 *            Table body as {@link String} matrix
		 * @param headline
		 *            Table headline as {@link String} array
		 * @param iColumn
		 *            Column index as int
		 * @return new {@link ColumnContext}
		 * @since 0.1.0
		 */
		private Context.ColumnContext createColumnContext(String[][] body, String[] headline, int iColumn) {
			List<String> column = getColumn(body, headline, iColumn);

			return new ColumnContext(column);
		}

		/**
		 * @param body
		 *            Table body as {@link String} matrix
		 * @param headline
		 *            Table headline as {@link String} array
		 * @param iColumn
		 *            Column index as int
		 * @return List of column values as {@link List}
		 * @since 0.1.0
		 */
		private List<String> getColumn(String[][] body, String[] headline, int iColumn) {
			List<String> result = new ArrayList<>();

			if (headline != null) {
				result.add(headline[iColumn]);
			}

			for (int iRow = 0; iRow < body.length; iRow++) {
				result.add(body[iRow][iColumn]);
			}

			return result;
		}
	}

	/**
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private static final class DefaultValues {

		/**
		 * @since 0.1.0
		 */
		private static final String COLUMN_SEPARATOR = " │ ";

		/**
		 * @since 0.1.0
		 */
		private static final String NULL_VALUE = "";
	}

	/**
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private record Padding(String before, String after) {
	}

	/**
	 * Default {@link TableStringGenerator} instance.
	 * 
	 * @since 0.1.0
	 */
	public static final TableStringGenerator DEFAULT = builder().build();

	/**
	 * Returns a new {@link Builder} for {@link TableStringGenerator}.
	 * 
	 * @return {@link Builder}
	 * @since 0.1.0
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * @since 0.1.0
	 */
	private final String columnSeparator;

	/**
	 * @since 0.1.0
	 */
	private final String nullValue;

	/**
	 * @param nullValue
	 *            The {@link String} to use for null values
	 * @param columnSeparator
	 *            The {@link String} to use as column separator
	 * @since 0.1.0
	 */
	private TableStringGenerator(String nullValue, String columnSeparator) {
		this.nullValue = nullValue;
		this.columnSeparator = columnSeparator;
	}

	/**
	 * Converts the given table body to a {@link String}.
	 * 
	 * @param body
	 *            The table body as two-dimensional {@link String} array
	 * @return {@link String}
	 * @since 0.1.0
	 */
	public String toString(String[][] body) {
		return toString(body, null);
	}

	/**
	 * Converts the given table body and headline to a {@link String}.
	 * 
	 * @param body
	 *            The table body as two-dimensional {@link String} array
	 * @param headline
	 *            The table headline as {@link String} array
	 * @return {@link String}
	 * @since 0.1.0
	 */
	public String toString(String[][] body, String[] headline) {
		if (headline == null && body.length == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		Context context = createContext(body, headline);

		if (headline != null) {
			appendRow(headline, sb, context);
			appendHeadlineSeparator(headline, sb, context);
		}

		for (int iRow = 0; iRow < body.length; iRow++) {
			String[] row = body[iRow];

			appendRow(row, sb, context);
		}

		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	/**
	 * @param row
	 *            Table row as {@link String} array
	 * @param sb
	 *            StringBuilder for result as {@link StringBuilder}
	 * @param iColumn
	 *            Column index as int
	 * @param context
	 *            Table context as {@link Context}
	 * @since 0.1.0
	 */
	private void appendCell(String[] row, StringBuilder sb, int iColumn, Context context) {
		Padding padding = getPadding(row, iColumn, context);

		sb.append(padding.before + format(row[iColumn]) + padding.after);
	}

	/**
	 * @param headline
	 *            Table headline as {@link String} array
	 * @param sb
	 *            StringBuilder for result as {@link StringBuilder}
	 * @param context
	 *            Table context as {@link Context}
	 * @since 0.1.0
	 */
	private void appendHeadlineSeparator(String[] headline, StringBuilder sb, Context context) {
		for (int iColumn = 0; iColumn < headline.length; iColumn++) {
			sb.append("─".repeat(context.columns[iColumn].textWidth));

			if (iColumn < headline.length - 1) {
				sb.append("─".repeat(WhitespaceCounter.SPACE.countLeading(columnSeparator)));

				if (columnSeparator.equals(DefaultValues.COLUMN_SEPARATOR)) {
					sb.append("┼");
				} else {
					sb.append(columnSeparator);
				}

				sb.append("─".repeat(WhitespaceCounter.SPACE.countTrailing(columnSeparator)));
			}
		}

		sb.append("\n");
	}

	/**
	 * @param row
	 *            Table row as {@link String} array
	 * @param sb
	 *            StringBuilder for result as {@link StringBuilder}
	 * @param context
	 *            Table context as {@link Context}
	 * @since 0.1.0
	 */
	private void appendRow(String[] row, StringBuilder sb, Context context) {
		for (int iColumn = 0; iColumn < row.length; iColumn++) {
			appendCell(row, sb, iColumn, context);

			if (iColumn < row.length - 1) {
				sb.append(columnSeparator);
			}
		}

		sb.append("\n");
	}

	/**
	 * @param body
	 *            Table body as {@link String} matrix
	 * @param headline
	 *            Table headline as {@link String} array
	 * @return Context for table as {@link Context}
	 * @since 0.1.0
	 */
	private Context createContext(String[][] body, String[] headline) {
		Context context = new Context();
		context.columns = new Context.ColumnContext[getColumnCount(body, headline)];

		for (int iColumn = 0; iColumn < context.columns.length; iColumn++) {
			context.columns[iColumn] = context.createColumnContext(body, headline, iColumn);
		}

		return context;
	}

	/**
	 * @param string
	 *            String to format as {@link String}
	 * @return formatted {@link String}
	 * @since 0.1.0
	 */
	private String format(String string) {
		if (string == null) {
			return nullValue;
		} else {
			return string;
		}
	}

	/**
	 * @param body
	 *            Table body as {@link String} matrix
	 * @param headline
	 *            Table headline as {@link String} array
	 * @return column count as int
	 * @since 0.1.0
	 */
	private int getColumnCount(String[][] body, String[] headline) {
		if (headline != null) {
			for (int iRow = 0; iRow < body.length; iRow++) {
				if (body[iRow].length != headline.length) {
					throw new IllegalArgumentException("Row " + iRow + " has a different column count ("
							+ body[iRow].length + ") than the headline (" + headline.length + ").");
				}
			}

			return headline.length;
		}

		return body[0].length;
	}

	/**
	 * @param string
	 *            {@link String} to get length of
	 * @return length of string as int
	 * @since 0.1.0
	 */
	private int getLength(String string) {
		return string == null ? nullValue.length() : string.length();
	}

	/**
	 * @param row
	 *            Table row as {@link String} array
	 * @param iColumn
	 *            Column index as int
	 * @param context
	 *            Table context as {@link Context}
	 * @return {@link Padding} for cell
	 * @since 0.1.0
	 */
	private Padding getPadding(String[] row, int iColumn, Context context) {
		return context.columns[iColumn].getPadding(row[iColumn]);
	}
}