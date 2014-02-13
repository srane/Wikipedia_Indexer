package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RuleClass(className = RULENAMES.DATES)
public class Dates implements TokenizerRule {

	public Dates() {
		// TODO Auto-generated constructor stub
	}

	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			stream.reset();
			String token;
			while (stream.hasNext()) {
				token = stream.next();

				// token = "on 16 November 2000.GRO";
				if (token != null) {

					String monthRegex = "((?i)january|febraury|march|april|may|june|july|august|september|october|november|december)";

					// 00:58:53 UTC Sunday on, 26 December 2004 => 20041226
					// 00:58:53
					Pattern patt = Pattern
							.compile("(\\d\\d\\:\\d\\d:\\d\\d\\sUTC)\\s\\w+\\s\\w+,\\s(\\d?\\d\\s"
									+ monthRegex + "\\s(19|20)\\d\\d)");

					while (true) {
						Matcher matcher = patt.matcher(token);
						if (!matcher.find()) {
							break;
						}
						Date date = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"HH:mm:ss zdd MMMMM yyyy", Locale.US);
						try {
							simpleDateFormat.setLenient(false);
							String temp = matcher.group(1) + matcher.group(2);

							date = simpleDateFormat.parse(temp);
						} catch (ParseException e) {

							e.printStackTrace();
							token = matcher.replaceFirst(simpleDateFormat
									.format(""));
						}

						if (date != null) {
							simpleDateFormat.applyPattern("yyyyMMdd HH:mm:ss");
							token = matcher.replaceFirst(simpleDateFormat
									.format(date));
							stream.replacePrevious(token);
						}
					}

					patt = Pattern.compile("\\s(\\d\\d?\\s" + monthRegex
							+ "\\s(19|20)\\d\\d)");

					while (true) {
						Matcher matcher = patt.matcher(token);
						if (!matcher.find()) {
							break;
						}
						Date date = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"dd MMMMM yyyy", Locale.ENGLISH);
						try {

							date = simpleDateFormat.parse(matcher.group(1));
						} catch (ParseException e) {

							e.printStackTrace();
							token = matcher.replaceFirst(simpleDateFormat
									.format(""));
						}

						if (date != null) {
							simpleDateFormat.applyPattern(" yyyyMMdd");
							token = matcher.replaceFirst(simpleDateFormat
									.format(date));
							stream.replacePrevious(token);
						}
					}

					patt = Pattern.compile("(" + monthRegex
							+ "\\s\\d?\\d,?\\s(19|20)\\d\\d),?");

					while (true) {
						Matcher matcher = patt.matcher(token);
						if (!matcher.find()) {
							break;
						}
						Date date = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"MMMMM d yyyy", Locale.US);
						try {
							simpleDateFormat.setLenient(false);
							String temp = matcher.group(1);
							temp = temp.replaceAll(",", "");
							date = simpleDateFormat.parse(temp);
						} catch (ParseException e) {

							e.printStackTrace();
							token = matcher.replaceFirst(simpleDateFormat
									.format(""));
						}

