package com.nlp.classifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.model.MaxentModel;
import opennlp.model.TwoPassDataIndexer;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import com.nlp.common.ChunkParser;

public class AnswerTypeClassifier {

	// this is machine specific
	public static String model_dir = System.getProperty("user.dir")
			+ "/models/";
	public static String data_dir = System.getProperty("user.dir") + "/data/";
	public static String wordnet_dir = System.getProperty("user.dir")
			+ "/wordnet/dict/";

	private MaxentModel model;
	private double[] probs;
	private AnswerTypeContextGenerator atcg;

	public AnswerTypeClassifier(MaxentModel model, double[] probs,
			AnswerTypeContextGenerator atcg) {
		this.model = model;
		this.probs = probs;
		this.atcg = atcg;
	}

	// <start id="atc.compute"/>
	public String computeAnswerType(Parse question) {
		double[] probs = computeAnswerTypeProbs(question);// <co
															// id="atc.getprobs"/>
		return model.getBestOutcome(probs);// <co id="atc.outcome"/>
	}

	public double[] computeAnswerTypeProbs(Parse question) {
		String[] context = atcg.getContext(question);
		return model.eval(context, probs);
	}

	public static void train() throws IOException {

		String trainFile = data_dir + "q_train.dat";
		String endfile = model_dir + "answer.bin";
		File outFile = new File(endfile);

		InputStream chunkerStream = new FileInputStream(new File(model_dir,
				"en-chunker.bin"));
		ChunkerModel chunkerModel = new ChunkerModel(chunkerStream);
		ChunkerME chunker = new ChunkerME(chunkerModel);
		InputStream posStream = new FileInputStream(new File(model_dir,
				"en-pos-maxent.bin"));
		POSModel posModel = new POSModel(posStream);
		POSTaggerME tagger = new POSTaggerME(posModel);
		Parser parser = new ChunkParser(chunker, tagger);
		AnswerTypeContextGenerator actg = new AnswerTypeContextGenerator(
				new File(wordnet_dir));

		AnswerTypeEventStream es = new AnswerTypeEventStream(trainFile, actg,
				parser);
		GISModel model = GIS.trainModel(100, new TwoPassDataIndexer(es, 3));

		new DoccatModel("en", model).serialize(new FileOutputStream(outFile));

	}

	public static void main(String[] args) throws IOException {
		train();
	}
	

}