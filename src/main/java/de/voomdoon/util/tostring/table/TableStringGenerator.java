package de.voomdoon.util.tostring.table;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.voomdoon.util.commons.string.StringUtil;

//FEATURE date and time right alignment 

//FEATURE: support symbols with more space (e.g. ja)

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class TableStringGenerator {

	/**
	 * DOCME add JavaDoc for TableStringGenerator
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	public static class Builder {

		/**
		 * @since 0.1.0
		 */
		private String columnSeparator = DEFAULT_COLUMN_SEPARATOR;

		/**
		 * @since 0.1.0
		 */
		private String nullValue = DEFAULT_NULL_VALUE;

		/**
		 * DOCME add JavaDoc for method build
		 * 
		 * @since 0.1.0
		 */
		public TableStringGenerator build() {
			return new TableStringGenerator(nullValue, columnSeparator);
		}

		/**
		 * @param columnSeparator
		 *            DOCME
		 * @return this {@link Builder}
		 * @since 0.1.0
		 */
		public Builder setColumnSeparator(String columnSeparator) {
			this.columnSeparator = columnSeparator;

			return this;
		}

		/**
		 * @param string
		 *            DOCME
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
				 * @return
				 * @since 0.1.0
				 */
				private int getTextWidth(List<String> column) {
					return column.stream().mapToInt(TableStringGenerator.this::getLength).max().orElse(0);
				}

				/**
				 * @param column
				 * @since 0.1.0
				 */
				private void initNumberWidths(List<String> column) {
					for (String cell : column) {
						initNumberWidths(cell);
					}
				}

				/**
				 * @param cell
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
					.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}(:\\d{2})?");

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
			 * DOCME add JavaDoc for constructor ColumnContext
			 * 
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
			 * @return
			 * @since 0.1.0
			 */
			public Padding getPadding(String cell) {
				// TODO rework

				if (cell == null) {
					return getTextPadding(cell);
				} else if (INTEGER_PATTERN.matcher(cell).matches()) {
					return getRightAllignedPadding(cell);
				} else if (REAL_PATTERN.matcher(cell).matches()) {
					return getRealPadding(cell);
				} else if (TIME_PATTERN.matcher(cell).matches()) {
					return getRightAllignedPadding(cell);
				} else if (DATE_PATTERN.matcher(cell).matches()) {
					return getRightAllignedPadding(cell);
				} else if (DATE_TIME_PATTERN.matcher(cell).matches()) {
					return getRightAllignedPadding(cell);
				} else if (DATE_TIME_MS_PATTERN.matcher(cell).matches()) {
					return getRealPadding(cell);
				}

				return getTextPadding(cell);
			}

			/**
			 * @param cell
			 * @return
			 * @since 0.1.0
			 */
			private Padding getRealPadding(String cell) {
				int index = cell.indexOf('.');
				int left = index;
				int right = cell.length() - index - 1;

				int leftPadding = numberWidthLeft - left;
				int rightPadding = numberWidthRight - right;

				int totalWidth = leftPadding + cell.length() + rightPadding;

				if (totalWidth < textWidth) {
					// Add extra left padding to shift right
					leftPadding += textWidth - totalWidth;
				}

				return new Padding(" ".repeat(leftPadding), //
						"0".repeat(rightPadding));
			}

			/**
			 * @param cell
			 * @return
			 * @since 0.1.0
			 */
			private Padding getRightAllignedPadding(String cell) {
				return new Padding(" ".repeat(textWidth - cell.length()), "");
			}

			/**
			 * @param cell
			 * @return
			 * @since 0.1.0
			 */
			private Padding getTextPadding(String cell) {
				return new Padding(//
						"", //
						" ".repeat(textWidth - getLength(cell)));
			}
		}

		/**
		 * @since 0.1.0
		 */
		private ColumnContext[] columns;

		/**
		 * DOCME add JavaDoc for method getColumnContext
		 * 
		 * @param body
		 * @param headline
		 * @param iColumn
		 * @return
		 * @since 0.1.0
		 */
		private Context.ColumnContext createColumnContext(String[][] body, String[] headline, int iColumn) {
			List<String> column = getColumn(body, headline, iColumn);

			return new ColumnContext(column);
		}

		/**
		 * @param body
		 * @param headline
		 * @param iColumn
		 * @return
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
	private record Padding(String before, String after) {
	}

	/**
	 * @since 0.1.0
	 */
	public static final TableStringGenerator DEFAULT = builder().build();

	/**
	 * @since 0.1.0
	 */
	private static final String DEFAULT_COLUMN_SEPARATOR = " │ ";

	/**
	 * @since 0.1.0
	 */
	private static final String DEFAULT_NULL_VALUE = "";

	/**
	 * DOCME add JavaDoc for method builder
	 * 
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
	 * DOCME add JavaDoc for constructor TableStringGenerator
	 * 
	 * @param nullValue
	 * @param columnSeparator
	 * @since 0.1.0
	 */
	private TableStringGenerator(String nullValue, String columnSeparator) {
		this.nullValue = nullValue;
		this.columnSeparator = columnSeparator;
	}

	/**
	 * DOCME add JavaDoc for method toString
	 * 
	 * @param body
	 * @return {@link String}
	 * @since 0.1.0
	 */
	public String toString(String[][] body) {
		return toString(body, null);
	}

	/**
	 * DOCME add JavaDoc for method toString
	 * 
	 * @param body
	 * @param headline
	 * @return
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
	 * @param sb
	 * @param iColumn
	 * @param context
	 * @since 0.1.0
	 */
	private void appendCell(String[] row, StringBuilder sb, int iColumn, Context context) {
		Padding padding = getPadding(row, iColumn, context);

		sb.append(padding.before + format(row[iColumn]) + padding.after);
	}

	/**
	 * @param headline
	 * @param sb
	 * @param context
	 * @since 0.1.0
	 */
	private void appendHeadlineSeparator(String[] headline, StringBuilder sb, Context context) {
		for (int iColumn = 0; iColumn < headline.length; iColumn++) {
			sb.append("─".repeat(context.columns[iColumn].textWidth));

			if (iColumn < headline.length - 1) {
				sb.append("─".repeat(StringUtil.countLeadingSpaces(columnSeparator)));

				if (columnSeparator.equals(DEFAULT_COLUMN_SEPARATOR)) {
					sb.append("┼");
				} else {
					sb.append(columnSeparator);
				}

				sb.append("─".repeat(StringUtil.countTrailingSpaces(columnSeparator)));
			}
		}

		sb.append("\n");
	}

	/**
	 * @param row
	 * @param sb
	 * @param context
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
	 * DOCME add JavaDoc for method getContext
	 * 
	 * @param body
	 * @param headline
	 * @return
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
	 * DOCME add JavaDoc for method format
	 * 
	 * @param string
	 * @return
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
	 * @param headline
	 * @return
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
	 * DOCME add JavaDoc for method getLength
	 * 
	 * @param string
	 * @return
	 * @since 0.1.0
	 */
	private int getLength(String string) {
		return string == null ? nullValue.length() : string.length();
	}

	/**
	 * DOCME add JavaDoc for method getPadding
	 * 
	 * @param row
	 * @param iColumn
	 * @param context
	 * @return
	 * @since 0.1.0
	 */
	private Padding getPadding(String[] row, int iColumn, Context context) {
		return context.columns[iColumn].getPadding(row[iColumn]);
	}
}
