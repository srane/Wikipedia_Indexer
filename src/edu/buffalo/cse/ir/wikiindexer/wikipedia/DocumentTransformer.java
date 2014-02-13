/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section;

/**
 * A Callable document transformer that converts the given WikipediaDocument
 * object into an IndexableDocument object using the given Tokenizer
 * 
 * @author nikhillo
 * 
 */
public class DocumentTransformer implements Callable<IndexableDocument> {
	private Map<INDEXFIELD, Tokenizer> tokenizerMap;
	private WikipediaDocument doc;

	/**
	 * Default constructor, DO NOT change
	 * 
	 * @param tknizerMap
	 *            : A map mapping a fully initialized tokenizer to a given field
	 *            type
	 * @param doc
	 *            : The WikipediaDocument to be processed
	 */
	public DocumentTransformer(Map<INDEXFIELD, Tokenizer> tknizerMap,
			WikipediaDocument doc) {
		// TODO: Implement this method

		this.tokenizerMap = tknizerMap;
		this.doc = doc;

	}

	/**
	 * Method to trigger the transformation
	 * 
	 * @throws TokenizerException
	 *             Inc ase any tokenization error occurs
	 */
	public IndexableDocument call() throws TokenizerException {
		// TODO Implement this method
		if (doc.getTitle() == null) {
			return null;
		}

		IndexableDocument indexDoc = new IndexableDocument(doc.getTitle());
		if (tokenizerMap.containsKey(INDEXFIELD.AUTHOR)) {
			Tokenizer tokenizer = tokenizerMap.get(INDEXFIELD.AUTHOR);
			TokenStream authorStream = new TokenStream(doc.getAuthor());
			authorStream.reset();
			tokenizer.tokenize(authorStream);
			indexDoc.addField(INDEXFIELD.AUTHOR, authorStream);

		}

		if (tokenizerMap.containsKey(INDEXFIELD.CATEGORY)) {
			Tokenizer tokenizer = tokenizerMap.get(INDEXFIELD.CATEGORY);

			TokenStream tokenStream = new TokenStream();
			tokenStream.append(doc.getCategories().toArray(
					new String[doc.getCategories().size()]));
			tokenStream.reset();
			tokenizer.tokenize(tokenStream);
			indexDoc.addField(INDEXFIELD.CATEGORY, tokenStream);
		}

		if (tokenizerMap.containsKey(INDEXFIELD.LINK)) {
			Tokenizer tokenizer = tokenizerMap.get(INDEXFIELD.LINK);
			TokenStream stream = new TokenStream();
			stream.append(doc.getLinks().toArray(
					new String[doc.getLinks().size()]));
			stream.reset();
			tokenizer.tokenize(stream);
			indexDoc.addField(INDEXFIELD.LINK, stream);
		}

		if (tokenizerMap.containsKey(INDEXFIELD.TERM)) {
			Tokenizer tokenizer = tokenizerMap.get(INDEXFIELD.TERM);
			TokenStream stream = new TokenStream();
			List<Section> sections = doc.getSections();
			for (Section section : sections) {
				stream.append(section.getTitle());
				stream.append(section.getText());
			}
			stream.reset();
			tokenizer.tokenize(stream);
			indexDoc.addField(INDEXFIELD.TERM, stream);
		}

		return indexDoc;
	}
}
