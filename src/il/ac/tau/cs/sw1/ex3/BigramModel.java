package il.ac.tau.cs.sw1.ex3;


import java.io.*;

public class BigramModel {
	public static final int MAX_VOCABULARY_SIZE = 14500;
	public static final String VOC_FILE_SUFFIX = ".voc";
	public static final String COUNTS_FILE_SUFFIX = ".counts";
	public static final String SOME_NUM = "some_num";
	public static final int ELEMENT_NOT_FOUND = -1;
	
	String[] mVocabulary;
	int[][] mBigramCounts;
	
	// DO NOT CHANGE THIS !!! 
	public void initModel(String fileName) throws IOException{
		mVocabulary = buildVocabularyIndex(fileName);
		mBigramCounts = buildCountsArray(fileName, mVocabulary);
		
	}

	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	private boolean [] is_valid_word (String word){
		char [] chr_arr = word.toCharArray();
		boolean is_int = true;
		boolean is_word = false;
		for (char ch : chr_arr){
			if (Character.isLetter(ch)) is_word = true;
			if (!Character.isDigit(ch)) is_int = false;
		}
        return new boolean[]{is_word,is_int};
	}
	public String[] buildVocabularyIndex(String fileName) throws IOException{ // Q 1
		File input = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(input));
		String [] vocabulary_index = new String[MAX_VOCABULARY_SIZE];
		int curr_len = 0;
		String len_to_check = bufferedReader.readLine();
		boolean contains_int = false;
		while (curr_len < MAX_VOCABULARY_SIZE && len_to_check != null){
			String [] words_check = len_to_check.split("\\s+");
			for (String word: words_check){
				if (word.isEmpty()) continue;
				word = word.toLowerCase(); //used for compare between strings and insert
				boolean [] validation = is_valid_word(word);
				boolean is_word = validation[0];
				boolean is_int = validation[1];
				if (is_word) {
					boolean is_dup = false;
					for (int k=0; k<curr_len; k++) {
						if (vocabulary_index[k].equals(word)) { //note that vocabulary and word are lowercase
							is_dup = true;
							break;
						}
						}
					if (!is_dup) {
						vocabulary_index[curr_len] = word;
						curr_len++;
					}
				}
				else if (is_int && !contains_int){
					vocabulary_index[curr_len] = SOME_NUM;
					contains_int = true;
					curr_len++;
				}
				if (curr_len == MAX_VOCABULARY_SIZE) break;
			}
			len_to_check = bufferedReader.readLine();
		}
		bufferedReader.close();
		if (curr_len<MAX_VOCABULARY_SIZE) {
			String[] voc_index_2 = new String[curr_len];
            System.arraycopy(vocabulary_index, 0, voc_index_2, 0, curr_len);
			return voc_index_2;
		}
		return vocabulary_index;
	}

	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */

	public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException{ // Q - 2
		File input = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(input));
		int voc_len = vocabulary.length;
		int [][] res = new int [voc_len][voc_len];
		String str_to_check = bufferedReader.readLine();

		while (str_to_check != null) {
			str_to_check = str_to_check.trim();
			if (str_to_check.isEmpty()){
				str_to_check = bufferedReader.readLine();
				continue;
			}
			String[] words = str_to_check.split("\\s+");
			//iterate for each word in the sentence
			for (int i =0; i< words.length-1; i++) {
				boolean [] is_valid_word1 = is_valid_word(words[i]);
				boolean is_word1 = is_valid_word1[0];
				boolean is_int1 = is_valid_word1[1];
				boolean validation1 = is_word1 || is_int1;
				if (!validation1) continue;

				boolean [] is_valid_word2 = is_valid_word(words[i+1]);
				boolean is_word2 = is_valid_word2[0];
				boolean is_int2 = is_valid_word2[1];
				boolean validation2 = is_word2 || is_int2;
				//integer or word

				if (validation2){
					int index_1 = -1;
					int index_2 = -1;
					for (int j =0; j<voc_len;j++){
						if ((vocabulary[j].equals(words[i].toLowerCase())) || (vocabulary[j].equals(SOME_NUM)&& is_int1)) index_1 = j;
						if ((vocabulary[j].equals(words[i+1].toLowerCase())) || (vocabulary[j].equals(SOME_NUM)&& is_int2)) index_2 = j;
						//found the index in vocabulary of two words
						if (!(index_1 == -1 || index_2 ==-1)) {
							res[index_1][index_2]++;
							break;
						}
					}
				}
			}
			str_to_check = bufferedReader.readLine();
		}
		bufferedReader.close();
		return res;
	}

	
	
	/*
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: fileName is a legal file path
	 */
	public void saveModel(String fileName) throws IOException{ // Q-3
		fileName = fileName.endsWith(".txt")? fileName.substring(0,fileName.length()-4):fileName;
		File voc = new File(fileName+VOC_FILE_SUFFIX);
		BufferedWriter writer = new BufferedWriter(new FileWriter(voc));
		int len_voc = mVocabulary.length;
		writer.write(len_voc + " words");
		writer.newLine();
		for (int i=0; i<len_voc; i++){
			writer.write(i+","+mVocabulary[i]);
			writer.newLine();
		}
		// write the count file
		File cnt = new File(fileName+COUNTS_FILE_SUFFIX);
		BufferedWriter writer1 = new BufferedWriter(new FileWriter(cnt));
		for (int j=0; j<len_voc; j++){
			for (int k=0; k<len_voc; k++){
				int tmp = mBigramCounts[j][k];
				if (tmp !=0){
					writer1.write(j+","+k+":"+tmp);
					writer1.newLine();
				}
			}
		}
		writer.close();
		writer1.close();

	}

	/*
	 * @pre: fileName is a legal file path
	 */
	public void loadModel(String fileName) throws IOException{ // Q - 4
		fileName = fileName.endsWith(".txt")? fileName.substring(0,fileName.length()-4):fileName;
		File voc = new File(fileName+VOC_FILE_SUFFIX);
		BufferedReader voc_read = new BufferedReader(new FileReader(voc));
		String line = voc_read.readLine();
		int len_voc = Integer.parseInt(line.split(" ")[0]);
		String [] new_voc = new String[len_voc];
		line = voc_read.readLine(); //start with the first word in the loop
		while (line != null){
			String[] extract = line.split(",");
			int index = Integer.parseInt(extract[0]);
			String value = extract[1];
			new_voc[index] = value;
			line = voc_read.readLine();
		}
		voc_read.close();
		mVocabulary = new_voc;

		// this part is for loading counts
		File counts = new File(fileName+COUNTS_FILE_SUFFIX);
		BufferedReader cnt_read = new BufferedReader(new FileReader(counts));
		String line2 = cnt_read.readLine();
		int [][] new_cnt = new int[len_voc][len_voc]; //by default each int is 0
		while (line2!= null){
			int i = Integer.parseInt(String.valueOf(line2.charAt(0)));
			int j = Integer.parseInt(String.valueOf(line2.charAt(2)));
			int cnt = Integer.parseInt(String.valueOf(line2.charAt(4)));
			new_cnt[i][j] = cnt;
			line2 = cnt_read.readLine();
		}
		cnt_read.close();
		mBigramCounts = new_cnt;

	}
	/*
	 * @pre: word is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
	 */
	public int getWordIndex(String word){  // Q - 5
		int n = mVocabulary.length;
		for(int i =0; i<n; i++){
			if (mVocabulary[i].equals(word)) return i;
		}
		return ELEMENT_NOT_FOUND;
	}

	/*
	 * @pre: word1, word2 are in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
	 * exist in the vocabulary, $ret = 0
	 */
	public int getBigramCount(String word1, String word2){ //  Q - 6
		int word1_index = getWordIndex(word1);
		int word2_index = getWordIndex(word2);
		if (word1_index == -1 || word2_index == -1){
			return 0;
		}
		else {
            return mBigramCounts[word1_index][word2_index];
        }
	}
	
	/*
	 * @pre word in lowercase, and is in mVocabulary
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post $ret = the word with the lowest vocabulary index that appears most fequently after word (if a bigram starting with
	 * word was never seen, $ret will be null
	 */
	public String getMostFrequentProceeding(String word){ //  Q - 7
		int i = getWordIndex(word);
		int n = mVocabulary.length;
		int max_freq = 0;
		int max_index = -1;
		for (int j=0; j<n; j++){
			if(mBigramCounts[i][j] > max_freq) {
				max_index = j;
				max_freq = mBigramCounts[i][j];
			}
		}
		if (max_freq >0){
			return mVocabulary[max_index];
		}
        return null; //if all in lst is 0
    }
	
	/* @pre: sentence is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: each two words in the sentence are are separated with a single space
	 * @post: if sentence is is probable, according to the model, $ret = true, else, $ret = false
	 */
	public boolean isLegalSentence(String sentence){  //  Q - 8
		if (sentence.isEmpty()) return true;
		String [] words = sentence.split("\\s+");
		int len_voc = mVocabulary.length;
		int prev_index = -1;
		boolean in_voc = false; //checks if word in dic
		for(int k=0 ; k<len_voc; k++) {
			if (words[0].equals(mVocabulary[k])) {
				in_voc = true; //word in dic
				prev_index = k;
				break;
			}
		}
		if (!in_voc) return false;
		for (int j=0;j<words.length-1;j++){
			int curr_index = -1;
			in_voc = false;
			for(int i=0 ; i<len_voc; i++){
				if(words[j+1].equals(mVocabulary[i])){
					curr_index = i;
					in_voc = true;
					break;
				}
				}
			if (!in_voc) return false;
			else if (mBigramCounts[prev_index][curr_index] == 0) return false;
			prev_index = curr_index;
			}

		return true;
	}

	/*
	 * @pre: arr1.length = arr2.legnth
	 * post if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calcluates CosineSim
	 */
	public static double calcCosineSim(int[] arr1, int[] arr2){ //  Q - 9
		int multiplier = 0;
		int tot_a = 0;
		int tot_b = 0;
		for (int i=0; i<arr1.length; i++){
			int tmp1 = arr1[i];
			int tmp2 = arr2[i];
			multiplier += tmp1*tmp2;
			tot_a += tmp1*tmp1;
			tot_b += tmp2*tmp2;
		}
		if (multiplier == 0 || tot_a == 0 || tot_b == 0) return -1;
		double denominator = Math.sqrt(tot_a) * Math.sqrt(tot_b);
		return multiplier/denominator;
	}

	
	/*
	 * @pre: word is in vocabulary
	 * @pre: the method initModel was called (the language model is initialized), 
	 * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w) among all the
	 * other words in vocabulary
	 */
	public String getClosestWord(String word){ //  Q - 10
		int n = mVocabulary.length;
		int word_i = -1; //never be -1 at the end because word in voc
		for (int i=0; i<n; i++){
			if (mVocabulary[i].equals(word.toLowerCase())){
				word_i = i;//found index of word
				break;
			}
		}
		int [] word_vector = new int[n];
        System.arraycopy(mBigramCounts[word_i], 0, word_vector, 0, n);

		int max_index = 0;
		double max_score = -1.0;
		int [] vector_to_check = new int[n];

		// iterate over all words in mBiagramCounts
		for (int k=0; k<n; k++){
			if (k == word_i) continue;
			System.arraycopy(mBigramCounts[k],0,vector_to_check,0,n);
			double curr_score = calcCosineSim(vector_to_check,word_vector);
			if (curr_score> max_score) {
				max_score = curr_score;
				max_index = k;
			}
		}

		return mVocabulary[max_index];
	}

	
}
