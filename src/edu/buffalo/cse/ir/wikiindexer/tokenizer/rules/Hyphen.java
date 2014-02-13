package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RuleClass(className = RULENAMES.HYPHEN)
public class Hyphen implements TokenizerRule {

	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			stream.reset();
			String token;
			Pattern pattern = Pattern.compile("-|�");
			Pattern pattern1 = Pattern
					.compile("(.*?\\d.*?(-|�).*?)|(.*?(-|�).*?\\d.*?)");
			Pattern pattern2 = Pattern.compile("\\b(-|�)\\b");

			while (stream.hasNext()) {
				token = stream.next();

				if (token != null) {
					Matcher matcher = pattern.matcher(token);
					if (matcher.find()) {
						Matcher matcher1 = pattern1.matcher(token);
						if (!matcher1.find()) {
							Matcher matcher2 = pattern2.matcher(token);
							if (matcher2.find()) {
								token = matcher2.replaceAll(" ");
							} else {
								Matcher matcher3 = pattern.matcher(token);
								if (matcher3.find()) {
									token = matcher3.replaceAll("");
								}
							}

						}

						if (!token.matches("") && (!token.matches(" "))) {
							stream.replacePrevious(token);

						} else {
							stream.previous();
							stream.remove();
						}
					}

				}

			}
			stream.reset();
		}
	}
}