						if (date != null) {
							simpleDateFormat.applyPattern("yyyyMMdd");
							token = matcher.replaceFirst(simpleDateFormat
									.format(date));
							stream.replacePrevious(token);
						}
					}

					patt = Pattern.compile("\\s(\\d{1,4}\\s?((?i)ad|bc)\\b)");
					while (true) {
						Matcher matcher = patt.matcher(token);
						if (!matcher.find()) {
							break;
						}
						Date date = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"yyyyG", Locale.US);

						simpleDateFormat.setLenient(false);
						String temp = matcher.group(1);
						temp = temp.replaceAll(" ", "");
						try {
							date = simpleDateFormat.parse(temp);
						} catch (ParseException e) {

							e.printStackTrace();
							token = matcher.replaceFirst(simpleDateFormat
									.format(""));
						}

						if (date != null) {
							simpleDateFormat.applyPattern("yyyyMMdd");
							String temp1 = simpleDateFormat.format(date);
							Calendar instance = GregorianCalendar.getInstance();
							instance.setTime(date);
							if (instance.get(Calendar.ERA) == GregorianCalendar.BC) {
								temp1 = "-" + temp1;
							}

							token = matcher.replaceFirst(" " + temp1);
							stream.replacePrevious(token);
						}
					}

					// patt = Pattern
					// .compile("\\s(\\d?\\d\\:\\d\\d\\s?((?i)am|pm))");

					patt = Pattern
							.compile("\\s([012345]?[0-9]\\:[012345][0-9]\\s?((?i)am|pm)\\b)");

					while (true) {
						Matcher matcher = patt.matcher(token);
						if (!matcher.find()) {
							break;
						}
						Date date = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"hh:mma", Locale.US);

						simpleDateFormat.setLenient(false);
						String temp = matcher.group(1);
						temp = temp.replaceAll(" ", "");
						try {
							date = simpleDateFormat.parse(temp);
						} catch (ParseException e) {

							e.printStackTrace();
							token = matcher.replaceFirst(simpleDateFormat
									.format(""));
						}

						if (date != null) {
							simpleDateFormat.applyPattern(" HH:mm:ss");
							token = matcher.replaceFirst(simpleDateFormat
									.format(date));
							stream.replacePrevious(token);
						}
					}

					// April 11 => 19000411
					patt = Pattern
							.compile("(((?i)january|febraury|march|april|may|june|july|august|september|october|november|december)\\s(?:1[012]|0?[1-9])\\b)");
					while (true) {
						Matcher matcher = patt.matcher(token);
						if (!matcher.find()) {
							break;
						}
						Date date = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"MMMMM dd yyyy", Locale.US);
						// Calendar cal = Calendar.getInstance();
						// cal.set(Calendar.YEAR, 1900);
						// simpleDateFormat.setCalendar(cal);
						try {
							simpleDateFormat.setLenient(false);

							date = simpleDateFormat.parse(matcher.group(1)
									+ " 1900");
							// date.setYear(1900);

						} catch (ParseException e) {

							e.printStackTrace();
							token = matcher.replaceFirst(simpleDateFormat
									.format(""));
						}

						if (date != null) {
							simpleDateFormat.applyPattern("yyyyMMdd");
							token = matcher.replaceFirst(simpleDateFormat
									.format(date));
							stream.replacePrevious(token);
						}
					}

					// 2011�12 => 20110101�20120101

					patt = Pattern.compile("(19|20)(\\d\\d)�(\\d\\d)");

					while (true) {
						Matcher matcher = patt.matcher(token);
						if (!matcher.find()) {
							break;
						}
						Date date1 = null;
						Date date2 = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"yyyy", Locale.US);
						// Calendar cal = Calendar.getInstance();
						// cal.set(Calendar.YEAR, 1900);
						// simpleDateFormat.setCalendar(cal);

						try {
							simpleDateFormat.setLenient(false);
							String strDate1 = matcher.group(1)
									+ matcher.group(2);
							String strDate2 = matcher.group(1)
									+ matcher.group(3);
							date1 = simpleDateFormat.parse(strDate1);
							date2 = simpleDateFormat.parse(strDate2);
							// date.setYear(1900);

						} catch (ParseException e) {

							e.printStackTrace();
							token = matcher.replaceFirst(simpleDateFormat
									.format(""));
						}

						if (date1 != null) {
							simpleDateFormat.applyPattern("yyyyMMdd");
							String temp = simpleDateFormat.format(date1) + "�"
									+ simpleDateFormat.format(date2);
							token = matcher.replaceFirst(temp);
							stream.replacePrevious(token);
						}
					}

					patt = Pattern.compile("\\s(\\d\\d\\d\\d)\\s");

					while (true) {
						Matcher matcher = patt.matcher(token);
						if (!matcher.find()) {
							break;
						}
						Date date = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"yyyy", Locale.US);
						try {
							simpleDateFormat.setLenient(false);
							date = simpleDateFormat.parse(matcher.group(1));
						} catch (ParseException e) {

							e.printStackTrace();
							token = matcher.replaceFirst(simpleDateFormat
									.format(""));
						}

						if (date != null) {
							simpleDateFormat.applyPattern(" yyyyMMdd ");
							token = matcher.replaceFirst(simpleDateFormat
									.format(date));
							stream.replacePrevious(token);
						}
					}

					// String month = "January";
					// String year = "1990";
					// String date = "1";
					// String time = "00:00:00";
					//
					// String datePattern = "[1-31]";
					// String monthPattern =
					// "(?i)january|febraury|march|april|may|june|july|august|september|october|november|december";
					// String yearPattern = "19\\d\\d|20\\d\\d";
					// String yearCPattern = "(\\d+)\\s?[ad|bc]";
					//
					// Pattern patt = Pattern.compile("(" + datePattern +
					// ")\\s*("
					// + monthPattern + ")\\s*(" + yearPattern + ")");
					//
					// Matcher matcher = patt.matcher(token);
					// if (matcher.find()) {
					// month = matcher.group(2);
					// date = matcher.group(1);
					// year = matcher.group(3);
					//
					// SimpleDateFormat date = new SimpleDateFormat();
					//
					//
					//
					// }

					// Date date = null;
					// try {
					// date = new SimpleDateFormat("YYYYMMDD", Locale.ENGLISH)
					// .parse(token);
					// } catch (ParseException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// if (date == null) {
					// continue;
					// }
					// stream.replacePrevious(date.toString());
					// boolean flag = false;
					//
					// String month = "January";
					//
					// String time = "00:00:00";
					// Pattern patt = Pattern
					// .compile("((?i)january|febraury|march|april|may|june|july|august|september|october|november|december)");//
					// |(19\\d\\d|20\\d\\d)||(\\d\\d:\\d\\d\\s?[am|pm]))");
					//
					// Matcher matcher = patt.matcher(token);
					// if (matcher.find()) {
					// flag = true;
					// month = matcher.group(1);
					// }
					// String year = "1990";
					// patt = Pattern.compile("(19\\d\\d|20\\d\\d)");
					// matcher = patt.matcher(token);
					// if (matcher.find()) {
					// flag = true;
					// year = matcher.group(1);
					// }
					//
					// String date = "1";
					// patt = Pattern.compile("([1-31])");
					// matcher = patt.matcher(token);
					// if (matcher.find() && flag) {
					// flag = true;
					// year = matcher.group(1);
					// }
				}
			}
			stream.reset();
		}

	}
}
