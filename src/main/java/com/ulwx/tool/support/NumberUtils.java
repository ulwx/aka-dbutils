/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ulwx.tool.support;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Miscellaneous utility methods for number conversion and parsing. Mainly for
 * internal use within the framework; consider Jakarta's Commons Lang for a more
 * comprehensive suite of string utilities.
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 1.1.2
 */
public  class NumberUtils {


	/**
	 * Convert the given number into an instance of the given target class.
	 * 
	 * @param number
	 *            the number to convert
	 * @param targetClass
	 *            the target class to convert to
	 * @return the converted number
	 * @throws IllegalArgumentException
	 *             if the target class is not supported (i.e. not a standard
	 *             Number subclass as included in the JDK)
	 * @see Byte
	 * @see Short
	 * @see Integer
	 * @see Long
	 * @see BigInteger
	 * @see Float
	 * @see Double
	 * @see BigDecimal
	 */
	public static Number convertNumberToTargetClass(Number number,
			Class targetClass) throws IllegalArgumentException {

		Assert.notNull(number, "Number must not be null");
		Assert.notNull(targetClass, "Target class must not be null");

		if (targetClass.isInstance(number)) {
			return number;
		} else if (targetClass.equals(Byte.class) ||targetClass.equals(byte.class)) {
			long value = number.longValue();
			if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
				raiseOverflowException(number, targetClass);
			}
			return new Byte(number.byteValue());
		} else if (targetClass.equals(Short.class) ||targetClass.equals(short.class)) {
			long value = number.longValue();
			if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
				raiseOverflowException(number, targetClass);
			}
			return new Short(number.shortValue());
		} else if (targetClass.equals(Integer.class) ||targetClass.equals(int.class)) {
			long value = number.longValue();
			if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
				raiseOverflowException(number, targetClass);
			}
			return new Integer(number.intValue());
		} else if (targetClass.equals(Long.class) ||targetClass.equals(long.class)) {
			return new Long(number.longValue());
		} else if (targetClass.equals(Float.class) ||targetClass.equals(float.class)) {
			return new Float(number.floatValue());
		} else if (targetClass.equals(Double.class) ||targetClass.equals(double.class)) {
			return new Double(number.doubleValue());
		} else if (targetClass.equals(BigInteger.class) ) {
			return BigInteger.valueOf(number.longValue());
		} else if (targetClass.equals(BigDecimal.class)) {
			return new BigDecimal(number.toString());
		} else {
			throw new IllegalArgumentException("Could not convert number ["
					+ number + "] of type [" + number.getClass().getName()
					+ "] to unknown target class [" + targetClass.getName()
					+ "]");
		}
	}

	/**
	 * Raise an overflow exception for the given number and target class.
	 * 
	 * @param number
	 *            the number we tried to convert
	 * @param targetClass
	 *            the target class we tried to convert to
	 */
	private static void raiseOverflowException(Number number, Class targetClass) {
		throw new IllegalArgumentException("Could not convert number ["
				+ number + "] of type [" + number.getClass().getName()
				+ "] to target class [" + targetClass.getName() + "]: overflow");
	}

	/**
	 * Parse the given text into a number instance of the given target class,
	 * using the corresponding default <code>decode</code> methods. Trims the
	 * input <code>String</code> before attempting to parse the number. Supports
	 * numbers in hex format (with leading 0x) and in octal format (with leading
	 * 0).
	 * 
	 * @param text
	 *            the text to convert
	 * @param targetClass
	 *            the target class to parse into
	 * @return the parsed number
	 * @throws IllegalArgumentException
	 *             if the target class is not supported (i.e. not a standard
	 *             Number subclass as included in the JDK)
	 * @see Byte#decode
	 * @see Short#decode
	 * @see Integer#decode
	 * @see Long#decode
	 * @see #decodeBigInteger(String)
	 * @see Float#valueOf
	 * @see Double#valueOf
	 * @see BigDecimal#BigDecimal(String)
	 */
	public static Number parseNumber(String text, Class targetClass) {
		Assert.notNull(text, "Text must not be null");
		Assert.notNull(targetClass, "Target class must not be null");

		String trimmed = text.trim();
		String signchar="";
		if(trimmed.charAt(0)=='+'||trimmed.charAt(0)=='-'){
			signchar=trimmed.charAt(0)+"";
			trimmed=trimmed.substring(1);
			
		}
		if(trimmed.startsWith("0x")||trimmed.startsWith("0X")||trimmed.startsWith("#")){
			//
		}else{
			if(StringUtils.hasText(trimmed) ){
				if(!trimmed.equals("0")){
					trimmed=StringUtils.trimLeadingString(trimmed, "0");
					if(trimmed.equals("")){
						trimmed="0";
					}
				}else{
					trimmed="0";
				}
			}else{
				return null;
			}
			
		}
		
		trimmed=signchar+trimmed;

		if (targetClass.equals(Byte.class) || targetClass==byte.class) {
			return Byte.decode(trimmed);
		} else if (targetClass.equals(Short.class)|| targetClass==short.class) {
			return Short.decode(trimmed);
		} else if (targetClass.equals(Integer.class)|| targetClass==int.class) {
			
			return Integer.decode(trimmed);
		} else if (targetClass.equals(Long.class)|| targetClass==long.class) {
			return Long.decode(trimmed);
		} else if (targetClass.equals(BigInteger.class)) {
			return decodeBigInteger(trimmed);
		} else if (targetClass.equals(Float.class)|| targetClass==float.class) {
			return Float.valueOf(trimmed);
		} else if (targetClass.equals(Double.class)|| targetClass==double.class) {
			return Double.valueOf(trimmed);
		} else if (targetClass.equals(BigDecimal.class)
				|| targetClass.equals(Number.class)) {
			return new BigDecimal(trimmed);
		} else {
			return null;
		}
	}

	/**
	 * Parse the given text into a number instance of the given target class,
	 * using the given NumberFormat. Trims the input <code>String</code> before
	 * attempting to parse the number.
	 * 
	 * @param text
	 *            the text to convert
	 * @param targetClass
	 *            the target class to parse into
	 * @param numberFormat
	 *            the NumberFormat to use for parsing (if <code>null</code>,
	 *            this method falls back to
	 *            <code>parseNumber(String, Class)</code>)
	 * @return the parsed number
	 * @throws IllegalArgumentException
	 *             if the target class is not supported (i.e. not a standard
	 *             Number subclass as included in the JDK)
	 * @see NumberFormat#parse
	 * @see #convertNumberToTargetClass
	 * @see #parseNumber(String, Class)
	 */
	public static Number parseNumber(String text, Class targetClass,
			NumberFormat numberFormat) {
		if (numberFormat != null) {
			Assert.notNull(text, "Text must not be null");
			Assert.notNull(targetClass, "Target class must not be null");
			try {
				Number number = numberFormat.parse(text.trim());
				return convertNumberToTargetClass(number, targetClass);
			} catch (ParseException ex) {
				throw new IllegalArgumentException(ex.getMessage());
			}
		} else {
			return parseNumber(text, targetClass);
		}
	}

	public static long parseLong(String value) {
		if (!StringUtils.hasText(value)) {
			return 0;
		}
		NumberFormat df = NumberFormat.getInstance();
		long result;
		try {
			result = df.parse(value).longValue();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			result = 0;
		}
		return result;
	}

	/**
	 * Decode a {@link BigInteger} from a {@link String} value.
	 * Supports decimal, hex and octal notation.
	 * 
	 * @see BigInteger#BigInteger(String, int)
	 */
	private static BigInteger decodeBigInteger(String value) {
		int radix = 10;
		int index = 0;
		boolean negative = false;

		// Handle minus sign, if present.
		if (value.startsWith("-")) {
			negative = true;
			index++;
		}

		// Handle radix specifier, if present.
		if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
			index += 2;
			radix = 16;
		} else if (value.startsWith("#", index)) {
			index++;
			radix = 16;
		} else if (value.startsWith("0", index) && value.length() > 1 + index) {
			index++;
			radix = 8;
		}

		BigInteger result = new BigInteger(value.substring(index), radix);
		return (negative ? result.negate() : result);
	}

	public static int parseInt(String value) {
		if (!StringUtils.hasText(value)) {
			return 0;
		}
		NumberFormat df = NumberFormat.getInstance();
		int result;
		try {
			result = df.parse(value).intValue();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			result = 0;
		}
		return result;
	}

	public static float parseFloat(String value) {
		if (!StringUtils.hasText(value)) {
			return 0;
		}
		NumberFormat df = NumberFormat.getInstance();
		float result;
		try {
			result = df.parse(value).floatValue();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			result = 0;
		}
		return result;
	}

	public static double parseDouble(String value) {
		if (!StringUtils.hasText(value)) {
			return 0;
		}
		NumberFormat df = NumberFormat.getInstance();
		double result;
		try {
			result = df.parse(value).doubleValue();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			result = 0;
		}
		return result;
	}

	public static short parseShort(String value) {
		if (!StringUtils.hasText(value)) {
			return 0;
		}
		NumberFormat df = NumberFormat.getInstance();
		short result;
		try {
			result = df.parse(value).shortValue();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			result = 0;
		}
		return result;
	}

	/**
	 * 将给定的数值格式为百分比表示
	 * 
	 * @param d
	 * @return
	 */
	public static String formatToPercent(double d) {
		if (d == 0)
			return String.valueOf(d);
		d = d * 100;
		return truncate(String.valueOf(d), 2) + "%";
	}

	/**
	 * 将给定数字截取到小数点后几位
	 * 
	 * @param d
	 * @param accuracy
	 *            精度 ，保留小数点后的位数
	 * @return
	 */
	public static String truncate(String d, int accuracy) {
		if (!StringUtils.hasText(d))
			return "";
		int index = d.indexOf(".");
		if (index > 0) {
			d = d.substring(0, (index + accuracy + 1) > d.length() ? d.length()
					: (index + accuracy + 1));
		}
		return d;
	}

	/**
	 * 将给定的数值格式为易于理解的表示，如20000转换为2万
	 * 
	 * @param d
	 * @return
	 */
	public static String formatToSimple(double d) {
		if (d == 0)
			return String.valueOf(d);
		int tt = (int)d / 10000;
		if (tt > 0) {
			d = d / 10000;
			return truncate(String.valueOf(d), 2) + "万";
		}
		return truncate(String.valueOf(d), 2);
	}

	public static boolean isNumber(Object value) {
		if(value==null) return true;
		
		if(value instanceof Number) {
			return true;
		}else {
			return false;
		}
		
	}
	
	public static boolean isNumber(Class t) {

		
		if(Number.class.isAssignableFrom(t) ||
				t==int.class ||t==double.class || t==long.class ||t==float.class||t==short.class
				||t==byte.class) {
			return true;
		}else {
			return false;
		}
		
	}
	
	public static long[] toLongArray(int[] arrays) {
		if(arrays!=null && arrays.length>=0) {
			long[] ret=new long[arrays.length];
			for(int i=0; i<arrays.length; i++) {
				ret[i]=arrays[i];
			}
			return ret;
		}
		return new long[0];
	}
	public static void main(String args[]) {
		System.out.println(parseFloat("1,5457"));
		// System.out.println(getRandom(3));
		System.out.println((Double)3.433 instanceof Number);
		System.out.println(convertNumberToTargetClass(new Integer(0),Double.class));
		System.out.println(isNumber(int.class));
		
		int i=1234;
		long n=34566;
		
		int i2=(int)n;
	}
}
