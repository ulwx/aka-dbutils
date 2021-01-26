package com.github.ulwx.tool.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Arrays;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;


public class EncryptUtil {

	static Logger log = LoggerFactory.getLogger(EncryptUtil.class);

	private static final int KEY_SIZE = 1024;
	private static final String MD5_ALGORITHM = "md5";
	private static final String DES_ALGORITHM = "des";
	private static final String RSA_ALGORITHM = "rsa";
	private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	private static MessageDigest md5;
	private static Encoder encoder;
	private static Decoder decoder;
	private static SecureRandom random;
	private static KeyPair keyPair;

	private EncryptUtil() {
	}

	static {
		try {
			md5 = MessageDigest.getInstance(MD5_ALGORITHM);

			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
			keyPairGenerator.initialize(KEY_SIZE);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			log.error(e + "", e);
		}
		random = new SecureRandom();
	}

	/**
	 * 功能简述: 使用md5进行单向加密.
	 */
	public static String encryptMD5(String plainText) {
		byte[] cipherData = md5.digest(plainText.getBytes());
		StringBuilder builder = new StringBuilder();
		for (byte cipher : cipherData) {
			String toHexStr = Integer.toHexString(cipher & 0xff);
			builder.append(toHexStr.length() == 1 ? "0" + toHexStr : toHexStr);
		}
		return builder.toString();
	}

	/**
	 * 功能简述: 使用BASE64进行加密.
	 * 
	 * @param plainData
	 *            明文数据
	 * @return 加密之后的文本内容
	 */
	public static String encryptBASE64(byte[] plainData) {
		Encoder encoder = java.util.Base64.getEncoder();
		String encode = encoder.encodeToString(plainData);
		return encode;
	}

	/**
	 * 功能简述: 使用BASE64进行解密.
	 * 
	 * @param cipherText
	 *            密文文本
	 * @return 解密之后的数据
	 */
	public static byte[] decryptBASE64(String cipherText) {
		byte[] plainData = null;
		try {
			Decoder decoder = java.util.Base64.getDecoder();
			byte[] buffer = decoder.decode(cipherText);
			return buffer;
		} catch (Exception e) {
			// Exception handler
			log.error(e + "", e);
		}
		return plainData;
	}

	/**
	 * 功能简述: 使用DES算法进行加密.
	 * 
	 * @param plainData
	 *            明文数据
	 * @param key
	 *            加密密钥
	 * @return
	 */
	public static byte[] encryptDES(byte[] plainData, String key) {
		return processCipher(plainData, createSecretKey(key), Cipher.ENCRYPT_MODE, DES_ALGORITHM);
	}

	/**
	 * 功能简述: 使用DES算法进行解密.
	 * 
	 * @param cipherData
	 *            密文数据
	 * @param key
	 *            解密密钥
	 * @return
	 */
	public static byte[] decryptDES(byte[] cipherData, String key) {
		return processCipher(cipherData, createSecretKey(key), Cipher.DECRYPT_MODE, DES_ALGORITHM);
	}

	public static String encryptDES(String plainData, String key) {
		try {
			return encryptBASE64(encryptDES(plainData.getBytes("utf-8"), key));
		} catch (UnsupportedEncodingException e) {
			log.error(e + "", e);
		}
		return "";
	}

	public static String decryptDES(String cipherData, String key) {
		try {
			byte[] data = decryptDES(decryptBASE64(cipherData), key);
			return new String(data, "utf-8");
		} catch (Exception e) {
			log.error(e + "", e);
		}
		return "";
	}

