package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.Serializable;
import java.util.HashMap;

public class DictionaryMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4379347449749629305L;
	private int lastIndex;

	public synchronized int getLastIndex() {
		return lastIndex;
	}

	private HashMap<String, Integer> lookup;
	private HashMap<Integer, String> reverseLookup;

	public DictionaryMap() {
		this.lookup = new HashMap<String, Integer>();
		this.reverseLookup = new HashMap<Integer, String>();
	}

	public synchronized int put(String key) {
		if (lookup.containsKey(key)) {
			return lookup.get(key);
		} else {
			lastIndex++;
			lookup.put(key, getLastIndex());
			reverseLookup.put(getLastIndex(), key);
			return getLastIndex();
		}
	}

	public synchronized boolean containsKey(String key) {
		return lookup.containsKey(key);
	}

	public synchronized boolean containsValue(int valueId) {
		return reverseLookup.containsKey(valueId);
	}

	public synchronized String getKey(int valueId) {
		return reverseLookup.get(valueId);
	}

	public synchronized int getValueId(String key) {
		return lookup.get(key);
	}

	public synchronized int size() {
		return lookup.size();
	}

	// public HashMap<String, Integer> getlookUP() {
	// return lookup;
	// }

}
