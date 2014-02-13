/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo This class implements Wikipedia markup processing. Wikipedia
 *         markup details are presented here:
 *         http://en.wikipedia.org/wiki/Help:Wiki_markup It is expected that all
 *         methods marked "todo" will be implemented by students. All methods
 *         are static as the class is not expected to maintain any state.
 */
public class WikipediaParser {

	/**
	 * A function which will parse the whole document remove all the unnecessary
	 * markup and gives the {@link WikipediaDocument} object in return
	 * 
	 * @param idFromXml
	 * @param timestampFromXml
	 * @param authorFromXml
	 * @param ttl
	 * @param textFromXml
	 * @return {@link WikipediaDocument} object contain all the details of the
	 *         Document
	 * @throws ParseException
	 */
	public WikipediaDocument parse(int idFromXml, String timestampFromXml,
			String authorFromXml, String ttl, String textFromXml)
			throws ParseException {

		// Creating Wikidocument object to fill and return
		WikipediaDocument wikiDoc = new WikipediaDocument(idFromXml,
				timestampFromXml, authorFromXml, ttl);

		// Parse text formating
		textFromXml = parseTextFormatting(textFromXml);

		// Parse and remove all templates from the page
		textFromXml = parseTemplates(textFromXml);

		// Parse and remove all the listItem markup
		textFromXml = parseListItem(textFromXml);

		// Parse and remove external links
		textFromXml = removeExternalLinks(textFromXml);

		// Parse, remove and add all the categories of the page to the
		textFromXml = parseCategory(textFromXml, wikiDoc);

		// Parse all the links and put to the document
		textFromXml = parseLinksFromWholeText(textFromXml, wikiDoc);

		// Parse and remove all the Tags from the document
		textFromXml = parseTagFormatting(textFromXml);

		parseSection(textFromXml, wikiDoc);

		return wikiDoc;

	}

	/* TODO DONE */
	/**
	 * Method to parse section titles or headings. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * 
	 * @param titleStr
	 *            : The string to be parsed
	 * @return The parsed string with the markup removed
	 */
	public static String parseSectionTitle(String titleStr) {

		if (titleStr == null) {
			return null;
		}
		String sectionParsedText = new String();
		Pattern pattern = Pattern.compile("(^=+\\s?)(.*?)(\\s?=+)",
				Pattern.MULTILINE);
		sectionParsedText = titleStr;

		while (true) {
			Matcher matcher = pattern.matcher(sectionParsedText);
			if (!matcher.find()) {
				break;
			}
			sectionParsedText = matcher.replaceFirst(matcher.group(2));

		}
		return sectionParsedText;
	}

	// public String getSection(String text) {
	// String sectionTitle = new String();
	// Pattern pattern = Pattern
	// .compile("(\\n=+)(.*?)(=+)([\\s\\S]*?)(\\n=+)(.*?)(=+)");
	// String sectionText = new String();
	// String textp = new String();
	// textp = text;
	// while (true) {
	// Matcher matcher = pattern.matcher(textp);
	// if (!matcher.find()) {
	// break;
	// }
	//
	// sectionTitle = matcher.group(2);
	// sectionText = matcher.group(4);
	// textp = matcher.replaceFirst(matcher.group(2));
	// sectionTitleArray.add(sectionTitle);
	// sectionTextArray.add(sectionText);
	// }
	//
	// return sectionTitle;
	// }

	public String parseSection(String text, WikipediaDocument wikiDoc) {
		String sectionTitle = new String();
		Pattern pattern = Pattern.compile(
				"(?:^=+\\s?(.*?)\\s?=+)?((?:(?!^=+\\s?.*?\\s?=+)[\\s\\S])*)",
				Pattern.MULTILINE);
		String sectionText = new String();
		String parsedText = new String();
		parsedText = text;
		while (true) {
			Matcher matcher = pattern.matcher(parsedText);
			if (!matcher.find() || parsedText.trim().length() == 0) {
				break;
			}

			sectionTitle = matcher.group(1);
			if (sectionTitle == null || sectionTitle.trim().length() == 0) {
				sectionTitle = "Default";
			}
			sectionText = matcher.group(2);
			// parsedText = matcher.replaceFirst(matcher.group(5)
			// + matcher.group(6) + matcher.group(7));
			parsedText = matcher.replaceFirst("");
			wikiDoc.addSection(sectionTitle, sectionText);
		}

		return sectionTitle;
	}

