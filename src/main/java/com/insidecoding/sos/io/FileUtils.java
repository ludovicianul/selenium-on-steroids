package com.insidecoding.sos.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

import com.insidecoding.sos.junit.AbstractSoSBase;

/**
 * This class offers helper methods to for I/O operations. <br/>
 * It allows you to read from text files, properties files and Microsoft Excel
 * files. <br/>
 * This is very helpful for data driven testing
 * 
 * @author ludovicianul
 * 
 */
public final class FileUtils {
	private Map<String, Workbook> workbooks = new HashMap<String, Workbook>();
	private List<InputStream> FISs = new ArrayList<InputStream>();
	private Map<String, ResourceBundle> bundles = new HashMap<String, ResourceBundle>();

	private static final Logger LOG = Logger.getLogger(FileUtils.class);

	/**
	 * This will call {@code buildupPropertiesBundles("./src/test/resources")}
	 */
	public FileUtils() {
		buildupPropertiesBundles(new File("./src/test/resources"));
	}

	/**
	 * Loads all the properties files from this location
	 * 
	 * @param resourcesLocation
	 */
	public FileUtils(String resourcesLocation) throws IOException {
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
	 * Loads all properties files into a bundle cache
	 * 
	 * @param file
	 *            - the folder where the properties files can be found
	 */
	private void buildupPropertiesBundles(File file) {
		File[] files = file.listFiles();

		for (File f : files) {
			if (f.getName().endsWith("properties")) {
				ResourceBundle bundle = ResourceBundle.getBundle(f.getName()
						.substring(0, f.getName().indexOf("properties") - 1));
				bundles.put(f.getName(), bundle);
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
	public void loadPropertiesBundle(String bundleName, Locale locale) {
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
	public void loadPropertiesBundle(String bundleName) {
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
		bundles.put(bundleName, bundle);
	}

	/**
	 * Loads the properties from a file specified as a parameter.
	 * 
	 * @param propertiesFile
	 *            Path to the properties file.
	 */
	public void loadPropertiesFromFile(String propertiesFile)
			throws IOException {
		File file = new File(propertiesFile);

		String bundleName = file.getPath().substring(0,
				file.getPath().indexOf("properties") - 1);
		FileInputStream inputStream = null;

		try {
			inputStream = new FileInputStream(file);
			ResourceBundle bundle = new PropertyResourceBundle(inputStream);
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
	 */
	public ResourceBundle getBundle(String bundleName) {
		return bundles.get(bundleName);
	}

	/**
	 * Gets the value from the resource bundles corresponding to the supplied
	 * key. <br/>
	 * If the property exists in multiple bundles only the first occurrence will
	 * be returned
	 * 
	 * @param key
	 *            - the key to search for
	 * @return - {@code null} if the property is not found; {@code Boolean.TRUE}
	 *         if the property is true or {@code Boolean.FALSE} otherwise
	 */
	public Boolean getPropertyAsBoolean(String key) {
		for (ResourceBundle bundle : bundles.values()) {

			if (bundle.getString(key) != null) {
				return Boolean.valueOf(bundle.getString(key));
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
	 *            - the name of the bundle to search in
	 * @param key
	 *            - the key to search for
	 * @return - {@code null} if the property is not found; {@code Boolean.TRUE}
	 *         if the property is true or {@code Boolean.FALSE} otherwise
	 */
	public Boolean getPropertyAsBoolean(String bundleName, String key) {
		ResourceBundle bundle = bundles.get(bundleName);

		if (bundle.getString(key) != null) {
			return Boolean.valueOf(bundle.getString(key));
		}

		return Boolean.FALSE;
	}

	/**
	 * Gets the value from the resource bundles corresponding to the supplied
	 * key. <br/>
	 * If the property exists in multiple bundles only the first occurrence will
	 * be returned
	 * 
	 * @param key
	 *            - the key to search for
	 * @return - {@code null} if the property is not found or the corresponding
	 *         value otherwise
	 */
	public String getPropertyAsString(String key) {
		for (ResourceBundle bundle : bundles.values()) {

			if (bundle.getString(key) != null) {
				return bundle.getString(key);
			}

		}
		return null;
	}

	/**
	 * Gets the value from the resource bundles corresponding to the supplied
	 * key. <br/>
	 * 
	 * 
	 * @param bunldeName
	 *            - the name of the bundle to search in
	 * @param key
	 *            - the key to search for
	 * @return - {@code null} if the property is not found or the corresponding
	 *         value otherwise
	 */
	public String getPropertyAsString(String bunldeName, String key) {
		ResourceBundle bundle = bundles.get(bunldeName);

		if (bundle.getString(key) != null) {
			return bundle.getString(key);
		}

		return null;
	}

	/**
	 * Gets the value as int from the resource bundles corresponding to the
	 * supplied key. <br/>
	 * 
	 * If the property exists in multiple bundles only the first occurrence will
	 * be returned
	 * 
	 * @param key
	 *            - the key to search for
	 * @return - {@code -1} if the property is not found or the value is not a
	 *         number; the corresponding value otherwise
	 */
	public int getPropertyAsInteger(String key) {
		for (ResourceBundle bundle : bundles.values()) {
			try {
				if (bundle.getString(key) != null) {
					return Integer.parseInt(bundle.getString(key));
				}
			} catch (NumberFormatException e) {
				return -1;
			}
		}
		return -1;
	}

	/**
	 * Gets the value as int from the resource bundles corresponding to the
	 * supplied key. <br/>
	 * 
	 * 
	 * @param bundleName
	 *            - the name of the bunlde to search in
	 * @param key
	 *            - the key to search for
	 * @return - {@code -1} if the property is not found or the value is not a
	 *         number; the corresponding value otherwise
	 */
	public int getPropertyAsInteger(String bundleName, String key) {
		ResourceBundle bundle = bundles.get(bundleName);
		try {
			if (bundle.getString(key) != null) {
				return Integer.parseInt(bundle.getString(key));
			}
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
	 *            - the key to search for
	 * @return - {@code -1} if the property is not found or the value is not a
	 *         number; the corresponding value otherwise
	 */
	public double getPropertyAsDouble(String key) {
		for (ResourceBundle bundle : bundles.values()) {
			try {
				if (bundle.getString(key) != null) {
					return Double.parseDouble(bundle.getString(key));
				}
			} catch (NumberFormatException e) {
				return -1;
			}
		}
		return -1;
	}

	/**
	 * Gets the value as double from the resource bundles corresponding to the
	 * supplied key. <br/>
	 * 
	 * 
	 * @param bundleName
	 *            - the name of the bundle to search in
	 * @param key
	 *            - the key to search for
	 * @return - {@code -1} if the property is not found or the value is not a
	 *         number; the corresponding value otherwise
	 */
	public double getPropertyAsDouble(String bundleName, String key) {
		ResourceBundle bundle = bundles.get(bundleName);
		try {
			if (bundle.getString(key) != null) {
				return Double.parseDouble(bundle.getString(key));
			}
		} catch (NumberFormatException e) {
			return -1;
		}
		return -1;
	}

	/**
	 * Reads the value of a cell from an Excel workbook corresponding to the
	 * supplied coordinates. <br/>
	 * 
	 * 
	 * @param fileName
	 *            - the name of the
	 * @param sheetName
	 *            - the name of the sheet within the workbook
	 * @param rowNumber
	 *            - the row number; starts from 1
	 * @param cellNumber
	 *            - the cell number; starts from 1
	 * @return the cell value from the excel file
	 * @throws IOException
	 */
	public String readFromExcel(String fileName, String sheetName,
			int rowNumber, int cellNumber) throws IOException {
		Workbook workbook = getWorkbook(fileName);
		Sheet payments = workbook.getSheet(sheetName);

		Row row = payments.getRow(rowNumber);
		Cell cell = row.getCell(cellNumber);

		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_NUMERIC:
			return String.valueOf(cell.getNumericCellValue());
		default:
			return cell.getStringCellValue();
		}

	}

	/**
	 * Tries to load the workbook from the current cache. If it doesn't find it
	 * it will load it from the disk
	 * 
	 * @param fileName
	 *            - the name of file
	 * @return - the workbook corresponding to this file
	 * @throws IOException
	 */
	private Workbook getWorkbook(String fileName) throws IOException {
		if (workbooks.get(fileName) == null) {
			FileInputStream fis = new FileInputStream(new File(fileName));
			FISs.add(fis);
			Workbook workbook = null;
			try {
				workbook = new HSSFWorkbook(fis);
			} catch (OfficeXmlFileException e) {
				fis.close();
				fis = new FileInputStream(new File(fileName));
				FISs.add(fis);
				workbook = new XSSFWorkbook(fis);
			}
			workbooks.put(fileName, workbook);

		}

		return workbooks.get(fileName);
	}

	/**
	 * Gets the number of rows populated within the supplied file and sheet name
	 * 
	 * @param fileName
	 *            - the name of the file
	 * @param sheetName
	 *            - the sheet name
	 * @return - the number of rows
	 * @throws IOException
	 */
	public int getNumberOfRows(String fileName, String sheetName)
			throws IOException {

		Workbook workbook = getWorkbook(fileName);
		Sheet payments = workbook.getSheet(sheetName);
		return payments.getPhysicalNumberOfRows();

	}

	/**
	 * You MUST call this method after you finish running your tests. <br/>
	 * Usually you can do this in a @After method (tearDown()). <br/>
	 * If you just extend the provided {@link AbstractSoSBase} you won't need to
	 * worry about it
	 */
	public void releaseResources() {
		for (InputStream str : FISs) {
			try {
				str.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Returns the contents of a file into a String object. <br/>
	 * Please be careful when using this with large files
	 * 
	 * @param file
	 *            - the file
	 * @return - a String object with the contents of the file
	 * @throws IOException
	 */
	public String getFileContentsAsString(File file) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));

			StringBuilder builder = new StringBuilder();

			String line = null;
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}

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
	 *            - the name of the file
	 * @return - a String object with the contents of the file
	 * @throws IOException
	 */
	public String getFileContentsAsString(String fileName) throws IOException {
		return this.getFileContentsAsString(new File(fileName));
	}

	/**
	 * Parses a CSV file based on the header received
	 * 
	 * @param headers
	 *            a comma separated list of headers
	 * @param separator
	 *            the separator used in the CSV file
	 * @return - a Map having the Headers as keys and a corresponding list for
	 *         each value
	 */
	public Map<String, List<String>> parseCSV(String headers, String file,
			String separator) throws IOException {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		BufferedReader reader = null;
		try {

			String[] headersArr = headers.split(",");

			reader = new BufferedReader(new FileReader(new File(file)));
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
	public Properties convertResourceBundleToProperties(ResourceBundle resource) {
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
	 *            - the name of the file
	 * @return a list of Strings contains the lines of the file
	 * @throws IOException
	 */
	public List<String> getFileAsList(String fileName) throws IOException {
		List<String> result = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(fileName)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}

}
