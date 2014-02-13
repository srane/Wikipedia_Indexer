package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RuleClass(className = RULENAMES.NUMBERS)
public class Numbers implements TokenizerRule {

	public Numbers() {
		// TODO Auto-generated constructor stub
	}

	public void apply(TokenStream stream) throws TokenizerException {
		if (stream != null) {
			stream.reset();
			String token;
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {

					Pattern patt = Pattern.compile("\\d+/\\d+");
					Matcher matcher = patt.matcher(token);
					matcher.find();
					token = matcher.replaceAll("/");
					patt = Pattern
							.compile("\\b((?!(?:\\d?\\d?\\d\\d(?:0[1-9]|1[012])(?:[012][0-9]|3[01]))|(?:([01][0-9]|2[0123])\\:([012345][0-9])\\:([012345][0-9])))\\d)+\\.?,?\\s?");
					matcher = patt.matcher(token);
					matcher.find();
					token = matcher.replaceAll("");
					stream.replacePrevious(token);
				}
			}
			stream.reset();
		}

	}
}