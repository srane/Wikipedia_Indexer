package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;

@RuleClass(className = RULENAMES.WHITESPACE)
public class WhiteSpace implements TokenizerRule {
	public void apply(TokenStream stream) throws TokenizerException {

		if (stream != null) {
			stream.reset();
			String token;
			String newToken;
			List<String> splitTokensList = new ArrayList<String>();
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {
					Pattern pattern = Pattern
							.compile("\\s*((?:(?!\\s).)+)\\s*");

					while (true && token != null) {
						Matcher matcher = pattern.matcher(token);
						if (matcher.find()) {
							newToken = matcher.group(1);
							splitTokensList.add(newToken);
							token = matcher.replaceFirst("");
						} else
							break;

					}
					stream.previous();
					stream.set(splitTokensList
							.toArray(new String[splitTokensList.size()]));
					splitTokensList.clear();
					stream.next();
				}

			}
			stream.reset();
		}
	}
}