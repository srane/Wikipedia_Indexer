/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

/**
 * This class represents a stream of tokens as the name suggests. It wraps the
 * token stream and provides utility methods to manipulate it
 * 
 * @author nikhillo
 * 
 */
public class TokenStream implements Iterator<String> {

	private ArrayList<String> mainStream;
	private int index;

	public TokenStream() {
		this.mainStream = new ArrayList<>();
	}

	/**
	 * Default constructor
	 * 
	 * @param bldr
	 *            : THe stringbuilder to seed the stream
	 */
	public TokenStream(StringBuilder bldr) {
		// TODO: DONE Implement this methods
		String input = null;
		if (bldr != null) {
			input = bldr.toString();
		}

		checkInputString(input);

	}

	/**
	 * Overloaded constructor
	 * 
	 * @param bldr
	 *            : THe stringbuilder to seed the stream
	 */
	public TokenStream(String string) {
		checkInputString(string);

	}

	/**
	 * This function will check the input to the constructors. Will be called by
	 * the constructors.
	 * 
	 * @param input
	 * @author Niraj
	 */
	private void checkInputString(String input) {
		this.mainStream = new ArrayList<>();
		if (input == null || input.trim().length() == 0) {
			return;
		}
		this.mainStream.add(input.toString());
	}

	/**
	 * Method to append tokens to the stream
	 * 
	 * @param tokens
	 *            : The tokens to be appended
	 */
	public void append(String... tokens) {
		// TODO: DONE Implement this method
		if (tokens == null || tokens.length == 0) {
			return;
		}

		for (String token : tokens) {
			if (token != null && token.trim().length() > 0) {
				this.mainStream.add(token.trim());
			}
		}

	}

	/**
	 * Method to retrieve a map of token to count mapping This map should
	 * contain the unique set of tokens as keys The values should be the number
	 * of occurrences of the token in the given stream
	 * 
	 * @return The map as described above, no restrictions on ordering
	 *         applicable
	 */
	public Map<String, Integer> getTokenMap() {
		// TODO: DONEImplement this method
		if (this.mainStream.size() == 0) {
			return null;
		}

		Map<String, Integer> result = new HashMap<>();
		ListIterator<String> listIterator = this.mainStream.listIterator();
		while (listIterator.hasNext()) {
			String token = listIterator.next().trim();
			if (result.containsKey(token)) {
				continue;
			}
			int frequency = query(token);
			result.put(token, frequency);
		}
		return result;
	}

	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * 
	 * @return A collection containing the ordered tokens as wrapped by this
	 *         stream Each token must be a separate element within the
	 *         collection. Operations on the returned collection should NOT
	 *         affect the token stream
	 */
	public Collection<String> getAllTokens() {
		// TODO: DONE Implement this method

		// java.util.Collections.sort(mainStream);
		if (mainStream.size() == 0) {
			return null;
		}

		return mainStream;
	}

	/**
	 * Method to query for the given token within the stream
	 * 
	 * @param token
	 *            : The token to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {
		// TODO: DONE Implement this method
		if (token == null || token.trim().length() == 0) {
			return 0;
		}

		// token = token.trim();
		// Iterator<String> iterator = this.mainStream.iterator();
		// int count = 0;
		// while (iterator.hasNext()) {
		// String string = (String) iterator.next();
		// if (string.trim().equalsIgnoreCase(token)) {
		// count++;
		// }
		// }

		int count = java.util.Collections.frequency(this.mainStream, token);
		return count;
	}

	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * 
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasNext() {
		// TODO: DONE Implement this method
		int size = this.mainStream.size();
		if (index < size) {
			return true;
		} else {
			return false;
		}

		// try {
		// String string = this.mainStream.get(index);
		// return true;
		// } catch (IndexOutOfBoundsException e) {
		// return false;
		// }

	}

	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * 
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasPrevious() {
		// TODO: DONE Implement this method

		int size = this.mainStream.size();
		if (index > 0 && index - 1 < size) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Iterator method: Method to get the next token from the stream Callers
	 * must call the set method to modify the token, changing the value of the
	 * token returned by this method must not alter the stream
	 * 
	 * @return The next token from the stream, null if at the end
	 */
	public String next() {
		// TODO: DONE Implement this method
		int size = this.mainStream.size();
		if (index < size) {
			String string = this.mainStream.get(index);
			index++;
			return string;
		}
		return null;

	}

