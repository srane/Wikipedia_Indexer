/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;

/**
 * @author nikhillo This class is used to introspect a given index The
 *         expectation is the class should be able to read the index and all
 *         associated dictionaries.
 */
public class IndexReader {
	private INDEXFIELD field;
	private String tempPath;
	private static final String TEMP_PATH = "tmp.dir";
	private HashMap<Integer, HashMap<Integer, Integer>> index;
	private HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>> termIndex;
	private Properties props;
	private SharedDictionary keyDictionary;
	private SharedDictionary valueDictionary;

	/**
	 * Constructor to create an instance
	 * 
	 * @param props
	 *            : The properties file
	 * @param field
	 *            : The index field whose index is to be read
	 */
	public IndexReader(Properties props, INDEXFIELD field) {
		// TODO: Implement this method
		this.field = field;
		this.props = props;
		tempPath = props.getProperty(IndexerConstants.ROOT_DIR) + "files"
				+ File.separator + props.getProperty(TEMP_PATH);
		tempPath += "indexes" + File.separator;

		loadIndex(field);

	}

	private void loadIndex(INDEXFIELD field) {
		if (field == INDEXFIELD.TERM) {
			termIndex = new HashMap<String, HashMap<Integer, HashMap<Integer, Integer>>>();
		} else {
			String filePath = tempPath + field + ".ser";
			index = loadFile(filePath);
		}

	}

