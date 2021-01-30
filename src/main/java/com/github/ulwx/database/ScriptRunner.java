
package com.github.ulwx.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScriptRunner {

	private static Logger log = LoggerFactory.getLogger(ScriptRunner.class);
	private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	private static final String DEFAULT_DELIMITER = ";";
	
	private static final Pattern DELIMITER_PATTERN = Pattern
			.compile("^\\s*((--)|(//))?\\s*(//)?\\s*@DELIMITER\\s+([^\\s]+)", Pattern.CASE_INSENSITIVE);

	private final Connection connection;

	private boolean stopOnError=true;
	private boolean throwWarning=true;
	private boolean sendFullScript=true;
	private boolean removeCRs=true;
	private boolean escapeProcessing = true;

	private PrintWriter logWriter = null;
	private PrintWriter errorLogWriter =  null;
	private StringWriter resultWriter=new StringWriter(); 
	private PrintWriter resultPrintWriter = new PrintWriter(resultWriter);

	private String delimiter = DEFAULT_DELIMITER;
	private boolean fullLineDelimiter;

	public ScriptRunner(Connection connection) {
		this.connection = connection;
	}


	public void setStopOnError(boolean stopOnError) {
		this.stopOnError = stopOnError;
	}

	public void setThrowWarning(boolean throwWarning) {
		this.throwWarning = throwWarning;
	}


	public void setSendFullScript(boolean sendFullScript) {
		this.sendFullScript = sendFullScript;
	}

	public void setRemoveCRs(boolean removeCRs) {
		this.removeCRs = removeCRs;
	}

	/**
	 * @since 3.1.1
	 */
	public void setEscapeProcessing(boolean escapeProcessing) {
		this.escapeProcessing = escapeProcessing;
	}

	public void setLogWriter(PrintWriter logWriter) {
		this.logWriter = logWriter;
	}

	public void setErrorLogWriter(PrintWriter errorLogWriter) {
		this.errorLogWriter = errorLogWriter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public void setFullLineDelimiter(boolean fullLineDelimiter) {
		this.fullLineDelimiter = fullLineDelimiter;
	}

	public String runScript(Reader reader) {
		try {
			if (sendFullScript) {
				return executeFullScript(reader);
			} else {
				return executeLineByLine(reader);
			}
		} finally {
		}
	}

	private String executeFullScript(Reader reader) {
		StringBuilder script = new StringBuilder();
		BufferedReader lineReader=null;
		try {
			lineReader = new BufferedReader(reader);
			String line;
			while ((line = lineReader.readLine()) != null) {
				script.append(line);
				script.append(LINE_SEPARATOR);
			}
			String command = script.toString();

			executeStatement(command);
			return this.resultWriter.toString();
		} catch (Exception e) {
			String message = "Error executing: " + script + ". Cause: " + e;
			log.error(message,e);
			//printlnError(message);
			throw new RuntimeSqlException(e);
		}finally {
		}
	}

	private String executeLineByLine(Reader reader) {
		StringBuilder command = new StringBuilder();
		try {
			BufferedReader lineReader = new BufferedReader(reader);
			String line;
			while ((line = lineReader.readLine()) != null) {
				handleLine(command, line);
			}
			checkForMissingLineTerminator(command);
			return this.resultWriter.toString();
		} catch (Exception e) {
			String message = "Error executing: " + command + ".  Cause: " + e;
			log.error(message,e);
			//printlnError(message);
			throw new RuntimeSqlException(message, e);
		}
	}

	private void checkForMissingLineTerminator(StringBuilder command) {
		if (command != null && command.toString().trim().length() > 0) {
			throw new RuntimeSqlException("Line missing end-of-line terminator (" + delimiter + ") => " + command);
		}
	}

	private void handleLine(StringBuilder command, String line) throws SQLException {
		String trimmedLine = line.trim();
		if (lineIsComment(trimmedLine)) {
			Matcher matcher = DELIMITER_PATTERN.matcher(trimmedLine);
			if (matcher.find()) {
				delimiter = matcher.group(5);
			}
			//println(trimmedLine);
		} else if (commandReadyToExecute(trimmedLine)) {
			command.append(line.substring(0, line.lastIndexOf(delimiter)));
			command.append(LINE_SEPARATOR);
			//println(command);
			executeStatement(command.toString());
			command.setLength(0);
		} else if (trimmedLine.length() > 0) {
			command.append(line);
			command.append(LINE_SEPARATOR);
		}
	}

	private boolean lineIsComment(String trimmedLine) {
		return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
	}

	private boolean commandReadyToExecute(String trimmedLine) {
		// issue #561 remove anything after the delimiter
		return !fullLineDelimiter && trimmedLine.contains(delimiter)
				|| fullLineDelimiter && trimmedLine.equals(delimiter);
	}

	private void executeStatement(String command) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			statement.setEscapeProcessing(escapeProcessing);
			String sql = command;
			if (removeCRs) {
				sql = sql.replaceAll("\r\n", "\n");
			}
			try {
				boolean hasResults = statement.execute(sql);
				while (!(!hasResults && statement.getUpdateCount() == -1)) {
					checkWarnings(statement);
					printResults(statement, hasResults);
					hasResults = statement.getMoreResults();
				}
			} catch (SQLWarning e) {
				throw e;
			} catch (SQLException e) {
				if (stopOnError) {
					throw e;
				} else {
					String message = "Error executing: " + command + ".  Cause: " + e;
					//printlnError(message);
				}
			}
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				// Ignore to workaround a bug in some connection pools
				// (Does anyone know the details of the bug?)
			}
		}
	}

	private void checkWarnings(Statement statement) throws SQLException {
		if (!throwWarning) {
			return;
		}
		// In Oracle, CREATE PROCEDURE, FUNCTION, etc. returns warning
		// instead of throwing exception if there is compilation error.
		SQLWarning warning = statement.getWarnings();
		if (warning != null) {
			throw warning;
		}
	}

	private void printResults(Statement statement, boolean hasResults) {
		if (!hasResults) {
			return ;
		}
		
		try (ResultSet rs = statement.getResultSet()) {
			if(rs==null) {
				return;
			}
			ResultSetMetaData md = rs.getMetaData();
			int cols = md.getColumnCount();
			for (int i = 0; i < cols; i++) {
				String name = md.getColumnLabel(i + 1);
				resultPrint(name + "\t");
			}
			resultPrintln("");
			while (rs.next()) {
				for (int i = 0; i < cols; i++) {
					String value = rs.getString(i + 1);
					resultPrint(value + "\t");
				}
				resultPrintln("");
			}
		} catch (SQLException e) {
			
			throw new RuntimeSqlException("Error printing results: " + e, e);
		}
	}

	private void resultPrint(Object o) {
		if (resultPrintWriter != null) {
			resultPrintWriter.print(o);
			resultPrintWriter.flush();
		}else {
		}
	}
	private void resultPrintln(Object o) {
		if (resultPrintWriter != null) {
			resultPrintWriter.println(o);
			resultPrintWriter.flush();
		}else {
		}
	}


}