	/**
	 * Iterator method: Method to get the previous token from the stream Callers
	 * must call the set method to modify the token, changing the value of the
	 * token returned by this method must not alter the stream
	 * 
	 * @return The next token from the stream, null if at the end
	 */
	public String previous() {
		// TODO: DONE Implement this method

		try {
			String string = this.mainStream.get(index - 1);
			index--;
			return string;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

	}

	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
		// TODO: DONE Implement this method
		try {
			this.mainStream.remove(index);
		} catch (Exception e) {

		}

	}

	/**
	 * Method to merge the current token with the previous token, assumes
	 * whitespace separator between tokens when merged. The token iterator
	 * should now point to the newly merged token (i.e. the previous one)
	 * 
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithPrevious() {
		// TODO: DONE Implement this method
		String previousToken;
		String currentToken;
		try {
			previousToken = this.mainStream.remove(index - 1);
			currentToken = this.mainStream.remove(index - 1);
		} catch (Exception e) {
			return false;
		}

		previousToken = previousToken + " " + currentToken;
		this.mainStream.add(index - 1, previousToken);
		index--;

		return true;
	}

	/**
	 * Method to merge the current token with the next token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the current one)
	 * 
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithNext() {
		// TODO: DONE Implement this method
		String currentToken;
		String nextToken;
		try {
			currentToken = this.mainStream.remove(index);
			nextToken = this.mainStream.remove(index);
		} catch (Exception e) {
			return false;
		}

		currentToken = currentToken + " " + nextToken;
		this.mainStream.add(index, currentToken);

		return true;
	}

	/**
	 * Method to replace the current token with the given tokens The stream
	 * should be manipulated accordingly based upon the number of tokens set It
	 * is expected that remove will be called to delete a token instead of
	 * passing null or an empty string here. The iterator should point to the
	 * last set token, i.e, last token in the passed array.
	 * 
	 * @param newValue
	 *            : The array of new values with every new token as a separate
	 *            element within the array
	 */
	public void set(String... newValue) {
		// TODO: DONE Implement this method
		if (newValue == null || newValue.length == 0) {
			return;
		}

		boolean flag = true;
		for (String string : newValue) {

			if (string != null && string.trim().length() > 0) {
				if (flag) {
					try {
						this.mainStream.remove(index);
						index--;
					} catch (Exception e) {
						return;
					}

					flag = false;

				}
				index++;
				this.mainStream.add(index, string);

			}
		}

	}

	/**
	 * Iterator method: Method to reset the iterator to the start of the stream
	 * next must be called to get a token
	 */
	public void reset() {
		// TODO: DONE Implement this method
		index = 0;
	}

	/**
	 * Iterator method: Method to set the iterator to beyond the last token in
	 * the stream previous must be called to get a token
	 */
	public void seekEnd() {

		index = this.mainStream.size();

	}

	/**
	 * Method to merge this stream with another stream
	 * 
	 * @param other
	 *            : The stream to be merged
	 */
	public void merge(TokenStream other) {
		// TODO: DONE Implement this method
		if (other == null || other.getAllTokens() == null) {
			return;
		}

		this.mainStream.addAll(other.getAllTokens());

	}

	public void replacePrevious(String string) {
		if (string == null || string.trim().length() == 0) {
			return;
		}

		try {
			this.mainStream.remove(index - 1);
			this.mainStream.add(index - 1, string);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
