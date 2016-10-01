package com.framework.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.framework.log.Logger;

public class XmlUtils {

	public static Document read(String path) {
		return read(new File(path));
	}

	public static Document read(File file) {
		Document doc = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(file);
		} catch (Exception e) {
			Logger.error(e);
		}
		return doc;
	}

	public static Document read(URL url) {
		Document doc = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(url);
		} catch (Exception e) {
			Logger.error(e);
		}
		return doc;
	}

	public static void write(Document doc, String file) {
		write(doc, new File(file));
	}

	public static void write(Document doc, File file) {
		try {
			XMLOutputter xmlopt = new XMLOutputter();
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			Format fm = Format.getPrettyFormat();
			xmlopt.setFormat(fm);
			xmlopt.output(doc, writer);
			writer.close();
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	public static void write(Element root, String file) {
		write(new Document(root), file);
	}

	public static void write(Element root, File file) {
		write(new Document(root), file);
	}

}
