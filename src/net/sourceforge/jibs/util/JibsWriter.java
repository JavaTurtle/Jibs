package net.sourceforge.jibs.util;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class JibsWriter {
	private static Logger logger = Logger.getLogger(JibsWriter.class);
	private OutputStream outputStream;
	private String lineSeparator;

	public JibsWriter(OutputStream outputStream) {
		this.outputStream = outputStream;
		lineSeparator = System.getProperty("line.separator");
	}

	public void println(String msg) {
		try {
			outputStream.write(msg.getBytes());
			outputStream.write(lineSeparator.getBytes());
		} catch (IOException e) {
			logger.warn(e);
		}
	}

	public void print(String msg) {
		try {
			outputStream.write(msg.getBytes());
		} catch (IOException e) {
			logger.warn(e);
		}
	}

	public void flush() {
		try {
			outputStream.flush();
		} catch (IOException e) {
			logger.warn(e);
		}
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

}