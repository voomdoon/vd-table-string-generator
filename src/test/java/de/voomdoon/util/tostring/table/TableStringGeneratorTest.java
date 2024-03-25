package de.voomdoon.util.tostring.table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.voomdoon.testing.tests.TestBase;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
class TableStringGeneratorTest extends TestBase {

	/**
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class String2_String_Test extends TestBase {

		/**
		 * @since 0.1.0
		 */
		@Test
		void test() throws Exception {
			logTestStart();

			String[][] table = { { "a", "b", "c" } };

			String actual = toString(table, new String[] { "A", "B", "C" });

			assertThat(actual).isEqualTo("A │ B │ C\n──┼───┼──\na │ b │ c");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_body_empty() throws Exception {
			logTestStart();

			String actual = toString(new String[0][0], new String[] { "h1", "h2" });

			assertThat(actual).isEqualTo("h1 │ h2\n───┼───");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding() throws Exception {
			logTestStart();

			String[][] table = { { "aa", "b" } };

			String actual = toString(table, new String[] { "A", "B" });

			assertThat(actual).isEqualTo("A  │ B\n───┼──\naa │ b");
		}

		/**
		 * @param body
		 * @param headline
		 * @return
		 * @since 0.1.0
		 */
		private String toString(String[][] body, String[] headline) {
			String actual = TableStringGenerator.DEFAULT.toString(body, headline);

			logger.debug("actual:\n" + actual);

			return actual;
		}
	}

	/**
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class String2_Test extends TestBase {

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_empty() throws Exception {
			logTestStart();

			String actual = toString(new String[0][0]);

			assertThat(actual).isBlank();
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_null_noError() throws Exception {
			logTestStart();

			assertDoesNotThrow(() -> toString(new String[][] { { null } }));
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding_integer_and_word() throws Exception {
			logTestStart();

			String[][] body = { { "1", "2" }, { "aa", "b" }, { "a", "b" } };

			String actual = toString(body);

			assertThat(actual).isEqualTo(" 1 │ 2\naa │ b\na  │ b");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding_number_integer() throws Exception {
			logTestStart();

			String[][] body = { { "1", "2" }, { "11", "2" } };

			String actual = toString(body);

			assertThat(actual).isEqualTo(" 1 │ 2\n11 │ 2");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding_number_real_leadingSpace() throws Exception {
			logTestStart();

			String[][] body = { { "1.2", "2" }, { "12.3", "2" } };

			String actual = toString(body);

			assertThat(actual).isEqualTo(" 1.2 │ 2\n12.3 │ 2");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding_number_real_trailingZero() throws Exception {
			logTestStart();

			String[][] body = { { "1.2", "2" }, { "1.23", "2" } };

			String actual = toString(body);

			assertThat(actual).isEqualTo("1.20 │ 2\n1.23 │ 2");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding_word() throws Exception {
			logTestStart();

			String[][] body = { { "A", "B" }, { "aa", "b" } };

			String actual = toString(body);

			assertThat(actual).isEqualTo("A  │ B\naa │ b");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_row1() throws Exception {
			logTestStart();

			String[][] body = { { "a", "b", "c" } };

			String actual = toString(body);

			assertThat(actual).isEqualTo("a │ b │ c");
		}

		/**
		 * @param table
		 * @return
		 * @since 0.1.0
		 */
		private String toString(String[][] table) {
			String actual = TableStringGenerator.DEFAULT.toString(table);

			logger.debug("actual:\n" + actual);

			return actual;
		}
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void testSetColumnSeparator() throws Exception {
		logTestStart();

		TableStringGenerator generator = new TableStringGenerator();
		generator.setColumnSeparator(":");

		String actual = generator.toString(new String[][] { { "a", "b" } });

		assertThat(actual).isEqualTo("a:b");
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void testSetColumnSeparator_withHeadline() throws Exception {
		logTestStart();

		TableStringGenerator generator = new TableStringGenerator();
		generator.setColumnSeparator(":");

		String actual = generator.toString(new String[][] { { "a", "b" } }, new String[] { "A", "B" });

		assertThat(actual).isEqualTo("A:B\n─┼─\na:b");
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void testSetNullValue() throws Exception {
		logTestStart();

		TableStringGenerator generator = new TableStringGenerator();
		generator.setNullValue("NULL");

		String actual = generator.toString(new String[][] { { "test", null } });

		assertThat(actual).isEqualTo("test │ NULL");
	}
}
