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

  
  private MaxentModel model;
  private double[] probs;
  private AnswerTypeContextGenerator atcg;

  public AnswerTypeClassifier(MaxentModel model, double[] probs, AnswerTypeContextGenerator atcg) {
    this.model = model;
    this.probs = probs;
    this.atcg = atcg;
  }


  //<start id="atc.compute"/>
  public String computeAnswerType(Parse question) {
    double[] probs = computeAnswerTypeProbs(question);//<co id="atc.getprobs"/>
    return model.getBestOutcome(probs);//<co id="atc.outcome"/>
  }

  public double[] computeAnswerTypeProbs(Parse question) {
    String[] context = atcg.getContext(question);//<co id="atc.context"/>
    return model.eval(context, probs);//<co id="atc.evaluate"/>
  }
  
  
  
  
  
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.err.println("Usage: AnswerTypeClassifier trainFile modelFile");
      System.exit(1);
    }
    String trainFile = args[0];
    File outFile = new File(args[1]);
    String modelsDirProp = System.getProperty("model.dir");
    File modelsDir = new File(modelsDirProp);
    String wordnetDir = System.getProperty("wordnet.dir");
    InputStream chunkerStream = new FileInputStream(
        new File(modelsDir,"en-chunker.bin"));
    ChunkerModel chunkerModel = new ChunkerModel(chunkerStream);
    ChunkerME chunker = new ChunkerME(chunkerModel);
    InputStream posStream = new FileInputStream(
        new File(modelsDir,"en-pos-maxent.bin"));
    POSModel posModel = new POSModel(posStream);
    POSTaggerME tagger =  new POSTaggerME(posModel);
    Parser parser = new ChunkParser(chunker, tagger);
    AnswerTypeContextGenerator actg = new AnswerTypeContextGenerator(new File(wordnetDir));
    //<start id="atc.train"/>
    AnswerTypeEventStream es = new AnswerTypeEventStream(trainFile,
            actg, parser);
    GISModel model = GIS.trainModel(100, new TwoPassDataIndexer(es, 3));//<co id="atc.train.do"/>
    new DoccatModel("en", model).serialize(new FileOutputStream(outFile));

  }
}