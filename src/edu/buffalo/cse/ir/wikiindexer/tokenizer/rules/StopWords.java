package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RuleClass(className = RULENAMES.STOPWORDS)
public class StopWords implements TokenizerRule {
	public StopWords() {

	}

	public void apply(TokenStream stream) throws TokenizerException {

		if (stream != null) {
			String token;
			stream.reset();
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					Pattern pattern = Pattern
							.compile("\\bthis\\b|\\bis\\b|\\ba\\b|\\bdo\\b|\\bnot\\b|\\bof\\b");
					Matcher matcher = pattern.matcher(token);
					if (matcher.find()) {
						stream.previous();
						stream.remove();

					} else {
						
						stream.replacePrevious(token);
						
					}
				}

			}

			stream.reset();
		}
	}
}