package com.insidecoding.sos.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This class offers helper methods for I/O operations. <br/>
 * It allows you to read/write/append to/from text files, properties files, CSV
 * files and Microsoft Excel files. <br/>
 * This is very helpful for data driven testing. <br/>
 * 
 * @author ludovicianul
 * 
 */
public final class FileUtils {
	/**
	 * Simple cache that holds all the workbooks that will be used within the
	 * current run.
	 */
	private Map<String, Workbook> workbooks = new HashMap<String, Workbook>();

	/**
	 * Holds all the resources that were opened so that they can be released at
	 * the end of the execution cycle.
	 */
	private List<InputStream> fis = new ArrayList<InputStream>();

	/**
	 * Simple cache that holds all the bunldes used within in the application.
	 */
	private Map<String, ResourceBundle> bundles = new HashMap<String, ResourceBundle>();

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(FileUtils.class);

	/**
	 * This will call {@code buildupPropertiesBundles("./src/test/resources")}.
	 */
	public FileUtils() {
		try {
			buildupPropertiesBundles(new File("./src/test/resources"));
		} catch (IOException e) {
			LOG.warn("No bundle was loaded!!");
		}
	}

	/**
	 * Loads all the properties files from this location.
	 * 
	 * @param resourcesLocation
	 *            the path from where the properties files will be loaded. This
	 *            must be a folder otherwise a {@link IOException} will be
	 *            thrown
	 * @throws IOException
	 *             if something goes wrong while reading the resources files
	 */
	public FileUtils(final String resourcesLocation) throws IOException {
		LOG.info("Loading properties files from: " + resourcesLocation);
		File file = new File(resourcesLocation);
		if (!file.exists()) {
			throw new IOException("Invalid path: " + resourcesLocation);
		}
		if (!file.isDirectory()) {
			throw new IOException("The path supplied must be a folder: "
					+ resourcesLocation);
		}
		buildupPropertiesBundles(file);
	}

	/**
	 * Loads all properties files into a bundle cache.
	 * 
	 * @param file
	 *            the folder where the properties files can be found
	 * @throws IOException
	 *             if something goes wrong while reading the file
	 */
	private void buildupPropertiesBundles(final File file) throws IOException {
		File[] files = file.listFiles();

		for (File f : files) {
			if (f.getName().endsWith("properties")) {
				String bundleName = f.getName().substring(0,
						f.getName().indexOf("properties") - 1);

				LOG.info("Loading: " + bundleName);
				ResourceBundle bundle = new PropertyResourceBundle(
						new FileInputStream(f));
				bundles.put(bundleName, bundle);
			}
		}
	}

	/**
	 * Loads a locale specific bundle.
	 * 
	 * @param bundleName
	 *            Name of the bundle to be loaded. This name must be fully
	 *            qualified.
	 * @param locale
	 *            Locale for which the resource bundle will be loaded.
	 */
	public void loadPropertiesBundle(final String bundleName,
			final Locale locale) {
		LOG.info("Loading properties bundle: " + bundleName);
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
		bundles.put(bundleName, bundle);
	}

	/**
	 * Loads a properties bundle with the default locale.
	 * 
	 * @param bundleName
	 *            Name of the bundle to be loaded. This name must be fully
	 *            qualified.
	 */
	public void loadPropertiesBundle(final String bundleName) {
		LOG.info("Loading properties bundle: " + bundleName);
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
		bundles.put(bundleName, bundle);
	}

