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
import java.util.HashMap;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;

/**
 * @author nikhillo This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable {

	private static final String TEMP_PATH = "tmp.dir";
	private HashMap<Integer, HashMap<Integer, Integer>> index;
	boolean isForward;
	private String tempPath;
	private INDEXFIELD keyField;
	private SharedDictionary sharedDictKey;
	private SharedDictionary sharedDictValue;

	/**
	 * Constructor that assumes the underlying index is inverted Every index
	 * (inverted or forward), has a key field and the value field The key field
	 * is the field on which the postings are aggregated The value field is the
	 * field whose postings we are accumulating For term index for example: Key:
	 * Term (or term id) - referenced by TERM INDEXFIELD Value: Document (or
	 * document id) - referenced by LINK INDEXFIELD
	 * 
	 * @param props
	 *            : The Properties file
	 * @param keyField
	 *            : The index field that is the key for this index
	 * @param valueField
	 *            : The index field that is the value for this index
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField,
			INDEXFIELD valueField) {
		this(props, keyField, valueField, false);
	}

	/**
	 * Overloaded constructor that allows specifying the index type as inverted
	 * or forward Every index (inverted or forward), has a key field and the
	 * value field The key field is the field on which the postings are
	 * aggregated
	 * 
	 * The value field is the field whose postings we are accumulating For term
	 * index for example: Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * 
	 * @param props
	 *            : The Properties file
	 * @param keyField
	 *            : The index field that is the key for this index
	 * @param valueField
	 *            : The index field that is the value for this index
	 * @param isForward
	 *            : true if the index is a forward index, false if inverted
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField,
			INDEXFIELD valueField, boolean isForward) {
		// TODO: Implement this method
		this.isForward = isForward;
		this.keyField = keyField;
		tempPath = props.getProperty(IndexerConstants.ROOT_DIR) + "files"
				+ File.separator + props.getProperty(TEMP_PATH);
		tempPath += "indexes" + File.separator;
		if (keyField != INDEXFIELD.TERM) {
			tempPath += keyField + ".ser";
			loadFile(tempPath);
		}

		sharedDictKey = new SharedDictionary(props, keyField);
		sharedDictValue = new SharedDictionary(props, valueField);

	}

	@SuppressWarnings("unchecked")
	private void loadFile(String tempPath) {

		FileInputStream inStream = null;

		File file = new File(tempPath);
		if (!file.exists()) {
			index = new HashMap<Integer, HashMap<Integer, Integer>>();
			return;
		}

		try {
			inStream = new FileInputStream(file);
			ObjectInputStream objectStream = new ObjectInputStream(inStream);
			index = (HashMap<Integer, HashMap<Integer, Integer>>) objectStream
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

	}

	/**
	 * Method to make the writer self aware of the current partition it is
	 * handling Applicable only for distributed indexes.
	 * 
	 * @param pnum
	 *            : The partition number
	 */
	public void setPartitionNumber(int pnum) {
		tempPath += keyField + String.valueOf(pnum) + ".ser";
		loadFile(tempPath);
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param keyId
	 *            : The id for the key field, pre-converted
	 * @param valueId
	 *            : The id for the value field, pre-converted
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, int valueId, int numOccurances)
			throws IndexerException {
		// TODO: Implement this method
		if (index.containsKey(keyId)) {
			HashMap<Integer, Integer> hashMap = index.get(keyId);
			hashMap.put(valueId, numOccurances);

		} else {
			HashMap<Integer, Integer> hashMap = new HashMap<>();
			hashMap.put(valueId, numOccurances);
			index.put(keyId, hashMap);
		}

	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param keyId
	 *            : The id for the key field, pre-converted
	 * @param value
	 *            : The value for the value field
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, String value, int numOccurances)
			throws IndexerException {
		if (value == null) {
			return;
		}
		int valueId = sharedDictValue.lookup(value);
		addToIndex(keyId, valueId, numOccurances);

	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param key
	 *            : The key for the key field
	 * @param valueId
	 *            : The id for the value field, pre-converted
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(String key, int valueId, int numOccurances)
			throws IndexerException {
		// TODO: Done Implement this method
		if (key == null) {
			return;
		}
		int keyId = sharedDictKey.lookup(key);
		addToIndex(keyId, valueId, numOccurances);
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * 
	 * @param key
	 *            : The key for the key field
	 * @param value
	 *            : The value for the value field
	 * @param numOccurances
	 *            : Number of times the value field is referenced by the key
	 *            field. Ignore if a forward index
	 * @throws IndexerException
	 *             : If any exception occurs while indexing
	 */
	public void addToIndex(String key, String value, int numOccurances)
			throws IndexerException {
		// TODO: Done Implement this method
		if (key == null || value == null) {
			return;
		}
		int keyId = sharedDictKey.lookup(key);
		int valueId = sharedDictValue.lookup(value);
		addToIndex(keyId, valueId, numOccurances);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {

		try {

			File file = new File(tempPath);
			if (!file.exists()) {

				file.getParentFile().mkdirs();
			}
			FileOutputStream outputStream = new FileOutputStream(tempPath);
			// FileChannel channel = outputStream.getChannel();
			// FileLock lock = channel.lock();
			// try {
			// channel.tryLock();
			// } catch (OverlappingFileLockException e) {
			// throw new IndexerException(
			// "Not ablt to aquire lock on the Index file");
			// } finally {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					new BufferedOutputStream(outputStream));
			System.out.println(tempPath);
			objectOutputStream.writeObject(index);

			objectOutputStream.flush();
			objectOutputStream.close();
			// lock.release();
			// channel.close();
			//
			// }

		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException(
					"Not able to get the Indexer Output Stream");
		} catch (Exception e) {
			e.printStackTrace();
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
		try {
			sharedDictKey.writeToDisk();
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sharedDictKey.cleanUp();
		sharedDictKey.cleanUp();
		// } catch (IndexerException e) {
		// e.printStackTrace();
		// }

	}

}
