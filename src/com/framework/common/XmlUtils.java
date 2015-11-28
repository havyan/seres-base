package com.framework.common;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;

import org.jdom2.Document;
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
            FileWriter writer = new FileWriter(file);
            Format fm = Format.getPrettyFormat();
            xmlopt.setFormat(fm);
            xmlopt.output(doc, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