	@SuppressWarnings("unchecked")
	private HashMap<Integer, HashMap<Integer, Integer>> loadFile(String filePath) {
		FileInputStream inStream = null;
		HashMap<Integer, HashMap<Integer, Integer>> result = null;
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		try {
			inStream = new FileInputStream(file);
			ObjectInputStream objectStream = new ObjectInputStream(inStream);
			result = (HashMap<Integer, HashMap<Integer, Integer>>) objectStream
					.readObject();
			objectStream.close();
		} catch (FileNotFoundException e) {
			// index = new HashMap<>(); May have to write this
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Method to get the total number of terms in the key dictionary
	 * 
	 * @return The total number of terms as above
	 */
	public int getTotalKeyTerms() {
		// TODO: Implement this method
		if (keyDictionary == null) {
			keyDictionary = new SharedDictionary(props, field);
		}

		return keyDictionary.size();

	}

	/**
	 * Method to get the total number of terms in the value dictionary
	 * 
	 * @return The total number of terms as above
	 */
	public int getTotalValueTerms() {
		// TODO: Implement this method
		if (valueDictionary == null) {
			valueDictionary = new SharedDictionary(props, INDEXFIELD.LINK);
		}

		return valueDictionary.size();
	}

	/**
	 * Method to retrieve the postings list for a given dictionary term
	 * 
	 * @param key
	 *            : The dictionary term to be queried
	 * @return The postings list with the value term as the key and the number
	 *         of occurrences as value. An ordering is not expected on the map
	 */
	public Map<String, Integer> getPostings(String key) {
		// TODO: Implement this method
		HashMap<Integer, HashMap<Integer, Integer>> hashMap;
		if (field == INDEXFIELD.TERM) {
			int partitionNumber = Partitioner.getPartitionNumber(key);
			String indexName = field.toString() + partitionNumber;
			if (termIndex.containsKey(indexName)) {
				hashMap = termIndex.get(indexName);
			} else {
				String path = tempPath + indexName + ".ser";
				hashMap = loadFile(path);
				termIndex.put(indexName, hashMap);
			}

		} else {
			hashMap = index;
		}

		if (keyDictionary == null) {
			keyDictionary = new SharedDictionary(props, field);
		}

		int keyId = keyDictionary.lookup(key);

		if (hashMap == null || !hashMap.containsKey(keyId)) {
			return null;
		}
		return convertToString(hashMap.get(keyId));
	}

	private HashMap<String, Integer> convertToString(
			HashMap<Integer, Integer> inputMap) {
		if (valueDictionary == null) {
			valueDictionary = new SharedDictionary(props, INDEXFIELD.LINK);
		}
		HashMap<String, Integer> outputMap = new HashMap<String, Integer>();
		Set<Entry<Integer, Integer>> entrySet = inputMap.entrySet();
		for (Entry<Integer, Integer> entry : entrySet) {
			outputMap.put(valueDictionary.getKey(entry.getKey()),
					entry.getValue());
		}
		return outputMap;
	}

	private HashMap<Integer, HashMap<Integer, Integer>> getTermIndex(int pnum) {
		HashMap<Integer, HashMap<Integer, Integer>> hashMap;
		String indexName = field.toString() + pnum;
		if (termIndex.containsKey(indexName)) {
			hashMap = termIndex.get(indexName);
		} else {
			String path = tempPath + indexName + ".ser";
			hashMap = loadFile(path);
			termIndex.put(indexName, hashMap);
		}
		return hashMap;
	}

	/**
	 * Method to get the top k key terms from the given index The top here
	 * refers to the largest size of postings.
	 * 
	 * @param k
	 *            : The number of postings list requested
	 * @return An ordered collection of dictionary terms that satisfy the
	 *         requirement If k is more than the total size of the index, return
	 *         the full index and don't pad the collection. Return null in case
	 *         of an error or invalid inputs
	 */
	public Collection<String> getTopK(int k) {
		// TODO: Implement this method

		try {
			int size = 0;
			if (keyDictionary == null) {
				keyDictionary = new SharedDictionary(props, field);
			}
			Collection<String> kTop = new ArrayList<String>();

			int key;

			int[] kSize = new int[k];
			int[] kTerms = new int[k];
			int count = 1;
			if (field == INDEXFIELD.TERM) {
				count = 5;
			}
			while (count >= 1) {

				HashMap<Integer, HashMap<Integer, Integer>> termIndex2;

				if (field == INDEXFIELD.TERM) {
					termIndex2 = getTermIndex(count);

				} else {
					termIndex2 = index;
				}

				Set<Entry<Integer, HashMap<Integer, Integer>>> entrySet = termIndex2
						.entrySet();

				int point = 0;
				for (Entry<Integer, HashMap<Integer, Integer>> entry : entrySet) {
					key = entry.getKey();
					size = entry.getValue().size();

					if (point == 0) {
						kSize[0] = size;
						kTerms[0] = key;
						point++;

					} else {
						for (int i = 0; i < point; i++) {
							if (size > kSize[i]) {
								for (int j = point; j > i; j--) {
									if (point == k) {
										point--;
										j = point;
									}
									kSize[j] = kSize[j - 1];
									kTerms[j] = kTerms[j - 1];
								}

								kSize[i] = size;
								kTerms[i] = key;
								point++;
								break;
							} else if (point < k && i == point - 1) {
								kSize[point] = size;
								kTerms[point] = key;
								point++;
								break;
							}

						}
					}

				}
				count--;
			}
			for (int i = 0; i < k; i++)
				System.out.println(kSize[i]);

			for (int i = 0; i < k; i++) {
				kTop.add(keyDictionary.getKey(field, kTerms[i]));
			}

			return kTop;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Method to execute a boolean AND query on the index
	 * 
	 * @param terms
	 *            The terms to be queried on
	 * @return An ordered map containing the results of the query The key is the
	 *         value field of the dictionary and the value is the sum of
	 *         occurrences across the different postings. The value with the
	 *         highest cumulative count should be the first entry in the map.
	 */
	@SuppressWarnings({ "rawtypes", "null", "unchecked" })
	public Map<String, Integer> query(String... terms) {
		// TODO: Implement this method (FOR A BONUS)
		Map<String, Integer> Linkresult = new LinkedHashMap<String, Integer>();
		TreeMap<Integer, String> Treeresult = new TreeMap<Integer, String>();
		Map<Integer, String> Treeresult2 = new TreeMap<Integer, String>();
		int min;
		int small;
		Set<Entry<Integer, Integer>> entry[] = new Set[terms.length];
		SharedDictionary sDict = new SharedDictionary(props, field);
		SharedDictionary lDict = new SharedDictionary(props, INDEXFIELD.LINK);
		Entry<Integer, Integer> e = null;
		HashMap<Integer, Integer>[] queryList = new HashMap[terms.length];
		Iterator it[] = new Iterator[terms.length];
		int doc = 0;
		int occ = 0;
		int count = 1;
		int present = 0;
		if (field == INDEXFIELD.TERM) {
			count = 5;
		}
		while (count >= 1) {
			HashMap<Integer, HashMap<Integer, Integer>> queryIndex;

			if (field == INDEXFIELD.TERM) {
				queryIndex = getTermIndex(count);

			} else {
				queryIndex = index;
			}

			for (int i = 0; i < terms.length; i++) {

				if (queryIndex.containsKey(sDict.lookup(terms[i]))) {
					queryList[i] = queryIndex.get(sDict.lookup(terms[i]));
					entry[i] = queryList[i].entrySet();
					it[i] = entry[i].iterator();
				}
			}
			count--;
		}
		min = queryList[0].size();
		small = 0;
		for (int i = 1; i < terms.length; i++) {
			if (queryList[i].size() < min) {
				min = queryList[i].size();
				small = i;
			}
		}
		for (int i = 0; i < queryList[small].size(); i++) {
			present = 0;
			occ = 0;
			if (it[small].hasNext()) {
				e = (Entry<Integer, Integer>) it[small].next();
				doc = e.getKey();

			}
			for (int j = 0; j < terms.length; j++) {
				if (queryList[j].containsKey(doc)) {
					present++;
					occ = occ + queryList[j].get(doc);
				}

			}
			if (present == terms.length) {

				Treeresult.put(occ, lDict.getKey(doc));
			}
		}

		Treeresult2 = Treeresult.descendingMap();
		Set<Entry<Integer, String>> entrySet = Treeresult2.entrySet();

		for (Entry<Integer, String> entry2 : entrySet) {
			Linkresult.put(entry2.getValue(), entry2.getKey());
		}

		return Linkresult;
	}

}
