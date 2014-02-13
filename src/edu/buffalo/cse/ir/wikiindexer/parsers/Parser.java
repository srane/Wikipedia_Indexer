/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.util.Collection;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;

import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author nikhillo
 * 
 */
public class Parser extends DefaultHandler {
	/* */

	private String tmp;
	private StringBuilder builder = new StringBuilder();
	private String textFromXml;
	private Collection<WikipediaDocument> docs;
	private final Properties props;
	private String sectionTitle;
	private int idFromXml;
	private String timestampFromXml;
	private String authorFromXml;
	private String ttl;
	private int r;

	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public Parser(Properties idxProps) {
		props = idxProps;
	}

	/* TODO: Implement this method */
	/**
	 * 
	 * @param filename
	 * @param docs
	 */

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		builder.append(ch, start, length);
		tmp = new String(ch, start, length);

	}

	public void startElement(String uri, String name, String qName,
			Attributes atts) throws SAXException {
		tmp = " ";

		if (qName.equalsIgnoreCase("revision")) {
			r = 1;
		}

		if (qName.equalsIgnoreCase("id")) {
		}

		if (qName.equalsIgnoreCase("page")) {

		}
		if (qName.equalsIgnoreCase("text")) {
			builder.delete(0, builder.length());
		}

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("page")) {
			WikipediaParser wikiParser = new WikipediaParser();
			try {
				add(wikiParser.parse(idFromXml, timestampFromXml,
						authorFromXml, ttl, textFromXml), docs);
			} catch (ParseException e) {

				e.printStackTrace();
			}
		}

		else if (qName.equalsIgnoreCase("id") && r == 0) {
			idFromXml = Integer.parseInt(tmp);
		}

		else if (qName.equalsIgnoreCase("timestamp")) {
			timestampFromXml = tmp;
		}

		else if (qName.equalsIgnoreCase("username")
				|| qName.equalsIgnoreCase("ip")) {
			authorFromXml = tmp;
		}

		else if (qName.equalsIgnoreCase("title")) {

			ttl = tmp;

		}

		else if (qName.equalsIgnoreCase("text")) {

			textFromXml = builder.toString();
			// WikipediaParser wikiParser = new WikipediaParser();
			// wikiParser.parseSection(idFromXml, timestampFromXml,
			// authorFromXml,
			// ttl, textFromXml);
			// wikiParser.parseSectionTitle(textFromXml);

		}

		else if (qName.equalsIgnoreCase("revision")) {
			r = 0;
		}

	}

	public void parse(String filename, Collection<WikipediaDocument> docs) {
		this.docs = docs;
		if (filename == null || filename.trim().length() == 0) {
			return;
		}

		File file = new File(filename);
		if (!file.exists()) {
			return;
		}

		SAXParserFactory saxparfac = SAXParserFactory.newInstance();
		SAXParser saxpar;
		try {
			saxpar = saxparfac.newSAXParser();

			saxpar.parse(filename, this);
			System.out.println();
		} catch (ParserConfigurationException | SAXException | IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Method to add the given document to the collection. PLEASE USE THIS
	 * METHOD TO POPULATE THE COLLECTION AS YOU PARSE DOCUMENTS For better
	 * performance, add the document to the collection only after you have
	 * completely populated it, i.e., parsing is complete for that document.
	 * 
	 * @param doc
	 *            : The WikipediaDocument to be added
	 * @param documents
	 *            : The collection of WikipediaDocuments to be added to
	 */
	private synchronized void add(WikipediaDocument doc,
			Collection<WikipediaDocument> documents) {
		documents.add(doc);
	}
}