	/**
	 * 功能简述: 根据key创建密钥SecretKey.
	 * 
	 * @param key
	 * @return
	 */
	private static SecretKey createSecretKey(String key) {
		SecretKey secretKey = null;
		try {
			DESKeySpec keySpec = new DESKeySpec(key.getBytes("utf-8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
			secretKey = keyFactory.generateSecret(keySpec);
		} catch (Exception e) {
			// Exception handler
			log.error(e + "", e);
		}
		return secretKey;
	}

	/**
	 * 功能简述: 加密/解密处理流程.
	 * 
	 * @param processData
	 *            待处理的数据
	 * @param key
	 *            提供的密钥
	 * @param opsMode
	 *            工作模式
	 * @param algorithm
	 *            使用的算法
	 * @return
	 */
	private static byte[] processCipher(byte[] processData, Key key, int opsMode, String algorithm) {
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(opsMode, key, random);
			return cipher.doFinal(processData);
		} catch (Exception e) {
			// Exception handler
			log.error(e + "", e);
		}
		return null;
	}

	/**
	 * 功能简述: 创建私钥，用于RSA非对称加密.
	 * 
	 * @return
	 */
	public static PrivateKey createPrivateKey() {
		return keyPair.getPrivate();
	}

	/**
	 * 功能简述: 创建公钥，用于RSA非对称加密.
	 * 
	 * @return
	 */
	public static PublicKey createPublicKey() {
		return keyPair.getPublic();
	}

	/**
	 * 功能简述: 使用RSA算法加密.
	 * 
	 * @param plainData
	 *            明文数据
	 * @param key
	 *            密钥
	 * @return
	 */
	public static byte[] encryptRSA(byte[] plainData, Key key) {
		return processCipher(plainData, key, Cipher.ENCRYPT_MODE, RSA_ALGORITHM);
	}

	/**
	 * 功能简述: 使用RSA算法解密.
	 * 
	 * @param cipherData
	 *            密文数据
	 * @param key
	 *            密钥
	 * @return
	 */
	public static byte[] decryptRSA(byte[] cipherData, Key key) {
		return processCipher(cipherData, key, Cipher.DECRYPT_MODE, RSA_ALGORITHM);
	}

	/**
	 * 功能简述: 使用私钥对加密数据创建数字签名.
	 * 
	 * @param cipherData
	 *            已经加密过的数据
	 * @param privateKey
	 *            私钥
	 * @return
	 */
	public static byte[] createSignature(byte[] cipherData, PrivateKey privateKey) {
		try {
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
			signature.initSign(privateKey);
			signature.update(cipherData);
			return signature.sign();
		} catch (Exception e) {
			// Exception handler
			log.error(e + "", e);
		}
		return null;
	}

	/**
	 * 功能简述: 使用公钥对数字签名进行验证.
	 * 
	 * @param signData
	 *            数字签名
	 * @param publicKey
	 *            公钥
	 * @return
	 */
	public static boolean verifySignature(byte[] cipherData, byte[] signData, PublicKey publicKey) {
		try {
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
			signature.initVerify(publicKey);
			signature.update(cipherData);
			return signature.verify(signData);
		} catch (Exception e) {
			// Exception handler
			log.error(e + "", e);
		}
		return false;
	}

	/**
	 * SELECT TO_BASE64(AES_ENCRYPT('123459888999988','key')) SELECT
	 * AES_DECRYPT(FROM_BASE64("jbjmPimhQZHWZrM/oN+6mQ=="),'key')
	 * 
	 * @param s
	 * @return
	 */
	public static String aesEncrypt(String s,String KEY) {
		try {

			byte[] keyBytes = Arrays.copyOf(KEY.getBytes("ASCII"), 16);

			SecretKey key = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);

			byte[] cleartext = s.getBytes("utf-8");
			byte[] ciphertextBytes = cipher.doFinal(cleartext);
			String base64Str = Base64.encodeBytes(ciphertextBytes);
			return base64Str;

		} catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	/**
	 * SELECT TO_BASE64(AES_ENCRYPT('123459888999988','key')) SELECT
	 * AES_DECRYPT(FROM_BASE64("jbjmPimhQZHWZrM/oN+6mQ=="),'key')
	 * 
	 * @param str
	 * @return
	 */
	public static String aesUnEncrypt(String str,String KEY) {

		try {

			byte[] keyBytes = Arrays.copyOf(KEY.getBytes("ASCII"), 16);

			SecretKey key = new SecretKeySpec(keyBytes, "AES");
			Cipher decipher = Cipher.getInstance("AES");

			decipher.init(Cipher.DECRYPT_MODE, key);

			byte[] content = Base64.decode(str);
			byte[] ciphertextBytes = decipher.doFinal(content);

			return new String(ciphertextBytes, "utf-8");

		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	public static void main(String[] args) {
		String data = "1";
		String key = "123#$5@#G456";
		String ret = encryptDES(data, key);
		System.out.println(decryptDES(ret, key));

	}
}