	/* TODO DONE */
	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
	 * 
	 * @param itemText
	 *            : The string to be parsed
	 * @return The parsed string with markup removed
	 */
	public static String parseListItem(String itemText) {
		if (itemText == null) {
			return null;
		}

		Pattern patt = Pattern.compile("^(?:\\*|\\#|\\:)+\\s+",
				Pattern.MULTILINE);
		Matcher matcher = patt.matcher(itemText);

		itemText = matcher.replaceAll("");

		return itemText;
	}

	/* TODO DONE */
	/**
	 * Method to parse text formatting: bold and italics. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting(String text) {

		if (text == null) {
			return null;
		}

		Pattern patt = Pattern.compile("(\\'\\'+)(.*?)(\\1)");

		while (true) {
			Matcher matcher = patt.matcher(text);

			if (!matcher.find()) {

				break;
			}
			text = matcher.replaceFirst(matcher.group(2));

		}
		return text;
	}

	/* TODO DONE */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz> For most
	 * cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {

		if (text == null) {
			return null;
		}

		// // Do not delete can be used for heavy tagging
		// Pattern patt = Pattern
		// .compile("(?:<|&lt;)([a-zA-Z0-9]+)\\s?(?:(?!/).)*?(?:>|&gt;)([\\s\\S]*?)(?:<|&lt;)/(\\1)(?:>|&gt;)");
		// while (true) {
		// Matcher matcher = patt.matcher(text);
		//
		// if (!matcher.find()) {
		// break;
		// }
		// try {
		// text = matcher.replaceFirst(Matcher.quoteReplacement(matcher
		// .group(2).trim()));
		// } catch (Exception e) {
		// // TODO: handle exception
		// e.printStackTrace();
		// }
		//
		// }

		Pattern patt = Pattern.compile("\\s(?:<|&lt;).*?(?:>|&gt;)\\s");

		Matcher matcher = patt.matcher(text);

		if (matcher.find()) {
			text = matcher.replaceAll(" ");
		}

		patt = Pattern.compile("\\s?(?:<|&lt;).*?(?:>|&gt;)\\s?");
		matcher = patt.matcher(text);
		if (matcher.find()) {
			text = matcher.replaceAll("");
		}

		return text;

	}

	/* TODO DONE */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags For
	 * most cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTemplates(String text) {

		Pattern patt = Pattern.compile("\\{\\{(?:(?!\\{\\{)[\\s\\S])*?\\}\\}");
		while (true) {
			Matcher matcher = patt.matcher(text);

			if (!matcher.find()) {
				break;
			}
			text = matcher.replaceFirst("");
		}
		return text;
	}

	public String removeExternalLinks(String text) {
		Pattern patt = Pattern.compile("\\*\\[.*?\\s?(.+?)\\]");

		while (true) {
			Matcher matcher = patt.matcher(text);
			if (!matcher.find()) {
				break;

			}
			String match = matcher.group(1);

			text = matcher.replaceFirst(matcher.group(1));
		}
		return text;

	}

