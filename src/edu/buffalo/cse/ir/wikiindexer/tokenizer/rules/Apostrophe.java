package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;

@RuleClass(className = RULENAMES.APOSTROPHE)
public class Apostrophe implements TokenizerRule {
	public Apostrophe() {

	}

	public void apply(TokenStream stream) throws TokenizerException {

		if (stream != null) {
			stream.reset();
			String token;
			List<String> tokenArray = new ArrayList<String>();
			Pattern pattern = Pattern.compile("'");
			Pattern pattern1 = Pattern.compile("(.*?)(n't|N'T)");
			Pattern pattern2 = Pattern.compile("(.*?)('re|'RE)");
			Pattern pattern3 = Pattern.compile("(.*?)('ve|'VE)");
			Pattern pattern4 = Pattern.compile("(s'|S')");
			Pattern pattern5 = Pattern.compile("('s|'S)");
			Pattern pattern6 = Pattern.compile("(.*?)('d|'D)");
			Pattern pattern7 = Pattern.compile("(.*?)('ll|'LL)");
			Pattern pattern9 = Pattern.compile("(.*?)'m|'M");
			Pattern pattern10 = Pattern.compile("(.*?)'em|'EM");
			Pattern pattern11 = Pattern.compile("won't|WON'T");
			Pattern pattern12 = Pattern.compile("can't|CAN'T");
			Pattern pattern13 = Pattern.compile("shan't|SHAN'T");
			Pattern pattern14 = Pattern.compile("let's|LET'S");
			
			while (stream.hasNext()) {
				token = stream.next();

				if (token != null) {
					Matcher matcher = pattern.matcher(token);
					if (matcher.find()) {
						Matcher matcher1 = pattern1.matcher(token);
						if (matcher1.find()) {
							tokenArray.clear();
							tokenArray.add(matcher1.group(1));
							tokenArray.add("not");
							Matcher matcher11 = pattern11.matcher(token);
							if (matcher11.find()) {
								tokenArray.clear();
								tokenArray.add("will");
								tokenArray.add("not");

							} else {
								Matcher matcher13 = pattern13.matcher(token);
								if (matcher13.find()) {
									tokenArray.clear();
									tokenArray.add("shall");
									tokenArray.add("not");

								} else {
									Matcher matcher12 = pattern12
											.matcher(token);
									if (matcher12.find()) {
										tokenArray.clear();
										tokenArray.add("can");
										tokenArray.add("not");

									}
								}
							}
						} else {
							Matcher matcher2 = pattern2.matcher(token);
							if (matcher2.find()) {
								tokenArray.clear();
								tokenArray.add(matcher2.group(1));
								tokenArray.add("are");
							}

							else {
								Matcher matcher3 = pattern3.matcher(token);
								if (matcher3.find()) {
									tokenArray.clear();
									tokenArray.add(matcher3.group(1));
									tokenArray.add("have");

								}

								else {
									Matcher matcher4 = pattern4.matcher(token);
									if (matcher4.find()) {
										tokenArray.clear();
										tokenArray.add(matcher4
												.replaceFirst("s"));
									}

									else {
										Matcher matcher5 = pattern5
												.matcher(token);
										if (matcher5.find()) {
											tokenArray.clear();
											tokenArray.add(matcher5
													.replaceFirst(""));
											Matcher matcher14 = pattern14
													.matcher(token);
											if (matcher14.find()) {
												tokenArray.clear();
												tokenArray.add("let");
												tokenArray.add("us");

											}

										} else {
											Matcher matcher6 = pattern6
													.matcher(token);
											if (matcher6.find()) {
												tokenArray.clear();
												tokenArray.add(matcher6
														.group(1));
												tokenArray.add("would");

											} else {
												Matcher matcher7 = pattern7
														.matcher(token);
												if (matcher7.find()) {
													tokenArray.clear();
													tokenArray.add(matcher7
															.group(1));
													tokenArray.add("will");

												} else {
													Matcher matcher9 = pattern9
															.matcher(token);
													if (matcher9.find()) {
														tokenArray.clear();
														tokenArray.add(matcher9
																.group(1));
														tokenArray.add("am");

													} else {
														Matcher matcher10 = pattern10
																.matcher(token);
														if (matcher10.find()) {
															tokenArray.clear();
															tokenArray
																	.add(matcher10
																			.group(1));
															tokenArray
																	.add("them");

														} else {
															Matcher matcher8 = pattern
																	.matcher(token);
															if (matcher8.find()) {
																tokenArray
																		.clear();
																token = matcher8.replaceAll("");
																if(!token.matches("")&&!token.matches(" "))
																{tokenArray
																		.add(matcher8
																				.replaceAll(""));
																}else {
																	stream.previous();
																	stream.remove();
																}
																}
														}

													}

												}
											}

										}
									}
								}
							}
						}

						stream.previous();
						
						stream.set(tokenArray.toArray(new String[tokenArray
								.size()]));
						tokenArray.clear();
						stream.next();
					}

				}

			}
			stream.reset();
		}
	}
}
