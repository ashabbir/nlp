package com.PlayGround;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

public class SentenceDetector {
	
	private String model_dir;
	
	
	public SentenceDetector (String dir){
		this.model_dir = dir;
	}
	
	
	// detect sentences
	public String[] process(String paragraph)
			throws InvalidFormatException, IOException {

		// load sentence model
		InputStream is = new FileInputStream(model_dir + "en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		is.close();

		// split sentence
		SentenceDetectorME sdetector = new SentenceDetectorME(model);
		String sentences[] = sdetector.sentDetect(paragraph);

		return sentences;
	}


}
