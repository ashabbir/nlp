package com.nlp.indexer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.nlp.common.Annotation;
import com.nlp.common.DataReader;
import com.nlp.common.NameFinder;
import com.nlp.common.SentenceDetector;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;


public class Runner {

	// this is machine specific
	public static String model_dir = System.getProperty("user.dir") +"/models/";
	public static String data_dir = System.getProperty("user.dir") + "/data/";


	// Tokenize a sentence
	public static String[] Tokenize(String sent) throws InvalidFormatException,
			IOException {

		// load token model
		InputStream is = new FileInputStream(model_dir + "en-token.bin");
		TokenizerModel model = new TokenizerModel(is);
		is.close();

		// tokenize sentence
		Tokenizer tokenizer = new TokenizerME(model);
		String tokens[] = tokenizer.tokenize(sent);

		return tokens;
	}

	

	// run the pipeline
	public static void main(String[] args) throws InvalidFormatException,
			IOException {

		StringBuffer buffer = new StringBuffer();
		buffer.append(new DataReader(data_dir).process());
		String para = buffer.toString();
		System.out.println("All text: " + para.length());
		System.out.println("******************************");

		NameFinder finder = new NameFinder(model_dir);
		SentenceDetector sent_detector = new SentenceDetector(model_dir);

		String sentences[] = sent_detector.process(para);
		System.out.println("total sentences: " + sentences.length ) ;
		System.out.println("******************************");
		
		for (String sent : sentences) {

			//System.out.println("Sentence: " + sent);

			String tokens[] = Tokenize(sent);
			List<Annotation> results = finder.process(tokens);
			//for(Annotation r : results) { System.out.println(r);}
			
		}
		System.out.println("******************************");
		System.out.println("Done");
	}

}
