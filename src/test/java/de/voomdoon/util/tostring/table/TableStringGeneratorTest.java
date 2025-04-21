package de.voomdoon.util.tostring.table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.voomdoon.testing.tests.TestBase;
import de.voomdoon.util.tostring.table.TableStringGenerator.Builder;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
class TableStringGeneratorTest extends TestBase {

	/**
	 * DOCME add JavaDoc for TableStringGeneratorTest
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class BuilderTest extends TestBase {

		/**
		 * DOCME add JavaDoc for TableStringGeneratorTest.BuilderTest
		 *
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		class SetColumnSeparatorTest extends TestBase {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test() throws Exception {
				logTestStart();

				TableStringGenerator generator = TableStringGenerator.builder().setColumnSeparator(":").build();

				String actual = generator.toString(new String[][] { { "a", "b" } });

				assertThat(actual).isEqualTo("a:b");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_withHeadline() throws Exception {
				logTestStart();

				TableStringGenerator generator = TableStringGenerator.builder().setColumnSeparator(":").build();

				String actual = generator.toString(new String[][] { { "a", "b" } }, new String[] { "A", "B" });

				assertThat(actual).isEqualTo("A:B\n─:─\na:b");
			}

			/**
			 * @since 0.1.0
			 */
			@Test
			void testSet_withHeadline_withMoreThanOneCharacter() throws Exception {
				logTestStart();

				TableStringGenerator generator = TableStringGenerator.builder().setColumnSeparator("::").build();

				String actual = generator.toString(new String[][] { { "a", "b" } }, new String[] { "A", "B" });

				assertThat(actual).isEqualTo("A::B\n─::─\na::b");
			}
		}

		/**
		 * DOCME add JavaDoc for TableStringGeneratorTest
		 *
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		class SetNullValueTest extends TestBase {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_toString() throws Exception {
				logTestStart();

				TableStringGenerator generator = TableStringGenerator.builder().setNullValue("NULL").build();

				String actual = generator.toString(new String[][] { { "test", null } });

				assertThat(actual).isEqualTo("test │ NULL");
			}
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_builder() throws Exception {
			logTestStart();

			Builder actual = TableStringGenerator.builder();

			assertThat(actual).isNotNull();
		}
	}

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
		void test_error_IllegalArgumentException_columnCountInconsistent() throws Exception {
			logTestStart();

			String[][] body = { { "a" } };
			String[] headline = new String[] { "A", "B" };

			assertThatThrownBy(() -> toString(body, headline)).isInstanceOf(IllegalArgumentException.class);
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
		void test_null() throws Exception {
			logTestStart();

			String[][] body = { { "1", null, "2" } };

			String actual = toString(body);

			assertThat(actual).isEqualTo("1 │  │ 2");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding_date() throws Exception {
			logTestStart();

			TableStringGenerator generator = TableStringGenerator.builder().setNullValue("NULL").build();

			String[][] body = { { "1", "22222222222" }, { "1", "2001-02-03" } };

			String actual = toString(body, generator);

			assertThat(actual).isEqualTo("1 │ 22222222222\n1 │  2001-02-03");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding_dateTime() throws Exception {
			logTestStart();

			TableStringGenerator generator = TableStringGenerator.builder().setNullValue("NULL").build();

			String[][] body = { { "1", "22222222222222222222" }, { "1", "2001-02-03 12:34:56" } };

			String actual = toString(body, generator);

			assertThat(actual).isEqualTo("1 │ 22222222222222222222\n1 │  2001-02-03 12:34:56");
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
		void test_padding_nullValue() throws Exception {
			logTestStart();

			TableStringGenerator generator = TableStringGenerator.builder().setNullValue("NULL").build();

			String[][] body = { { "1", "2", "3" }, { "1", null, "3" }, { "1", "2", "3" } };

			String actual = toString(body, generator);

			assertThat(actual).isEqualTo("1 │    2 │ 3\n1 │ NULL │ 3\n1 │    2 │ 3");
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
		void test_padding_number_real_rightAlingned() throws Exception {
			logTestStart();

			String[][] body = { { "xxxxx", "2" }, { "12.3", "2" } };

			String actual = toString(body);

			assertThat(actual).isEqualTo("xxxxx │ 2\n 12.3 │ 2");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding_number_real_trailingZero() throws Exception {
			logTestStart();

			String[][] body = { { "1.2", "2" }, { "1.23", "2" } };

			String actual = toString(body);

			assertThat(actual).isEqualTo("1.2  │ 2\n1.23 │ 2");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding_time_hms() throws Exception {
			logTestStart();

			TableStringGenerator generator = TableStringGenerator.builder().setNullValue("NULL").build();

			String[][] body = { { "1", "222222222" }, { "1", "12:34" }, { "1", "12:34:56" } };

			String actual = toString(body, generator);

			assertThat(actual).isEqualTo("1 │ 222222222\n1 │     12:34\n1 │  12:34:56");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_padding_time_ms() throws Exception {
			logTestStart();

			TableStringGenerator generator = TableStringGenerator.builder().setNullValue("NULL").build();

			String[][] body = { { "1", "xxxxxxxxxxxxx" }, { "1", "12:34:56.78" }, { "1", "12:34:56.789" } };

			String actual = toString(body, generator);

			assertThat(actual).isEqualTo("1 │ xxxxxxxxxxxxx\n1 │  12:34:56.78 \n1 │  12:34:56.789");
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
			return toString(table, TableStringGenerator.DEFAULT);
		}

		/**
		 * @param table
		 * @param generator
		 * @return
		 * @since 0.1.0
		 */
		private String toString(String[][] table, TableStringGenerator generator) {
			String actual = generator.toString(table);

			logger.debug("actual:\n" + actual);

			return actual;
		}
	}
}
