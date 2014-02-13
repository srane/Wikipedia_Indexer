/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;

/**
 * @author nikhillo An abstract class that represents a dictionary object for a
 *         given index
 */
public abstract class Dictionary implements Writeable {

	private static DictionaryMap termDictionary;
	private static DictionaryMap authorDictionary;
	private static DictionaryMap linkDictionary;
	private static DictionaryMap categoryDictionary;
	INDEXFIELD indexField;
	String fileName;
	private static String folderPath;
	private static final String TEMP_PATH = "tmp.dir";
	private static final String KEY_LAST_INDEX = "lastIndex";

	@SuppressWarnings("unchecked")
	public Dictionary(Properties props, INDEXFIELD field) {
		// TODO Implement this method
		this.indexField = field;
		if (folderPath == null) {
			folderPath = props.getProperty(IndexerConstants.ROOT_DIR) + "files"
					+ File.separator + props.getProperty(TEMP_PATH)
					+ "dictionary";
		}

		loadObject(field);

	}

	private void loadObject(INDEXFIELD field) {
		switch (field) {
		case TERM:
			if (termDictionary == null) {
				termDictionary = loadFile(field);
			}
			break;
		case LINK:
			if (linkDictionary == null) {
				linkDictionary = loadFile(field);
			}
			break;
		case AUTHOR:
			if (authorDictionary == null) {
				authorDictionary = loadFile(field);
			}
			break;
		case CATEGORY:
			if (categoryDictionary == null) {
				categoryDictionary = loadFile(field);
			}
			break;

		default:
			break;
		}

	}

	@SuppressWarnings("unchecked")
	private DictionaryMap loadFile(INDEXFIELD field) {
		DictionaryMap result = null;
		String filePath = getDictionaryPath(field);
		File file = new File(filePath);
		if (!file.exists()) {
			result = new DictionaryMap();
			// result.put(KEY_LAST_INDEX, new Integer(0));
			return result;
		}
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(filePath);
			// InputStream buffer = new BufferedInputStream(inStream);
			ObjectInputStream objectStream = new ObjectInputStream(inStream);
			Object readObject = objectStream.readObject();
			result = (DictionaryMap) readObject;
			objectStream.close();
		} catch (FileNotFoundException e) {

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

	private String getDictionaryPath(INDEXFIELD field) {

		return folderPath + File.separator + field + ".ser";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
		writeFileToDisk(INDEXFIELD.TERM);
		writeFileToDisk(INDEXFIELD.AUTHOR);
		writeFileToDisk(INDEXFIELD.CATEGORY);
		writeFileToDisk(INDEXFIELD.LINK);

	}

	protected void writeFileToDisk(INDEXFIELD field) throws IndexerException {
		try {
			File file = new File(folderPath);
			if (!file.exists()) {
				file.mkdirs();
			}

			Object object = getDictionaryObject(field);
			if (object == null) {
				return;
			}
			String filePath = getDictionaryPath(field);
			FileOutputStream outputStream = new FileOutputStream(filePath);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					new BufferedOutputStream(outputStream));
			System.out.println(filePath);
			// System.out.println(((DictionaryMap) object).getlookUP());
			// System.out
			// .println("---------------------------------------------------------------");
			synchronized (object) {
				objectOutputStream.writeObject(object);
				objectOutputStream.flush();
				objectOutputStream.close();

			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException(
					"Not able to get the Indexer Output Stream");
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private DictionaryMap getDictionaryObject(INDEXFIELD field) {
		switch (field) {
		case TERM:
			return termDictionary;

		case LINK:
			return linkDictionary;
		case AUTHOR:
			return authorDictionary;
		case CATEGORY:
			return categoryDictionary;
		default:
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Done Implement this method
		// try {
		// writeToDisk();
		// } catch (IndexerException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * Method to check if the given value exists in the dictionary or not Unlike
	 * the subclassed lookup methods, it only checks if the value exists and
	 * does not change the underlying data structure
	 * 
	 * @param value
	 *            : The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		// TODO Implement this method
		if (termDictionary.containsKey(value)) {
			return true;
		}
		return false;
	}

	/**
	 * MEthod to lookup a given string from the dictionary. The query string can
	 * be an exact match or have wild cards (* and ?) Must be implemented ONLY
	 * AS A BONUS
	 * 
	 * @param queryStr
	 *            : The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 *         null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		// TODO: Implement this method (FOR A BONUS)

		// Collection<String> found = new ArrayList<String>();
		// Collection<String> keys = new ArrayList<String>();
		// if (!found.isEmpty()) {
		// found.clear();
		// }
		// Pattern pattern = Pattern.compile("\\*");
		// Matcher matcher = pattern.matcher(queryStr);
		// if (matcher.find()) {
		// keys = termDictionary.keySet();
		// String token = keys.iterator().next();
		//
		// } else if (termDictionary.containsKey(queryStr)) {
		// found.add(queryStr);
		// return found;
		// }
		return null;

	}

	/**
	 * Method to get the total number of terms in the dictionary
	 * 
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		// TODO: Implement this method

		return termDictionary.size();

	}

	protected int lookup(String value, INDEXFIELD field) {
		DictionaryMap dictionaryObject = getDictionaryObject(field);

		if (dictionaryObject.containsKey(value)) {
			return dictionaryObject.getValueId(value);
		} else {
			synchronized (dictionaryObject) {
				int valueId = dictionaryObject.put(value);
				return valueId;
			}

		}

	}

	protected int size(INDEXFIELD field) {
		int size = getDictionaryObject(field).size() - 1;
		return size;
	}

	protected String getKey(INDEXFIELD field, int valueId) {
		DictionaryMap dictionaryObject = getDictionaryObject(field);
		return dictionaryObject.getKey(valueId);
	}
}
