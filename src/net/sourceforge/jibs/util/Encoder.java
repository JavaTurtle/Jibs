package net.sourceforge.jibs.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class Encoder {
	private static Logger logger = Logger.getLogger(Encoder.class);

	public static String encrypt(String plaintext, String algorithm) {
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance(algorithm);
			md.update(plaintext.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.warn(e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			logger.warn(e);
			return null;
		}
		String x = new BigInteger(1, md.digest()).toString(16);
		return x;
	}
	public static void main(String[] args) {
		System.out.println("alice:"+Encoder.encrypt("alicealice", "MD5"));
		System.out.println("bob:"+Encoder.encrypt("bobbob", "MD5"));		
	}
}
