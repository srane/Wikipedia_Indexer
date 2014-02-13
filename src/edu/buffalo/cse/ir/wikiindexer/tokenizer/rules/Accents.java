package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



@RuleClass(className = RULENAMES.ACCENTS)
public class Accents implements TokenizerRule {
	public Accents() {
		// TODO Auto-generated constructor stub
	}

	public void apply(TokenStream stream) throws TokenizerException {

		if (stream != null) {
			stream.reset();
			String token;
			// ArrayList<String> list = new ArrayList<String>();
			while (stream.hasNext()) {
				token = stream.next();
				if (token != null) {

					token = Normalizer.normalize(token, Normalizer.Form.NFD);

					Pattern pattern = Pattern
							.compile("[\\p{InCombiningDiacriticalMarks}+]");
					Matcher matcher = pattern.matcher(token);
					while (matcher.find()) {
						token = matcher.replaceAll("");
						stream.replacePrevious(token);
					}
				}
				// list.add(token);

			}

			// stream.set(list.toArray(new String[list.size()]));
			stream.reset();
		}
	}
}