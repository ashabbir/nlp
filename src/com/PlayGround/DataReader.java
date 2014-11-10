package com.PlayGround;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class DataReader {

	private String data_dir;

	public DataReader(String data_dir) {
		this.data_dir = data_dir;
	}

	public String process() {
		StringBuffer data_buff = new StringBuffer();
		File folder = new File(data_dir);
		File[] listOfFiles = folder.listFiles();
		
		for (File f : listOfFiles) {
			System.out.println("Reading: " + f.getName());

			if (f.getName().endsWith(".pdf")) {
				read_pdf(f, data_buff);
			} else if (f.getName().endsWith(".txt")) {
				read_txt(f, data_buff);
			}
		}
		return data_buff.toString();

	}

	private void read_pdf(File f, StringBuffer data_buff) {
		PDDocument pd;
		try {
			pd = PDDocument.load(f);
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(pd);
			if (pd != null) {
				pd.close();
			}

			if (text != null && !text.isEmpty()) {
				data_buff.append(text.replace("\n", "").replace("  ", " "))
						.append(text);
				data_buff.append(System.getProperty("line.separator"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void read_txt(File f, StringBuffer data_buff) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			String text = null;

			while ((text = reader.readLine()) != null) {
				if (text != null && !text.isEmpty()) {
					data_buff.append(text.replace("\n", "").replace("  ", " "))
							.append(text);
					data_buff.append(text + ".");
					data_buff.append(System.getProperty("line.separator"));
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