	/**
	 * Loads the properties from a file specified as a parameter.
	 * 
	 * @param propertiesFile
	 *            Path to the properties file.
	 * @throws IOException
	 *             is something goes wrong while reading the file
	 */
	public void loadPropertiesFromFile(final String propertiesFile)
			throws IOException {
		LOG.info("Loading properties from file: " + propertiesFile);
		File file = new File(propertiesFile);

		String bundleName = file.getPath().substring(0,
				file.getPath().indexOf("properties") - 1);
		FileInputStream inputStream = null;

		try {
			inputStream = new FileInputStream(file);
			ResourceBundle bundle = new PropertyResourceBundle(inputStream);
			LOG.info("Adding to bunlde: " + bundleName);
			bundles.put(bundleName, bundle);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

	}

	/**
	 * Returns a single bundle from the bundles map.
	 * 
	 * @param bundleName
	 *            The name of the bundle to be retrieved.
	 * @return returns the specified bundle from the cache
	 */
	public ResourceBundle getBundle(final String bundleName) {
		LOG.info("Getting bundle: " + bundleName);
		return bundles.get(bundleName);
	}

	/**
	 * Gets the value from the resource bundles corresponding to the supplied
	 * key. <br/>
	 * If the property exists in multiple bundles only the first occurrence will
	 * be returned
	 * 
	 * @param key
	 *            the key to search for
	 * @return {@code Boolean.FALSE} if the property is not found;
	 *         {@code Boolean.TRUE} if the property is true or
	 *         {@code Boolean.FALSE} otherwise
	 */
	public Boolean getPropertyAsBoolean(final String key) {
		LOG.info("Getting value for key: " + key);
		for (ResourceBundle bundle : bundles.values()) {
			try {
				return Boolean.valueOf(bundle.getString(key));
			} catch (MissingResourceException e) {
				LOG.info("Resource: " + key + " not found!");
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * Gets the value from the resource bundles corresponding to the supplied
	 * key. <br/>
	 * 
	 * 
	 * @param bundleName
	 *            the name of the bundle to search in
	 * @param key
	 *            the key to search for
	 * @return {@code Boolean.FALSE} if the property is not found;
	 *         {@code Boolean.TRUE} if the property is true or
	 *         {@code Boolean.FALSE} otherwise
	 */
	public Boolean getPropertyAsBoolean(final String bundleName,
			final String key) {
		LOG.info("Getting value for key: " + key + " from bundle name: "
				+ bundleName);
		ResourceBundle bundle = bundles.get(bundleName);
		Boolean result = Boolean.FALSE;
		try {
			result = Boolean.valueOf(bundle.getString(key));
		} catch (MissingResourceException e) {
			LOG.info("Resource: " + key + " not found!");
		}
		return result;
	}

	/**
	 * Gets the value from the resource bundles corresponding to the supplied
	 * key. <br/>
	 * If the property exists in multiple bundles only the first occurrence will
	 * be returned
	 * 
	 * @param key
	 *            the key to search for
	 * @return {@code null} if the property is not found or the corresponding
	 *         value otherwise
	 */
	public String getPropertyAsString(final String key) {
		LOG.info("Getting value for key: " + key);
		for (ResourceBundle bundle : bundles.values()) {
			try {
				return bundle.getString(key);
			} catch (MissingResourceException e) {
				LOG.info("Resource: " + key + " not found!");
			}
		}
		return null;
	}

	/**
	 * Gets the value from the resource bundles corresponding to the supplied
	 * key. <br/>
	 * 
	 * 
	 * @param bundleName
	 *            the name of the bundle to search in
	 * @param key
	 *            the key to search for
	 * @return {@code null} if the property is not found or the corresponding
	 *         value otherwise
	 */
	public String getPropertyAsString(final String bundleName, final String key) {
		LOG.info("Getting value for key: " + key + " bundleName:" + bundleName);
		ResourceBundle bundle = bundles.get(bundleName);
		String result = null;
		try {
			result = bundle.getString(key);
		} catch (MissingResourceException e) {
			LOG.info("Resource: " + key + " not found!");
		}
		return result;
	}

	/**
	 * Gets the value as int from the resource bundles corresponding to the
	 * supplied key. <br/>
	 * 
	 * If the property exists in multiple bundles only the first occurrence will
	 * be returned
	 * 
	 * @param key
	 *            the key to search for
	 * @return {@code -1} if the property is not found or the value is not a
	 *         number; the corresponding value otherwise
	 */
	public int getPropertyAsInteger(final String key) {
		LOG.info("Getting value for key: " + key);
		int result = -1;
		for (ResourceBundle bundle : bundles.values()) {
			try {
				result = Integer.parseInt(bundle.getString(key));
				break;
			} catch (MissingResourceException e) {
				LOG.info("Resource: " + key + " not found!");
			} catch (NumberFormatException e) {
				result = -1;
			}
		}
		return result;
	}

	/**
	 * Gets the value as int from the resource bundles corresponding to the
	 * supplied key. <br/>
	 * 
	 * 
	 * @param bundleName
	 *            the name of the bunlde to search in
	 * @param key
	 *            the key to search for
	 * @return {@code -1} if the property is not found or the value is not a
	 *         number; the corresponding value otherwise
	 */
	public int getPropertyAsInteger(final String bundleName, final String key) {
		LOG.info("Getting value for key: " + key + " from bundle: "
				+ bundleName);
		ResourceBundle bundle = bundles.get(bundleName);
		try {
			return Integer.parseInt(bundle.getString(key));
		} catch (MissingResourceException e) {
			LOG.info("Resource: " + key + " not found!");
		} catch (NumberFormatException e) {
			return -1;
		}

		return -1;
	}

	/**
	 * Gets the value as double from the resource bundles corresponding to the
	 * supplied key. <br/>
	 * 
	 * If the property exists in multiple bundles only the first occurrence will
	 * be returned
	 * 
	 * @param key
	 *            the key to search for
	 * @return {@code -1} if the property is not found or the value is not a
	 *         number; the corresponding value otherwise
	 */
	public double getPropertyAsDouble(final String key) {
		LOG.info("Getting value for key: " + key);
		double result = -1d;
		for (ResourceBundle bundle : bundles.values()) {
			try {
				result = Double.parseDouble(bundle.getString(key));
				break;
			} catch (MissingResourceException e) {
				LOG.info("Resource: " + key + " not found!");
			} catch (NumberFormatException e) {
				result = -1d;
			}
		}
		return result;
	}

	/**
	 * Gets the value as double from the resource bundles corresponding to the
	 * supplied key. <br/>
	 * 
	 * 
	 * @param bundleName
	 *            the name of the bundle to search in
	 * @param key
	 *            the key to search for
	 * @return {@code -1} if the property is not found or the value is not a
	 *         number; the corresponding value otherwise
	 */
	public double getPropertyAsDouble(final String bundleName, final String key) {
		LOG.info("Getting value for key: " + key + " from bundle: "
				+ bundleName);
		ResourceBundle bundle = bundles.get(bundleName);
		try {
			return Double.parseDouble(bundle.getString(key));
		} catch (MissingResourceException e) {
			LOG.info("Resource: " + key + " not found!");
		} catch (NumberFormatException e) {
			return -1d;
		}
		return -1d;
	}

	/**
	 * Reads the value of a cell from an Excel workbook corresponding to the
	 * supplied coordinates. <br/>
	 * 
	 * 
	 * @param fileName
	 *            the name of the file
	 * @param sheetName
	 *            the name of the sheet within the file/workbook
	 * @param rowNumber
	 *            the row number; starts from 0
	 * @param cellNumber
	 *            the cell number; starts from 0
	 * @return the cell value from the excel file
	 * @throws IOException
	 *             if something goes wrong while reading the file
	 */
	public String readFromExcel(final String fileName, final String sheetName,
			final int rowNumber, final int cellNumber) throws IOException {
		LOG.info("Reading from file: " + fileName + " sheet: " + sheetName
				+ " rowNumber: " + rowNumber + " cellNumber:" + cellNumber);
		Workbook workbook = getWorkbook(fileName);
		Sheet rows = workbook.getSheet(sheetName);

		Row row = rows.getRow(rowNumber);
		Cell cell = row.getCell(cellNumber);
		String result = null;
		switch (cell.getCellType()) {

		case Cell.CELL_TYPE_BOOLEAN:
			result = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_STRING:
			result = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			result = String.valueOf(cell.getNumericCellValue());
			break;
		default:
			result = cell.getStringCellValue();
		}

		LOG.info(" Returning: " + result);
		return result;

	}

	/**
	 * Tries to load the workbook from the current cache. If it doesn't find it
	 * it will load it from the disk. If it doesn't find it on the disk a
	 * {@link IOException} will be thrown
	 * 
	 * @param fileName
	 *            the name of file
	 * @return the workbook corresponding to this file
	 * @throws IOException
	 *             if something goes wrong while reading the file
	 */
	private Workbook getWorkbook(final String fileName) throws IOException {
		if (workbooks.get(fileName) == null) {
			FileInputStream fiStream = new FileInputStream(new File(fileName));
			fis.add(fiStream);
			Workbook workbook = null;
			try {
				workbook = new HSSFWorkbook(fiStream);
			} catch (OfficeXmlFileException e) {
				fiStream.close();
				fiStream = new FileInputStream(new File(fileName));
				fis.add(fiStream);
				workbook = new XSSFWorkbook(fiStream);
			}
			workbooks.put(fileName, workbook);

		}

		return workbooks.get(fileName);
	}

	/**
	 * Gets the number of rows populated within the supplied file and sheet
	 * name.
	 * 
	 * @param fileName
	 *            the name of the file
	 * @param sheetName
	 *            the sheet name
	 * @return the number of rows
	 * @throws IOException
	 *             if something goes wrong while reading the file
	 */
	public int getNumberOfRows(final String fileName, final String sheetName)
			throws IOException {
		LOG.info("Getting the number of rows from:" + fileName + " sheet: "
				+ sheetName);
		Workbook workbook = getWorkbook(fileName);
		Sheet sheets = workbook.getSheet(sheetName);
		return sheets.getPhysicalNumberOfRows();
	}

	/**
	 * You MUST call this method after you finish running your tests. <br/>
	 * Usually you can do this in a @After method (tearDown()). <br/>
	 * If you just extend the provided {@code AbstractSoSBase} you won't need to
	 * worry about it
	 */
	public void releaseResources() {
		for (InputStream str : fis) {
			try {
				str.close();
			} catch (Exception e) {
				LOG.info("Exception while trying to close streams", e);
			}
		}
	}

	/**
	 * Returns the contents of a file into a String object. <br/>
	 * Please be careful when using this with large files
	 * 
	 * @param file
	 *            the file
	 * @param encoding
	 *            the file encoding. Example: "UTF-8", "UTF-16"
	 * @return a String object with the contents of the file
	 * @throws IOException
	 *             if something goes wrong while reading the file
	 */
	public String getFileContentsAsString(final File file, final String encoding)
			throws IOException {
		LOG.info("Getting files contents as string: " + file);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), encoding));

			StringBuilder builder = new StringBuilder();

			String line = null;
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}

			LOG.debug("File contents: " + builder);
			return builder.toString();
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	/**
	 * Returns the contents of a file into a String object. <br/>
	 * Please be careful when using this with large files
	 * 
	 * @param fileName
	 *            the name of the file
	 * @param encoding
	 *            the file encoding. Examples: "UTF-8", "UTF-16"
	 * @return a String object with the contents of the file
	 * @throws IOException
	 *             if something goes wrong while reading the file
	 */
	public String getFileContentsAsString(final String fileName,
			final String encoding) throws IOException {
		return this.getFileContentsAsString(new File(fileName), encoding);
	}

	/**
	 * Parses a CSV file based on the header received.
	 * 
	 * @param headers
	 *            a comma separated list of headers
	 * @param file
	 *            the file to read from
	 * @param separator
	 *            the separator used in the CSV file
	 * @param encoding
	 *            the file encoding. Examples: "UTF-8", "UTF-16".
	 * @return a Map having the Headers as keys and a corresponding list for
	 *         each value
	 * @throws IOException
	 *             if something goes wrong while reading the file
	 */
	public Map<String, List<String>> parseCSV(final String headers,
			final String file, final String separator, final String encoding)
			throws IOException {
		LOG.info("Parsing CSVs from file: " + file + " with headers: "
				+ headers + " separator: " + separator);
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		BufferedReader reader = null;
		try {

			String[] headersArr = headers.split(",");

			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), encoding));
			String line = null;

