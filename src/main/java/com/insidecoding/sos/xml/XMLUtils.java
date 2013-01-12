package com.insidecoding.sos.xml;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * This class offers helper methods to manipulate XML files
 * 
 * @author ludovicianul
 * 
 */
public final class XMLUtils {
	private static final Logger LOG = Logger.getLogger(XMLUtils.class);

	private XMLUtils() {

	}

	/**
	 * You must pass the xml string and the tag name. This should be called for
	 * situations like this: <age>18</age>
	 * 
	 * In this case, in order to get the 18 value we call the method like this:
	 * getTagText(xml, "age"); and this will return 18
	 * 
	 * The method will return the first tag it encounters
	 * 
	 * @param xmlString
	 * @param tagName
	 * @return
	 */
	public String getTagText(String xmlString, String tagName) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			dbf.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
					false);
			dbf.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-external-dtd",
					false);

			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlString));

			Document doc = db.parse(is);
			LOG.info("Getting tagName: " + tagName);
			NodeList nodes = doc.getElementsByTagName(tagName);

			LOG.info("Elements returned = " + nodes.getLength());

			Element element = (Element) nodes.item(0);

			LOG.info("element text = " + element.getTextContent());

			return element.getTextContent();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * You must pass the xml string the tage name and the attribute of the tag
	 * that you want to be returned This should be called for situations like
	 * this: <age gender="male">18</age>
	 * 
	 * In this case, in order to get the MALE value we call the method like
	 * this: getValueOfTagAttribute(xml, "age", "gender"); and this will return
	 * MALE
	 * 
	 * The method returns the first tag it encounter
	 * 
	 * @param xmlString
	 * @param tagName
	 * @param attribute
	 * @return
	 */
	public String getValueOfTagAttribute(String xmlString, String tagName,
			String attribute) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			dbf.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
					false);
			dbf.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-external-dtd",
					false);

			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlString));

			Document doc = db.parse(is);
			LOG.info("Getting tagName: " + tagName);
			NodeList nodes = doc.getElementsByTagName(tagName);

			LOG.info("Elements returned = " + nodes.getLength());

			Element element = (Element) nodes.item(0);

			LOG.info("element text = " + element.getAttribute(attribute));

			return element.getAttribute(attribute);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
