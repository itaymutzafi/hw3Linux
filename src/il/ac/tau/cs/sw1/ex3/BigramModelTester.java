package il.ac.tau.cs.sw1.ex3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class BigramModelTester {
	private static int testsFailed = 0;
	private static int totalTests = 0;

	private final static String ANSI_RED = "\u001B[31m";
	private final static String ANSI_GREEN = "\u001B[32m";

	private static void colorPrint(String message, String color) {
		final String ANSI_RESET = "\u001B[0m";
		System.err.println(color + message + ANSI_RESET);
	}

	private static void test(boolean predicate, String name) {
		totalTests++;
		if (!predicate) {
			colorPrint("!!! FAILED ----- Test: " + name + " ----- FAILED !!!", ANSI_RED);
			testsFailed++;
			return;
		}
		colorPrint("Passed >>> Test: " + name, ANSI_GREEN);
	}

	private static void summary() {
		System.out.println();
		System.out.println("-------------- Summary --------------");
		if (testsFailed > 0) {
			colorPrint(testsFailed + "/" + totalTests + " tests failed.", ANSI_RED);
		} else {
			colorPrint("Passed all " + totalTests + " tests.", ANSI_GREEN);
		}
	}

	public static final String ALL_YOU_NEED_FILENAME = "resources/hw3/all_you_need.txt";
	public static final String EMMA_FILENAME = "resources/hw3/emma.txt";
	public static final String EMPTY_FILENAME = "resources/hw3/empty.txt";
	public static final String ONLY_SPACES_FILENAME = "resources/hw3/only_spaces.txt";
	public static final String ONLY_ILLEGAL_FILENAME = "resources/hw3/only_illegal.txt";
	public static final String LEGAL_FILENAME = "resources/hw3/legal.txt";
	public static final String SAME_WORD_REPEATS_FILENAME = "resources/hw3/same_word_repeats.txt";
	public static final String SINGLE_WORD_FILENAME = "resources/hw3/single_word.txt";

	public static final String ALL_YOU_NEED_MODEL_DIR = "resources/hw3/file_generation_test/all_you_need_model";
	public static final String ALL_YOU_NEED_REFERENCE_MODEL_DIR = "resources/hw3/all_you_need_model";

	public static final String EMPTY_MODEL_DIR = "resources/hw3/file_generation_test/empty_model";
	public static final String EMPTY_REFERENCE_MODEL_DIR = "resources/hw3/empty_model";

	private static boolean filesAreIdentical(String path1, String path2) throws IOException {
		try (BufferedReader reader1 = new BufferedReader(new FileReader(new File(path1)))) {
			try (BufferedReader reader2 = new BufferedReader(new FileReader(new File(path2)))) {
				final String GOT_TO_EOF = null;
				String nextLine;
				while ((nextLine = reader1.readLine()) != GOT_TO_EOF) {
					if (!nextLine.equals(reader2.readLine())) {
						return false;
					}
				}
				return reader2.readLine() == GOT_TO_EOF;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		BigramModel sG = new BigramModel();
		String[] allYouNeedVocabulary = sG.buildVocabularyIndex(ALL_YOU_NEED_FILENAME);
		test(allYouNeedVocabulary.length == 5, "1.1");
		test(Arrays.equals(allYouNeedVocabulary, new String[] { "love", "all", "you", "need", "is" }), "1.2");
		// test(sG.buildVocabularyIndex(EMMA_FILENAME).length ==
		// BigramModel.MAX_VOCABULARY_SIZE,
		// "1.3 - max vocabulary size"); TODO: create a big enough file
		String[] emptyVocabulary = sG.buildVocabularyIndex(EMPTY_FILENAME);
		test(emptyVocabulary.length == 0, "1.4 - empty file");

		String[] onlySpacesVocabulary = sG.buildVocabularyIndex(ONLY_SPACES_FILENAME);
		test(onlySpacesVocabulary.length == 0, "1.5 - only spaces");

		String[] onlyIllegalVocabulary = sG.buildVocabularyIndex(ONLY_ILLEGAL_FILENAME);
		test(onlyIllegalVocabulary.length == 0, "1.6 - only illegal");

		String[] legalVocabulary = sG.buildVocabularyIndex(LEGAL_FILENAME);
		final String[] expected__1_7 = new String[] { "a", "2a", "abc", "a2", "some_num", "a,", "a+2", "last" };
		test(Arrays.equals(legalVocabulary, expected__1_7), "1.7 - legal");

		int[][] allYouNeedCountsArray = sG.buildCountsArray(ALL_YOU_NEED_FILENAME, allYouNeedVocabulary);
		test(allYouNeedCountsArray[0][0] == 3, "2.1");
		test(allYouNeedCountsArray[1][2] == 3, "2.2");
		test(allYouNeedCountsArray[0][2] == 0, "2.2.1");
		test(allYouNeedCountsArray.length == 5, "2.3");
		test(allYouNeedCountsArray.length == 5, "2.4");

		int[][] emptyCountsArray = sG.buildCountsArray(EMPTY_FILENAME, emptyVocabulary);
		test(emptyCountsArray.length == 0, "2.5 - empty file");

		String[] sameWordVocabulary = sG.buildVocabularyIndex(SAME_WORD_REPEATS_FILENAME);
		int[][] sameWordCountsArray = sG.buildCountsArray(SAME_WORD_REPEATS_FILENAME, sameWordVocabulary);
		test(sameWordCountsArray[0][0] == 6, "2.6 - same word repeats");

		String[] singleWordVocabulary = sG.buildVocabularyIndex(SINGLE_WORD_FILENAME);
		int[][] singleWordCountsArray = sG.buildCountsArray(SINGLE_WORD_FILENAME, singleWordVocabulary);
		test(singleWordCountsArray[0][0] == 0, "2.7 - single word file");

		sG.initModel(ALL_YOU_NEED_FILENAME);
		sG.saveModel(ALL_YOU_NEED_MODEL_DIR);

		test(filesAreIdentical(ALL_YOU_NEED_MODEL_DIR + ".voc", ALL_YOU_NEED_REFERENCE_MODEL_DIR + ".voc"),
				"3.1 - all-you-need voc files generation");
		test(filesAreIdentical(ALL_YOU_NEED_MODEL_DIR + ".counts", ALL_YOU_NEED_REFERENCE_MODEL_DIR + ".counts"),
				"3.2 - all-you-need counts files generation");
		
		sG.initModel(EMPTY_FILENAME);
		sG.saveModel(EMPTY_MODEL_DIR);

		test(filesAreIdentical(EMPTY_MODEL_DIR + ".voc", EMPTY_REFERENCE_MODEL_DIR + ".voc"),
				"3.3 - empty voc files generation");
		test(filesAreIdentical(EMPTY_MODEL_DIR + ".counts", EMPTY_REFERENCE_MODEL_DIR + ".counts"),
				"3.4 - empty counts files generation");

		sG.loadModel(ALL_YOU_NEED_MODEL_DIR);

		test(Arrays.equals(sG.mVocabulary, new String[] { "love", "all", "you", "need", "is" }), "4.1 - Override");
		
		BigramModel sGNew = new BigramModel();
		sGNew.loadModel(ALL_YOU_NEED_MODEL_DIR);
		test(Arrays.equals(sGNew.mVocabulary, new String[] { "love", "all", "you", "need", "is" }), "4.2 - New bigram model");

		test(sG.getWordIndex("love") == 0, "5.1 - first");
		test(sG.getWordIndex("all") == 1, "5.2 - middle");
		test(sG.getWordIndex("is") == 4, "5.3 - last");
		test(sG.getWordIndex("invalid") == -1, "5.4 - invalid");
		test(sG.getWordIndex("") == -1, "5.5 - empty (and invalid)");

		test(sG.mBigramCounts[0][0] == 3, "6.1");
		test(sG.mBigramCounts[1][2] == 3, "6.2");
		test(sG.getBigramCount("is", "love") == 2, "6.3");
		test(sG.getBigramCount("strawberry", "fields") == 0, "6.4");

		test(sG.getMostFrequentProceeding("is").equals("love"), "7.1");
		sG.initModel(LEGAL_FILENAME);
		test(sG.getMostFrequentProceeding("last") == null, "7.2 - no proceeding");
		
		sG.loadModel(ALL_YOU_NEED_MODEL_DIR);

		test(sG.isLegalSentence("love is all"), "8.1");
		test(!sG.isLegalSentence("love is is"), "8.2");
		test(!sG.isLegalSentence("love the beatles"), "8.3");
		test(!sG.isLegalSentence("beatles"), "8.4 - single word not in dictionary");
		test(sG.isLegalSentence("love"), "8.5 - single word in dictionary");
		test(sG.isLegalSentence(""), "8.5 - empty sentence");

		test(BigramModel.calcCosineSim(new int[] { 1, 2, 0, 4, 2 }, new int[] { 5, 0, 3, 1, 1 }) == 11. / 30, "9.1");
		test(BigramModel.calcCosineSim(new int[] { 0, 0, 0, 0, 0 }, new int[] { 5, 0, 3, 1, 1 }) == -1, "9.2 - only zeros");
		test(BigramModel.calcCosineSim(new int[] { 1, 2, 0, 4, 2 }, new int[] { 0, 0, 0, 0, 0 }) == -1, "9.3 - only zeros");
		test(BigramModel.calcCosineSim(new int[] { 1 }, new int[] { 1 }) == 1, "9.4 - single element in arrays");

		sG.initModel(EMMA_FILENAME);
		
		test(sG.getClosestWord("good").equals("great"), "10.1");
		test(sG.getClosestWord("emma").equals("she"), "10.2");
		test(sG.getClosestWord("EMMA").equals("she"), "10.2.1 - caps insensitivity");
		
		sG.initModel(SINGLE_WORD_FILENAME);
		test(sG.getClosestWord("a").equals("a"), "10.3 - single word file");
		test(sG.getClosestWord("A").equals("a"), "10.3.1 - caps insensitivity");

		sG.initModel(SAME_WORD_REPEATS_FILENAME);
		test(sG.getClosestWord("home").equals("home"), "10.4 - same word repeats file");

		sG.initModel(LEGAL_FILENAME);
		test(sG.getClosestWord("abc").equals(BigramModel.SOME_NUM), "10.5 - SOME_NUM");
		test(sG.getClosestWord("a").equals("a,"), "10.6 - multiple candidates");
		test(sG.getClosestWord("a,").equals("a"), "10.7 - multiple candidates backward");
		test(sG.getClosestWord("a+2").equals("a"), "10.7.1 - multiple candidates backward");

		summary();

	}
}

