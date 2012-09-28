package ch.cern.atlas.apvs.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
	
	/*
	 * Partial copy of commons-lang3 from apache, reworked to use in GWT
	 */
	
	public static final byte CHAR_UNASSIGNED = 0;
	public static final byte CHAR_UPPERCASE_LETTER = 1;
	public static final byte CHAR_LOWERCASE_LETTER = 2;

	/**
	 * <p>
	 * Splits a String by Character type as returned by
	 * {@code java.lang.Character.getType(char)}. Groups of contiguous
	 * characters of the same type are returned as complete tokens, with the
	 * following exception: the character of type
	 * {@code Character.UPPERCASE_LETTER}, if any, immediately preceding a token
	 * of type {@code Character.LOWERCASE_LETTER} will belong to the following
	 * token rather than to the preceding, if any,
	 * {@code Character.UPPERCASE_LETTER} token.
	 * 
	 * <pre>
	 * StringUtils.splitByCharacterTypeCamelCase(null)         = null
	 * StringUtils.splitByCharacterTypeCamelCase("")           = []
	 * StringUtils.splitByCharacterTypeCamelCase("ab de fg")   = ["ab", " ", "de", " ", "fg"]
	 * StringUtils.splitByCharacterTypeCamelCase("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
	 * StringUtils.splitByCharacterTypeCamelCase("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
	 * StringUtils.splitByCharacterTypeCamelCase("number5")    = ["number", "5"]
	 * StringUtils.splitByCharacterTypeCamelCase("fooBar")     = ["foo", "Bar"]
	 * StringUtils.splitByCharacterTypeCamelCase("foo200Bar")  = ["foo", "200", "Bar"]
	 * StringUtils.splitByCharacterTypeCamelCase("ASFRules")   = ["ASF", "Rules"]
	 * </pre>
	 * 
	 * @param str
	 *            the String to split, may be {@code null}
	 * @return an array of parsed Strings, {@code null} if null String input
	 * @since 2.4
	 */
	public static String[] splitByCharacterTypeCamelCase(String str) {
		return splitByCharacterType(str, true);
	}

	/**
	 * <p>
	 * Splits a String by Character type as returned by
	 * {@code java.lang.Character.getType(char)}. Groups of contiguous
	 * characters of the same type are returned as complete tokens, with the
	 * following exception: if {@code camelCase} is {@code true}, the character
	 * of type {@code Character.UPPERCASE_LETTER}, if any, immediately preceding
	 * a token of type {@code Character.LOWERCASE_LETTER} will belong to the
	 * following token rather than to the preceding, if any,
	 * {@code Character.UPPERCASE_LETTER} token.
	 * 
	 * @param str
	 *            the String to split, may be {@code null}
	 * @param camelCase
	 *            whether to use so-called "camel-case" for letter types
	 * @return an array of parsed Strings, {@code null} if null String input
	 * @since 2.4
	 */
	public static String[] splitByCharacterType(String str, boolean camelCase) {
		if (str == null) {
			return null;
		}
		if (str.length() == 0) {
			return new String[0];
		}
		char[] c = str.toCharArray();
		List<String> list = new ArrayList<String>();
		int tokenStart = 0;
		int currentType = getCharacterType(c[tokenStart]);
		for (int pos = tokenStart + 1; pos < c.length; pos++) {
			int type = getCharacterType(c[pos]);
			if (type == currentType) {
				continue;
			}
			if (camelCase && type == CHAR_LOWERCASE_LETTER
					&& currentType == CHAR_UPPERCASE_LETTER) {
				int newTokenStart = pos - 1;
				if (newTokenStart != tokenStart) {
					list.add(new String(c, tokenStart, newTokenStart
							- tokenStart));
					tokenStart = newTokenStart;
				}
			} else {
				list.add(new String(c, tokenStart, pos - tokenStart));
				tokenStart = pos;
			}
			currentType = type;
		}
		list.add(new String(c, tokenStart, c.length - tokenStart));
		return list.toArray(new String[list.size()]);
	}

	public static int getCharacterType(char c) {
		return Character.isLowerCase(c) ? CHAR_LOWERCASE_LETTER : Character.isUpperCase(c) ? CHAR_UPPERCASE_LETTER : CHAR_UNASSIGNED;
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing
	 * the provided list of elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty
	 * strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *)               = null
	 * StringUtils.join([], *)                 = ""
	 * StringUtils.join([null], *)             = ""
	 * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
	 * StringUtils.join(["a", "b", "c"], null) = "abc"
	 * StringUtils.join([null, "", "a"], ';')  = ";;a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 2.0
	 */
	public static String join(Object[] array, char separator) {
		if (array == null) {
			return null;
		}

		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing
	 * the provided list of elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty
	 * strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *)               = null
	 * StringUtils.join([], *)                 = ""
	 * StringUtils.join([null], *)             = ""
	 * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
	 * StringUtils.join(["a", "b", "c"], null) = "abc"
	 * StringUtils.join([null, "", "a"], ';')  = ";;a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass
	 *            in an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to
	 *            pass in an end index past the end of the array
	 * @return the joined String, {@code null} if null array input
	 * @since 2.0
	 */
	public static String join(Object[] array, char separator, int startIndex,
			int endIndex) {
		if (array == null) {
			return null;
		}
		int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return "";
		}

		StringBuilder buf = new StringBuilder(noOfItems * 16);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}
}
