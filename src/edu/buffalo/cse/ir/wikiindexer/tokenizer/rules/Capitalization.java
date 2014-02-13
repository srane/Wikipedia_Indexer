package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RuleClass(className = RULENAMES.CAPITALIZATION)
public class Capitalization implements TokenizerRule {
	public Capitalization() {

	}


	Pattern pattern3 = Pattern
			.compile("((?:\\.\\s|^)[A-Z]((?![A-Z_0-9]).)*?\\s)");

	public void apply(TokenStream stream) throws TokenizerException {

		if (stream != null) {
			stream.reset();
			String token;
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					while (true) {
						Matcher matcher = pattern3.matcher(token);
						if (!matcher.find()) {
							break;
						}
						token = matcher.replaceFirst(matcher.group(1)
								.toLowerCase());

					}
				}

				stream.replacePrevious(token);
			}
			stream.reset();
		}

	}

}