			while ((line = reader.readLine()) != null) {
				String[] lines = line.split(separator);

				if (lines.length != headersArr.length) {
					throw new IOException("Too many or too little theaders!");
				}

				for (int i = 0; i < lines.length; i++) {
					List<String> currentHeader = result.get(headersArr[i]
							.trim());

					if (currentHeader == null) {
						currentHeader = new ArrayList<String>();
						result.put(headersArr[i].trim(), currentHeader);
					}

					currentHeader.add(lines[i].trim());
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		LOG.info("Result of parsing CSV: " + result);
		return result;
	}

	/**
	 * Converts a ResourceBundle object to a Properties object.
	 * 
	 * @param resource
	 *            Name of the bundle to be converted.
	 * @return a Properties object corresponding to the supplied bundle object
	 * 
	 */
	public Properties convertResourceBundleToProperties(
			final ResourceBundle resource) {
		Properties properties = new Properties();

		Enumeration<String> keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			properties.put(key, resource.getString(key));
		}

		return properties;
	}

	/**
	 * This method returns the content of a file into a list of strings. Each
	 * file line will correspond to an element in the list. Please be careful
	 * when using this method as it is not intended to be used with large files
	 * 
	 * @param fileName
	 *            the name of the file
	 * @param encoding
	 *            the file encoding. Examples: "UTF-8", "UTF-16".
	 * @return a list of Strings contains the lines of the file
	 * @throws IOException
	 *             if something goes wrong while reading the file
	 */
	public List<String> getFileAsList(final String fileName,
			final String encoding) throws IOException {
		LOG.info("Get file as list. file: " + fileName);
		List<String> result = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), encoding));
			String line = null;
			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		LOG.info("Returning: " + result);
		return result;
	}

	/**
	 * Writes the specific string content {@code toWrite} to the specified file
	 * {@code filePath}. If the file doesn't exists one will be created and the
	 * content will be written. If the file exists and {@code overwrite} is true
	 * the content of the file will be overwritten otherwise the content will be
	 * appended to the existing file
	 * 
	 * @param filePath
	 *            the path to the file
	 * @param toWrite
	 *            the string to be written
	 * @param overwrite
	 *            true if you want to overwrite an existing file or false
	 *            otherwise
	 * @param fileEncoding
	 *            the file encoding. Examples: "UTF-8", "UTF-16".
	 * @throws IOException
	 *             if something goes wrong while writing to the file
	 */
	public void writeToFile(final String filePath, final String toWrite,
			final boolean overwrite, final String fileEncoding)
			throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			boolean created = file.createNewFile();
			LOG.info("File successfully created: " + created);
		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, overwrite), fileEncoding));
			writer.write(toWrite);

			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Writes the specific content {@code toWrite} to the specified file
	 * {@code filePath}. If the file doesn't exists one will be created and the
	 * content will be written. If the file exists and {@code overwrite} is true
	 * the content of the file will be overwritten otherwise the content will be
	 * appended to the existing file. Each item in the list will be written on a
	 * new line
	 * 
	 * @param filePath
	 *            the path to the file
	 * @param toWrite
	 *            the string to be written
	 * @param overwrite
	 *            true if you want to overwrite an existing file or false
	 *            otherwise
	 * @param fileEncoding
	 *            the file encoding. Examples: "UTF-8", "UTF-16".
	 * @throws IOException
	 *             if something goes wrong while writing to file
	 */
	public void writeToFile(final String filePath, final List<String> toWrite,
			final boolean overwrite, final String fileEncoding)
			throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			boolean created = file.createNewFile();
			LOG.info("File successfully created: " + created);
		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, overwrite), fileEncoding));
			for (String s : toWrite) {
				writer.write(s);
				writer.newLine();
			}

			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Reads the content of the file between the specific line numbers.
	 * 
	 * @param filePath
	 *            the path to the file
	 * @param lineToStart
	 *            the line number to start with
	 * @param lineToEnd
	 *            the line number to end with
	 * @param encoding
	 *            the file encoding. Examples: "UTF-8", "UTF-16".
	 * @return a list of strings for each line between {@code LineToStart} and
	 *         {@code lineToEnd}
	 * @throws IOException
	 *             if something goes wrong while reading the file
	 */
	public List<String> readFromFile(final String filePath,
			final int lineToStart, final int lineToEnd, final String encoding)
			throws IOException {
		if (lineToStart > lineToEnd) {
			throw new IllegalArgumentException(
					"Line to start must be lower than line to end");
		}
		LOG.info("Reading from file: " + filePath);
		List<String> result = new ArrayList<String>();
		BufferedReader reader = null;
		int i = 0;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath), encoding));
			String line = reader.readLine();
			while (line != null && i >= lineToStart && i <= lineToEnd) {
				result.add(line);
				i++;
				line = reader.readLine();
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		LOG.info("Returning: " + result);
		return result;
	}

}
