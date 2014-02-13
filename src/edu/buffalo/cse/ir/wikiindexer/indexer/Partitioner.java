/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

/**
 * @author nikhillo THis class is responsible for assigning a partition to a
 *         given term. The static methods imply that all instances of this class
 *         should behave exactly the same. Given a term, irrespective of what
 *         instance is called, the same partition number should be assigned to
 *         it.
 */
public class Partitioner {
	/**
	 * Method to get the total number of partitions THis is a pure design choice
	 * on how many partitions you need and also how they are assigned.
	 * 
	 * @return: Total number of partitions
	 */
	public static int getNumPartitions() {
		// TODO: Implement this method
		return 6;
	}

	/**
	 * Method to fetch the partition number for the given term. The partition
	 * numbers should be assigned from 0 to N-1 where N is the total number of
	 * partitions.
	 * 
	 * @param term
	 *            : The term to be looked up
	 * @return The assigned partition number for the given term
	 */
	public static int getPartitionNumber(String term) {
		// TDOD: Implement this method

		if (term == null || term.trim().length() == 0) {
			return 0;
		}

		char ch = term.toUpperCase().charAt(0);
		if (ch >= 'A' && ch <= 'E') {
			return 1;
		} else if (ch >= 'F' && ch <= 'J') {
			return 2;
		} else if (ch >= 'K' && ch <= 'O') {
			return 3;
		} else if (ch >= 'P' && ch <= 'T') {
			return 4;
		} else if (ch >= 'U' && ch <= 'Z') {
			return 5;
		} else {
			return 6;
		}

	}
}