	/* TODO DONE */
	/**
	 * Method to parse links and URLs. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return An array containing two elements as follows - The 0th element is
	 *         the parsed text as visible to the user on the page The 1st
	 *         element is the link url
	 */
	public static String[] parseLinks(String text) {
		String[] result = new String[] { "", "" };
		if (text == null || text.trim().length() == 0) {
			return result;
		}
		String replacingText = "";
		String link = "";
		// text = "London has [[public transport]]";

		// Pattern patt = Pattern.compile("\\*\\[(.+?)\\]");
		// Matcher matcher = patt.matcher(text);
		// String link = null;
		// if (matcher.find()) {
		// String match = matcher.group(1);
		// String[] subStrings = match.split("\\s", 2);
		//
		// text = matcher.replaceFirst(subStrings[1]);
		// link = subStrings[0];
		//
		// }

		// Pattern patt = Pattern
		// .compile("\\[\\[((?:(?!\\]\\]).)*?)\\|?((?:(?!\\|).)*?)\\]\\](?:\\<nowiki\\s/\\>)?");
		Pattern patt = Pattern
				.compile("\\[\\[(.*?)\\]\\](?:\\<nowiki\\s/\\>)?");
		Matcher matcher1 = patt.matcher(text);

		if (matcher1.find()) {

			String group1;
			String group2;
			String temp = matcher1.group(1);
			temp = new StringBuilder(temp).reverse().toString();

			// patt = Pattern.compile("((?:(?!\\|).)*)\\|?(.*?)");
			// matcher = patt.matcher(temp);
			// matcher.find();
			String[] temp1 = temp.split("\\|", 2);

			group2 = new StringBuilder(temp1[0]).reverse().toString();
			if (temp1.length < 2) {
				group1 = group2;
				group2 = "";
			} else
				group1 = new StringBuilder(temp1[1]).reverse().toString();

			if (group1.trim().length() > 0) {
				patt = Pattern
						.compile("^(?:Wikipedia|Category)\\:(.*?)\\s\\(.*?\\)");
				Matcher matcher = patt.matcher(group1);
				if (matcher.find()) {

					replacingText = matcher.group(1);
					link = "";
				} else {
					patt = Pattern.compile("(.*?)\\s(\\(.*?\\))");
					matcher = patt.matcher(group1);
					if (matcher.find()) {
						replacingText = matcher.group(1);
						link = group1.replace(' ', '_');
						link = Character.toUpperCase(link.charAt(0))
								+ link.substring(1);
					} else if (group1.contains(",")) {
						replacingText = group1.split(",")[0];
						link = group1.replace(' ', '_');
					} else {
						patt = Pattern
								.compile("^(?:Wikipedia|Category)\\:(.*)");
						matcher = patt.matcher(group1);
						if (matcher.find()) {
							replacingText = matcher.group(1);
							if (group1.contains("#")) {
								replacingText = group1;
							}
							link = "";
						} else {
							patt = Pattern.compile("^\\:(Category\\:.*)");
							matcher = patt.matcher(group1);
							if (matcher.find()) {
								replacingText = matcher.group(1);
								link = "";
							} else {
								patt = Pattern
										.compile("^Wiktionary\\:(..\\:.*)");
								matcher = patt.matcher(group1);
								if (matcher.find()) {
									replacingText = matcher.group(1);
									link = "";
								} else {
									patt = Pattern
											.compile("^(?:media|File)\\:.*");
									matcher = patt.matcher(group1);
									if (matcher.find()) {
										replacingText = "";
										link = "";
									} else if (group1.contains(":")
											|| group1.contains("#")) {
										link = "";
										replacingText = group1;
									} else {

										link = group1.replace(' ', '_');
										link = Character.toUpperCase(link
												.charAt(0)) + link.substring(1);
										replacingText = group1;
									}
								}
							}
						}
					}
				}

			}

			if (group2.trim().length() > 0) {

				replacingText = group2;
			}

		} else {
			patt = Pattern
					.compile("\\[http\\://www\\.wikipedia\\.org\\s?(.*)\\]");
			matcher1 = patt.matcher(text);
			if (matcher1.find()) {
				replacingText = matcher1.group(1);
				link = "";
			} else {
				patt = Pattern.compile("\\[http\\:.*?]");
				matcher1 = patt.matcher(text);
				if (matcher1.find()) {
					replacingText = "";
					link = "";
				} else {
					result[0] = text;
					result[1] = null;
					return result;
				}

			}

		}

		result[0] = matcher1.replaceFirst(Matcher
				.quoteReplacement(replacingText));
		result[1] = link;

		return result;
	}

	/**
	 * This function will parse and remove all the links from a given text. This
	 * function calls {@link WikipediaParser#parseLinks(String)}
	 * 
	 * @param text
	 * @return Array of Link and Text Strings
	 * @author Niraj
	 */
	public String parseLinksFromWholeText(String text, WikipediaDocument wikiDoc) {
		String[] result;
		while (true) {
			result = parseLinks(text);
			if (result[1] == null) {
				return result[0];
			}

			wikiDoc.addLink(result[1]);
			text = result[0];
		}

	}

	/**
	 * This function will remove the category of the article and will put the
	 * categories into the {@link WikipediaDocument} object.
	 * 
	 * @param text
	 *            The text from which the categories need to parsed
	 * @param wikiDoc
	 *            The object of the {@link WikipediaDocument} in which all the
	 *            categories will be put
	 * @return Parse String with the categories removed
	 * @author Niraj
	 */
	public String parseCategory(String text, WikipediaDocument wikiDoc) {
		Pattern patt = Pattern.compile("\\[\\[Category\\:(.*)\\]\\]");

		while (true) {
			Matcher matcher = patt.matcher(text);
			if (!matcher.find()) {
				break;
			}

			String category = matcher.group(1);
			text = matcher.replaceFirst(matcher.group(1));
			wikiDoc.addCategory(category);

		}
		return text;
	}

}
