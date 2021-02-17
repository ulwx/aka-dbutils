package com.github.ulwx.aka.dbutils.tool.support;

import java.rmi.server.UID;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class RandomUtils {

	public static Integer[] getRandomArray(int n) {
		if (n <= 0) {
			return null;
		}
		Set<Integer> r = new LinkedHashSet<Integer>();
		Random random = new Random();
		int count = n * 10;
		int i = 0;
		while (i < count && r.size() < n) {
			int f = random.nextInt(n);
			// System.out.println(f);
			r.add(f);
			i++;
		}
		if (i >= count && r.size() < n) {
			return null;
		}
		// System.out.println("i=="+i);
		Integer[] t = new Integer[0];
		return r.toArray(t);
	}

	public static String genUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}

	public static String genUID() {
		UID uid = new UID();
		return uid.toString();
	}

	public static String genRandomString(int strLen) {
		Random random = new Random();
		byte[] bytes = new byte[strLen];
		random.nextBytes(bytes);
		try {
			return Base64.encodeBytes(bytes).substring(0, strLen);

		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return "";
		// return RandomStringUtils.random(strLen);
	}

	public static String getRandomNumberString(int strLen) {
		Random random = new Random();
		String ss = "0123456789";
		String s = "";
		for (int i = 0; i < strLen; i++) {
			int n = random.nextInt(ss.length());
			char r = ss.charAt(n);
			s = s + r;
		}

		return s;
	}

	public static String getRandomAlphaString(int strLen) {
		Random random = new Random();
		String ss = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String s = "";
		for (int i = 0; i < strLen; i++) {
			int n = random.nextInt(ss.length());
			char r = ss.charAt(n);
			s = s + r;
		}
		return s;
	}


	public static String getRandomMixString(int strLen) {
		Random random = new Random();
		String ss = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String s = "";
		for (int i = 0; i < strLen; i++) {
			int n = random.nextInt(ss.length());
			char r = ss.charAt(n);
			s = s + r;
		}
		return s;
	}

	public static boolean nextBoolean() {
		return new Random().nextBoolean();
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed float value between
	 * 0.0 and 1.0 from the Math.random() sequence.
	 * 
	 * @return
	 */
	public static double nextDouble() {
		return new Random().nextDouble();
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed float value between
	 * 0.0 and 1.0 from the Math.random() sequence.
	 * 
	 * @return
	 */
	public static float nextFloat() {
		return new Random().nextFloat();
	}

	public static int nextInt() {
		return new Random().nextInt();
	}

	/**
	 * Returns a pseudorandom, uniformly distributed int value between 0
	 * (inclusive) and the specified value (exclusive), from the Math.random()
	 * sequence.
	 * 
	 * @param n
	 * @return
	 */
	public static int nextInt(int n) {
		return new Random().nextInt(n);
	}

	public static long nextLong() {
		return new Random().nextLong();
	}

	public static void main(String[] args) throws Exception {
		System.out.println(genUUID());
		System.out.println(genUID());
		// System.out.println(genRandomString(3456709).length());
		// System.out.println(genNameUUID("5486243a-73ec-42bd-b6e1"));
		System.out.println(RandomUtils.genRandomString(20));
		System.out.println(getRandomNumberString(20));
		System.out.println(getRandomAlphaString(10));
		System.out.println(getRandomMixString(100));
		// System.out.println((char)(int)97);
		System.out.println(CTime.formatDate().substring(8)
				+ RandomUtils.getRandomNumberString(4));
		System.out.println(CTime.formatWholeDate());
		System.out.println(new UID().toString());

	}
}
