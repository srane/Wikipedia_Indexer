package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class Punctuation implements TokenizerRule {
	public Punctuation() {

	}

	public void apply(TokenStream stream) throws TokenizerException {

		if (stream != null) {
			stream.reset();
			String token;
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					Pattern pattern = Pattern
							.compile("(\\.\\B)|(!\\B)|(\\?\\B)");
					Matcher matcher = pattern.matcher(token);
					if (matcher.find()) {
						token = matcher.replaceAll("");
					}
				}
				
				stream.replacePrevious(token);
				
			}
			stream.reset();
		}
	}
}