package de.voomdoon.util.tostring.table;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

//FEATURE date and time right alignment 

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class TableStringGenerator {

	/**
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private static class Context {

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		private static class ColumnContext {

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
					return column.stream().mapToInt(TableStringGenerator::getLength).max().orElse(0);
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
					} else if (REAL_PATTERN.matcher(cell).matches()) {
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
			private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");

			/**
			 * @since 0.1.0
			 */
			private static final Pattern REAL_PATTERN = Pattern.compile("(\\d?\\.\\d+|\\d+\\.\\d?)");

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
					return getIntegerPadding(cell);
				} else if (REAL_PATTERN.matcher(cell).matches()) {
					return getRealPadding(cell);
				}

				return getTextPadding(cell);
			}

			/**
			 * @param cell
			 * @return
			 * @since 0.1.0
			 */
			private Padding getIntegerPadding(String cell) {
				return new Padding(" ".repeat(textWidth - cell.length()), "");
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

				return new Padding(//
						" ".repeat(numberWidthLeft - left), //
						"0".repeat(numberWidthRight - right));
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
	public static final TableStringGenerator DEFAULT = new TableStringGenerator();

	/**
	 * DOCME add JavaDoc for method getLength
	 * 
	 * @param string
	 * @return
	 * @since 0.1.0
	 */
	private static int getLength(String string) {
		return string == null ? 0 : string.length();
	}

	/**
	 * @since 0.1.0
	 */
	private String columnSeparator = " │ ";

	/**
	 * @since 0.1.0
	 */
	private String nullValue = "null";

	/**
	 * DOCME add JavaDoc for method setColumnSeparator
	 * 
	 * @param columnSeparator
	 * @since 0.1.0
	 */
	public void setColumnSeparator(String columnSeparator) {
		this.columnSeparator = columnSeparator;
	}

	/**
	 * DOCME add JavaDoc for method setNullValue
	 * 
	 * @param nullValue
	 * @since 0.1.0
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
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

		Context context = getContext(body, headline);

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
				sb.append("─┼─");
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

	/**
	 * DOCME add JavaDoc for method getColumnContext
	 * 
	 * @param body
	 * @param headline
	 * @param iColumn
	 * @return
	 * @since 0.1.0
	 */
	private Context.ColumnContext getColumnContext(String[][] body, String[] headline, int iColumn) {
		List<String> column = getColumn(body, headline, iColumn);

		return new Context.ColumnContext(column);
	}

	/**
	 * @param body
	 * @param headline
	 * @return
	 * @since 0.1.0
	 */
	private int getColumnCount(String[][] body, String[] headline) {
		if (headline != null) {
			return headline.length;
		}

		return body[0].length;
	}

	/**
	 * DOCME add JavaDoc for method getContext
	 * 
	 * @param body
	 * @param headline
	 * @return
	 * @since 0.1.0
	 */
	private Context getContext(String[][] body, String[] headline) {
		Context context = new Context();
		context.columns = new Context.ColumnContext[getColumnCount(body, headline)];

		for (int iColumn = 0; iColumn < context.columns.length; iColumn++) {
			context.columns[iColumn] = getColumnContext(body, headline, iColumn);
		}

		return context;
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
