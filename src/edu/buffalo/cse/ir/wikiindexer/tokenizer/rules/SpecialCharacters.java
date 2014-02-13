package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RuleClass(className = RULENAMES.SPECIALCHARS)
public class SpecialCharacters implements TokenizerRule {
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			stream.reset();
			String token;
			List<String> tokenArray = new ArrayList<String>();
			String firstToken;
			
			Pattern pattern = Pattern
					.compile("(.*?)(\\b)(@|\\$|%|\\^|&|\\*|\\<|\\>|,|:|;|'|\\+|_|\"|/|=|\\|)(\\b)");
			Pattern pattern2 = Pattern
					.compile("(~|`|@|#|\\$|%|\\^|&|\\*|\\(|\\)|\\{|\\}|\\<|\\>|,|:|;|'|\\+|_|\"|\\\\|/|=|\\||\\[|\\])");

			while (stream.hasNext()) {
				token = stream.next();
				
				if (token != null) {
					Matcher matcher = pattern.matcher(token);
					if (matcher.find()) {
						matcher = pattern.matcher(token);
						do {
							matcher = pattern.matcher(token);
							if (matcher.find()) {
								Matcher matcher2 = pattern2.matcher(matcher
										.group(1));
								if (matcher2.find()) {
									firstToken = matcher2.replaceAll("");
									tokenArray.add(firstToken);

								} else {
									tokenArray.add(matcher.group(1));

								}

								token = matcher.replaceFirst("");
								matcher = pattern.matcher(token);
								if (!matcher.find()) {
									if (token.matches("") || token.matches(" ")) {
										stream.previous();
										stream.remove();
									}
								}
							}
							matcher = pattern.matcher(token);
							
						} while (matcher.find());
						Matcher matcher2 = pattern2.matcher(token);
						if (matcher2.find()) {
							token=matcher2.replaceAll("");
						}
						tokenArray.add(token);
						stream.previous();
						stream.set(tokenArray.toArray(new String[tokenArray
								.size()]));
						tokenArray.clear();
						stream.next();
					}

					Matcher matcher2 = pattern2.matcher(token);
					if (matcher2.find()) {
						token = matcher2.replaceAll("");
						if (token.matches("") || token.matches(" ")) {
							stream.previous();
							stream.remove();
						} else {

							
							stream.replacePrevious(token);
							
						}
					}

				}
			}
			stream.reset();
		}
	}
}
