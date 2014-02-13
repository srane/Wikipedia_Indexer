/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nikhillo This class represents a subclass of a Dictionary class that
 *         is local to a single thread. All methods in this class are assumed
 *         thread safe for the same reason.
 */
public class LocalDictionary extends Dictionary {

	private INDEXFIELD field;
	private DictionaryMap dictionary;

	/**
	 * Public default constructor
	 * 
	 * @param props
	 *            : The properties file
	 * @param field
	 *            : The field being indexed by this dictionary
	 */
	public LocalDictionary(Properties props, INDEXFIELD field) {
		super(props, field);
		// TODO Auto-generated constructor stub
		this.field = field;
		dictionary = new DictionaryMap();
	}

	/**
	 * Method to lookup and possibly add a mapping for the given value in the
	 * dictionary. The class should first try and find the given value within
	 * its dictionary. If found, it should return its id (Or hash value). If not
	 * found, it should create an entry and return the newly created id.
	 * 
	 * @param value
	 *            : The value to be looked up
	 * @return The id as explained above.
	 */
	public int lookup(String value) {

		if (dictionary.containsKey(value)) {
			return dictionary.getValueId(value);
		} else {

			int valueId = dictionary.put(value);
			return valueId;

		}

	}

	public int getTotalTerms() {
		// TODO: Implement this method

		return dictionary.size();

	}

	public boolean exists(String value) {
		// TODO Implement this method

		if (dictionary.containsKey(value)) {
			return true;
		}
		return false;
	}

}
