package com.insidecoding.sos.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.insidecoding.sos.io.FileUtils;

/**
 * This class offers helper methods to manipulate XML files. It does very basic
 * stuff for now. It will be further enhanced with new methods in the near
 * future.
 * 
 * @author ludovicianul
 * 
 */
public final class XMLUtils {

	/**
	 * The Logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(XMLUtils.class);

	/**
	 * FileUtils instance.
	 */
	private FileUtils fileUtil;

	/**
	 * Empty constructor.
	 */
	public XMLUtils() {
		this.fileUtil = new FileUtils();
	}

	/**
	 * Creates a new XMLUtils instance based on an existing FileUtils intance.
	 * 
	 * @param fu
	 *            a FileUtils instance.
	 */
	public XMLUtils(final FileUtils fu) {
		this.fileUtil = fu;
	}

	/**
	 * You must pass the xml string and the tag name. This should be called for
	 * situations where you want to get the value of a simple tag:
	 * &lt;age&gt;18&lt;/age&gt;.
	 * 
	 * In this case, in order to get the 18 value we call the method like this:
	 * getTagText(xml, "age"); and this will return 18
	 * 
	 * The method will return the first tag it encounters.
	 * 
	 * @param xmlString
	 *            the XML string
	 * @param tagName
	 *            the tag name to be searched for
	 * @return the value corresponding to the {@code tagName}
	 * 
	 * @throws ParserConfigurationException
	 *             if something goes wrong while parsing the XML
	 * @throws SAXException
	 *             if XML is malformed
	 * @throws IOException
	 *             if something goes woring when reading the file
	 * 
	 */
	public String getTagText(final String xmlString, final String tagName)
			throws ParserConfigurationException, SAXException, IOException {

		NodeList nodes = this.getNodeList(xmlString, tagName);

		LOG.info("Elements returned = " + nodes.getLength());

		Element element = (Element) nodes.item(0);

		LOG.info("element text = " + element.getTextContent());

		return element.getTextContent();

	}

	/**
	 * You must pass the xml string and the tag name. This should be called for
	 * situations where you want to get the value of a simple tag:
	 * &lt;age&gt;18&lt;/age&gt;.
	 * 
	 * In this case, in order to get the 18 value we call the method like this:
	 * getTagText(xml, "age"); and this will return 18
	 * 
	 * The method will return the first tag it encounters.
	 * 
	 * @param xmlFile
	 *            the XML file
	 * @param fileEncoding
	 *            the XML file encoding. For example "UTF-8"
	 * @param tagName
	 *            the tag name to be searched for
	 * @return the value corresponding to the {@code tagName}
	 * 
	 * @throws ParserConfigurationException
	 *             if something goes wrong while parsing the XML
	 * @throws SAXException
	 *             if XML is malformed
	 * @throws IOException
	 *             if something goes woring when reading the file
	 */
	public String getTagText(final File xmlFile, final String fileEncoding,
			final String tagName) throws ParserConfigurationException,
			SAXException, IOException {

		return this.getTagText(
				fileUtil.getFileContentsAsString(xmlFile, fileEncoding),
				tagName);
	}

	/**
	 * You must pass the xml string the tag name and the attribute of the tag
	 * that you want to be returned This should be called for situations like
	 * this: &lt;age gender="male"&gt;18&lt;/age&gt;.
	 * 
	 * In this case, in order to get the MALE value we call the method like
	 * this: getValueOfTagAttribute(xml, "age", "gender"); and this will return
	 * MALE
	 * 
	 * The method returns the first tag it encounters.
	 * 
	 * @param xmlFile
	 *            the XML file
	 * @param fileEncoding
	 *            the XML file encoding. For example "UTF-8"
	 * @param tagName
	 *            the tag name to be searched for
	 * @param attribute
	 *            the attribute name for the tag
	 * 
	 * @return the value of the {@code attribute} corresponding to the
	 *         {@code tagName}
	 * 
	 * @throws ParserConfigurationException
	 *             if something goes wrong while parsing the XML
	 * @throws SAXException
	 *             if XML is malformed
	 * @throws IOException
	 *             if something goes wrong when reading the file
	 */
	public String getValueOfTagAttribute(final File xmlFile,
			final String fileEncoding, final String tagName,
			final String attribute) throws ParserConfigurationException,
			SAXException, IOException {
		return this.getValueOfTagAttribute(
				fileUtil.getFileContentsAsString(xmlFile, fileEncoding),
				tagName, attribute);
	}

	/**
	 * You must pass the xml string the tag name and the attribute of the tag
	 * that you want to be returned This should be called for situations like
	 * this: &lt;age gender="male"&gt;18&lt;/age&gt;.
	 * 
	 * In this case, in order to get the MALE value we call the method like
	 * this: getValueOfTagAttribute(xml, "age", "gender"); and this will return
	 * MALE
	 * 
	 * The method returns the first tag it encounters.
	 * 
	 * @param xmlString
	 *            the XML string
	 * @param tagName
	 *            the tag name
	 * @param attribute
	 *            the attribute name
	 * @return the value of the {@code attribute} corresponding to the
	 *         {@code tagName}
	 * 
	 * @throws ParserConfigurationException
	 *             if something goes wrong while parsing the XML
	 * @throws SAXException
	 *             if XML is malformed
	 * @throws IOException
	 *             if something goes wrong when reading the file
	 */
	public String getValueOfTagAttribute(final String xmlString,
			final String tagName, final String attribute)
			throws ParserConfigurationException, SAXException, IOException {

		NodeList nodes = this.getNodeList(xmlString, tagName);

		LOG.info("Elements returned = " + nodes.getLength());

		Element element = (Element) nodes.item(0);

		LOG.info("element text = " + element.getAttribute(attribute));

		return element.getAttribute(attribute);

	}

	/**
	 * Gets the nodes list for a given tag name.
	 * 
	 * @param xmlString
	 *            the XML String
	 * @param tagName
	 *            the tag name to be searched
	 * @return the Node List for the given tag name
	 * 
	 * @throws ParserConfigurationException
	 *             if something goes wrong while parsing the XML
	 * @throws SAXException
	 *             if XML is malformed
	 * @throws IOException
	 *             if something goes wrong when reading the file
	 */
	private NodeList getNodeList(final String xmlString, final String tagName)
			throws ParserConfigurationException, SAXException, IOException {
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
		return nodes;
	}

}
