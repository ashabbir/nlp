package com.nlp.question;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


import com.nlp.classifier.AnswerTypeClassifier;
import com.nlp.classifier.AnswerTypeContextGenerator;
import com.nlp.common.ChunkParser;

import opennlp.model.MaxentModel;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class QuestionClassifier {

	// this is machine specific
	public static String model_dir = System.getProperty("user.dir")
			+ "/models/";
	public static String data_dir = System.getProperty("user.dir") + "/data/";
	public static String wordnet_dir = System.getProperty("user.dir")
			+ "/wordnet/dict/";

	protected MaxentModel model;
	protected double[] probs;
	protected AnswerTypeContextGenerator atcg;
	private POSTaggerME tagger;
	private ChunkerME chunker;

	public String parse(String qstr) {


		
		
		File modelsDir = new File(model_dir);
		try {
			InputStream chunkerStream = new FileInputStream(new File(modelsDir,
					"en-chunker.bin"));
			ChunkerModel chunkerModel = new ChunkerModel(chunkerStream);
			chunker = new ChunkerME(chunkerModel); // <co id="qqpp.chunker"/>
			InputStream posStream = new FileInputStream(new File(modelsDir,
					"en-pos-maxent.bin"));
			POSModel posModel = new POSModel(posStream);
			tagger = new POSTaggerME(posModel); // <co id="qqpp.tagger"/>
			model = new DoccatModel(new FileInputStream( // <co
															// id="qqpp.theModel"/>
					new File(model_dir, "en-answer.bin")))
					.getChunkerModel();
			probs = new double[model.getNumOutcomes()];
			atcg = new AnswerTypeContextGenerator(new File(wordnet_dir));// <co id="qqpp.context"/>
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		AnswerTypeClassifier atc = new AnswerTypeClassifier(model, probs, atcg);// <co
		Parser parser = new ChunkParser(chunker, tagger);

		Parse parse = ParserTool.parseLine(qstr, parser, 1)[0];//<co id="qqp.parseLine"/>
	    return atc.computeAnswerType(parse);

		

	}
	
	public static void main(String[] args){
		
		String[] q_array ={ "Who will pick you up"  ,
				"Who is the presedent of USA",
				"Where is NYU",
				"When will the sun rise tomorrow morning",
				"When does you class start"};
		QuestionClassifier c = new QuestionClassifier();
		for (String qstr : q_array){
			System.out.println(qstr);
			System.out.println(c.parse(qstr));
		}
		
		
		
	}
}
