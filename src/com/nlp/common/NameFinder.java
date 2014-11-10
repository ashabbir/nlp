package com.nlp.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class NameFinder {

	private String[] n = { "person", "location", "date", "money",
			"organization", "time", "percentage" };

	private NameFinderME[] finders;

	public NameFinder(String model_dir) throws InvalidFormatException,
			IOException {

		finders = new NameFinderME[n.length];
		for (int i = 0; i < n.length; i++) {
			finders[i] = new NameFinderME(new TokenNameFinderModel(
					new FileInputStream(new File(model_dir + "en-ner-" + n[i]
							+ ".bin"))));
		}
	}

	public List<Annotation> process(String tokens[]) {

		List<Annotation> allAnnotations = new ArrayList<Annotation>();
		for (int fi = 0; fi < finders.length; fi++) {

			Span[] spans = finders[fi].find(tokens);
			double[] probs = finders[fi].probs(spans);

			for (int ni = 0; ni < spans.length; ni++) {
				Annotation a = new Annotation(n[fi], spans[ni], probs[ni]);
				allAnnotations.add(a);
			}
			display_names(spans, tokens);

		}

		if (!allAnnotations.isEmpty()) {
			removeConflicts(allAnnotations);
		}

		return allAnnotations;
	}

	private void removeConflicts(List<Annotation> allAnnotations) {
		java.util.Collections.sort(allAnnotations);

		List<Annotation> stack = new ArrayList<Annotation>();
		stack.add(allAnnotations.get(0));
		for (int ai = 1; ai < allAnnotations.size(); ai++) {
			Annotation curr = (Annotation) allAnnotations.get(ai);
			boolean deleteCurr = false;
			for (int ki = stack.size() - 1; ki >= 0; ki--) {
				Annotation prev = (Annotation) stack.get(ki);
				if (prev.getSpan().equals(curr.getSpan())) {
					if (prev.getProb() > curr.getProb()) {
						deleteCurr = true;
						break;
					} else {
						allAnnotations.remove(stack.remove(ki));
						ai--;
					}
				} else if (prev.getSpan().intersects(curr.getSpan())) {
					if (prev.getProb() > curr.getProb()) {
						deleteCurr = true;
						break;
					} else {
						allAnnotations.remove(stack.remove(ki));
						ai--;
					}
				} else if (prev.getSpan().contains(curr.getSpan())) {
					break;
				} else {
					stack.remove(ki);
				}

			}
			if (deleteCurr) {
				allAnnotations.remove(ai);
				ai--;
				deleteCurr = false;
			} else {
				stack.add(curr);
			}
		}
	}

	public void display_names(Span[] spans, String[] tokens) {
		for (int si = 0; si < spans.length; si++) {
			StringBuilder sb = new StringBuilder();
			for (int ti = spans[si].getStart(); ti < spans[si].getEnd(); ti++) {
				sb.append(tokens[ti]).append(" ");
			}

			System.out.println(spans[si].getType() + " ==> "
					+ sb.substring(0, sb.length() - 1));

		}
	}
}
