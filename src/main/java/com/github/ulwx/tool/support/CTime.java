package com.github.ulwx.tool.support;

/**
 * @author cui
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class CTime {
	private static Logger log = LoggerFactory.getLogger(CTime.class);

	public static long MILSECOND_DAY = 1000 * 60 * 60 * 24;

	// 用于LocalDate，LocalDateTime，LocalTime的格式
	public static DateTimeFormatter DTF_YMD_HH_MM_SS_SSS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	public static DateTimeFormatter DTF_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static DateTimeFormatter DTF_YMD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static DateTimeFormatter DTF_HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");
	public static DateTimeFormatter DTF_HH_MM_SS_SSS = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
	public static DateTimeFormatter DTF_HHMMSSSSS = DateTimeFormatter.ofPattern("HHmmssSSS");
	public static DateTimeFormatter DTF_YMDHHMMSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	public static DateTimeFormatter DTF_YMDHHMMSSSSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
	public static DateTimeFormatter DTF_YYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

	public static String getCurrentDate() {
		String s = "";
		Calendar calend;
		TimeZone tz = TimeZone.getTimeZone("GMT+8:00");

		calend = Calendar.getInstance(tz);
		int year = calend.get(Calendar.YEAR);
		int M = calend.get(Calendar.MONTH) + 1;
		int d = calend.get(Calendar.DATE);
		int h = calend.get(Calendar.HOUR_OF_DAY);//
		int m = calend.get(Calendar.MINUTE);
		int ss = calend.get(Calendar.SECOND);

		String yearStr = "" + year;
		String MMStr = "" + M;
		String ddStr = "" + d;
		String hhStr = "" + h;
		String mmStr = "" + m;
		String ssStr = "" + ss;

		if (M < 10)
			MMStr = "0" + MMStr;
		if (d < 10)
			ddStr = "0" + ddStr;
		if (h < 10)
			hhStr = "0" + hhStr;
		if (m < 10)
			mmStr = "0" + mmStr;
		if (ss < 10)
			ssStr = "0" + ssStr;
		StringBuilder sb = new StringBuilder();
		sb.append(yearStr);
		sb.append("-");
		sb.append(MMStr);
		sb.append("-");
		sb.append(ddStr);
		sb.append(" ");
		sb.append(hhStr);
		sb.append(":");
		sb.append(mmStr);
		sb.append(":");
		sb.append(ssStr);

		return sb.toString();
		// return CTime.formatWholeDate(calend.getTime());
	}

	public static String formatTimeDate(Date dt) {
		// SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
		if (dt == null) {
			return "00:00:00.0";
		}
		SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
		sd.setLenient(false);
		return sd.format(dt);
	}

	public static Date getCurrentDateTime() {
		String s = "";
		Calendar calend;
		TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
		// TimeZone tzm = TimeZone.getDefault();
		// System.out.println(tz);
		// System.out.println(tzm);
		calend = Calendar.getInstance(tz);
		return calend.getTime();
	}

	public static Date getDate() {
		return CTime.getCurrentDateTime();
	}

	public static String formatDate(Date dt) {
		// SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		if (dt == null) {
			return "00000000000000";
		}
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		sd.setLenient(false);
		return sd.format(dt);

	}

	public static String formateDateToLocale(Date date) {
		return DateFormat.getDateInstance().format(date);
	}

	public static String formatDate() {
		// SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		sd.setLenient(false);
		return sd.format(getCurrentDateTime());
	}

	public static String formatWholeDate(Date dt) {
		// SimpleDateFormat dfk = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (dt == null) {
			return "0000-00-00 00:00:00.0";
		}
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sd.setLenient(false);
		return sd.format(dt);
	}

	public static String formatWholeAllDate(Date dt) {
		// SimpleDateFormat dfk = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (dt == null) {
			return "0000-00-00 00:00:00.000";

		}
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		sd.setLenient(false);
		return sd.format(dt);
	}

	public static String formatLogDate(Date dt) {
		// SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		if (dt == null) {
			return "0000-00-00 00:00:00.0";
		}
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		sd.setLenient(false);
		return sd.format(dt);
	}

	public static String formatLogDate() {
		// SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		sd.setLenient(false);
		return sd.format(getCurrentDateTime());
	}

	public static String formatWholeDate() {
		// SimpleDateFormat dfk = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sd.setLenient(false);
		return sd.format(getCurrentDateTime());
	}

	public static String formatWholeAllDate() {
		// SimpleDateFormat dfk = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		sd.setLenient(false);
		return sd.format(getCurrentDateTime());
	}

	/**
	 * 把2008-11-01 13:34:56 这个格式的日期转换成20081101133456格式
	 * 
	 * @param dt
	 * @return
	 */
	public static String formatDate(String dt) {
		StringBuffer strbf = new StringBuffer();
		StringTokenizer st = new StringTokenizer(dt.substring(0, 10), "-");
		while (st.hasMoreTokens()) {
			strbf.append(st.nextToken());
		}
		st = new StringTokenizer(dt.substring(11, 19), ":");
		while (st.hasMoreTokens()) {
			strbf.append(st.nextToken());
		}
		return strbf.toString();
	}

	public static String formatShortDate(Date dt) {

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		if (dt == null) {
			return "00000000";
		}
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		sd.setLenient(false);
		return sd.format(dt);
	}

	public static String formatShortDate() {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		sd.setLenient(false);
		return sd.format(getCurrentDateTime());
	}

	public static String formatRealDate(Date dt) {
		// SimpleDateFormat sdfk = new SimpleDateFormat("yyyy-MM-dd");
		if (dt == null) {
			return "0000-00-00";
		}
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		sd.setLenient(false);
		return sd.format(dt);
	}

	public static String formatYearDay(Date dt) {
		// SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-01");
		if (dt == null) {
			return "0000-00-00";
		}

		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		sd.setLenient(false);
		return sd.format(dt);
	}

	public static String getYYYY(Date dt) {
		// SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
		if (dt == null)
			return "0000";
		SimpleDateFormat sd = new SimpleDateFormat("yyyy");
		sd.setLenient(false);
		return sd.format(dt);
	}

	public static String getYYYY(String wholeDate) {
		Assert.notNull(wholeDate, "time string is null");
		wholeDate = wholeDate.trim();
		// yyyy-MM-dd HH:mm:ss.sss
		return wholeDate.substring(0, 4);

	}

	public static String getMM(String wholeDate) {
		Assert.notNull(wholeDate, "time string is null");
		wholeDate = wholeDate.trim();
		// yyyy-MM-dd HH:mm:ss.sss
		return wholeDate.substring(5, 7);
	}

	public static String getDD(String wholeDate) {
		Assert.notNull(wholeDate, "time string is null");
		wholeDate = wholeDate.trim();
		// yyyy-MM-dd HH:mm:ss.sss
		return wholeDate.substring(8, 10);
	}

	public static String getYYYY() {
		return getYYYY(getCurrentDateTime());
	}

	public static String getMM(Date dt) {
		// SimpleDateFormat mm = new SimpleDateFormat("MM");
		if (dt == null)
			return "00";

		SimpleDateFormat sd = new SimpleDateFormat("MM");

		sd.setLenient(false);
		return sd.format(dt);
	}

	public static String getMM() {
		return getMM(getCurrentDateTime());
	}

	public static String getDD(Date dt) {

		// SimpleDateFormat dd = new SimpleDateFormat("dd");
		if (dt == null)
			return "00";
		SimpleDateFormat sd = new SimpleDateFormat("dd");
		sd.setLenient(false);
		return sd.format(dt);
	}

	public static String getDD() {
		return getDD(getCurrentDateTime());
	}

	public static String formatShortDate(String dt) {
		StringBuffer strbf = new StringBuffer();
		StringTokenizer st = new StringTokenizer(dt.substring(0, 10), "-");
		while (st.hasMoreTokens()) {
			strbf.append(st.nextToken());
		}

		return strbf.toString();
	}

	public static boolean isWeekday(LocalDate date) throws ParseException {

		if (date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY)
			return true;

		return false;
	}

	private static Date parseDate(String dateString) {
		// SimpleDateFormat dfk = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (dateString == null)
				return null;
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sd.setLenient(false);

			return sd.parse(dateString);
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	private static Date parseAllDate(String dateString) {
		// SimpleDateFormat dfk = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (dateString == null)
				return null;

			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			sd.setLenient(false);

			return sd.parse(dateString);
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 解析 yyyy-mm-dd hh:mi:ss.SSS格式的日期 具有简单的容错
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date parseWholeAllDate(String dateString) {

		if (dateString == null)
			return null;

		dateString = dateString.trim();
		if (dateString.length() < 19)
			return null;
		else if (dateString.length() >= 19) {
			String[] strs = dateString.split(" +");
			dateString = StringUtils.trim(strs[0]);
			if (StringUtils.trim(strs[1]).lastIndexOf(".") < 0) {
				strs[1] = StringUtils.trim(strs[1]) + ".000";

			} else {
				strs[1] = StringUtils.trim(strs[1]);
			}
			dateString = dateString + " " + strs[1];
			return CTime.parseAllDate(dateString);
		} else {
			return null;
		}

	}

	/**
	 * 解析 yyyy-mm-dd hh:mi:ss格式的日期 具有简单的容错
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date parseWholeDate(String dateString) {

		if (dateString == null)
			return null;

		dateString = dateString.trim();
		if (dateString.length() < 19)
			return null;
		else if (dateString.length() >= 19) {
			String[] strs = dateString.split(" +");
			dateString = strs[0] + " " + strs[1];
		}
		return CTime.parseDate(dateString);
	}

	/**
	 * 解析yyyy-MM-dd模式的字符串转换成日期
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date parseDayDate(String dateString) {
		// SimpleDateFormat sdfk = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (dateString == null)
				return null;
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

			sd.setLenient(false);
			// sdfk.get().setLenient(false);// //严格匹配
			return sd.parse(dateString);
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 得到距离今天晚上23:59:59之前的秒数, 得到距离今天晚上23:59:59之前的秒数
	 * 
	 * @return
	 */

	public static long getExpireTime() {
		// java.util.Date todayDate = new java.util.Date();
		// java.util.Date todayDate=CTime.getDate();

		DateTime dt = new DateTime();
		DateTime dt2 = new DateTime();
		dt2.setHours(23);
		dt2.setMinutes(59);
		dt2.setSeconds(59);
		long result = (dt2.getTimeInMillis() - dt.getTimeInMillis()) / 1000;
		if (result < 0)
			result = 0;
		return result;

		// return 1000 * seconds;
	}

	/**
	 * 
	 * @param s1 被减日期字符串格式 yyyy-MM-dd
	 * @param s2 减日期字符串格式 yyyy-MM-dd
	 * @return 两个日期相差的天数
	 */
	public static long datediff(String s1, String s2) {
		Date d1 = parseDayDate(s1);
		Date d2 = parseDayDate(s2);
		return datediff(d1, d2);
	}

	public static Date parseYMDHMSDate(String dateString) {
		if (dateString == null)
			return null;

		dateString = dateString.trim();

		try {
			SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
			sd.setLenient(false);
			return sd.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.error("", e);
			return null;
		}
	}

	/**
	 * 根据给定的format格式返回日期
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date parseDate(String date, String format) {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		Date s = null;
		try {
			sf.setLenient(false);// 严格验证
			s = sf.parse(date);
		} catch (ParseException e) {
			log.error("", e);
			s = null;
		}
		return s;
	}

	public static Date parseDateTime(String date) {

		if (StringUtils.hasText(date)) {
			date = StringUtils.trim(date);
			if (date.length() == 10) {
				return CTime.parseDayDate(date);
			} else {
				if (date.length() >= 19) {
					return CTime.parseWholeAllDate(date);
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param date
	 * @param format 年yyyy 月MM 日dd 时HH 分mm 秒ss
	 * @return
	 */
	public static String formatDate(Date date, String format) {

		SimpleDateFormat sf = new SimpleDateFormat(format);
		sf.setLenient(false);// 严格匹配
		return sf.format(date);
	}

	/**
	 * 
	 * @param s1 被减日期字符串格式 yyyy-MM-dd
	 * @return 当前日期-S1 相差的天数
	 */
	public static long datediff(String s1) {
		Date d1 = parseDayDate(s1);
		Date d2 = getDate();
		return datediff(d2, d1);
	}

	public static long monthdiff(Date start, Date end) {

		DateTime s = new DateTime(start);
		DateTime e = new DateTime(end);

		long val = e.getMonth() - s.getMonth() + 12 * (e.getYear() - s.getYear());

		return val;

	}

	/**
	 * d1-d2的天数
	 * 
	 * @param start 被减日期
	 * @param end 减日期
	 * @return 两个日期相差的天数
	 */
	public static long datediff(Date start, Date end) {
		return ChronoUnit.DAYS.between(DateToLocalDate(start), DateToLocalDate(end));

	}

	/**
	 * 相隔多少天
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long datediff(LocalDate start, LocalDate end) {
		return ChronoUnit.DAYS.between(start, end);
	}

	/**
	 * 相隔多少天
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long datediff(LocalDateTime start, LocalDateTime end) {
		return ChronoUnit.DAYS.between(start, end);
	}

	/**
	 * 相隔多少秒
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long dateTimediffSecs(LocalDateTime start, LocalDateTime end) {
		return ChronoUnit.SECONDS.between(start, end);
	}

	/**
	 * 相隔多少小时
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long dateTimediffHours(LocalDateTime start, LocalDateTime end) {
		return ChronoUnit.HOURS.between(start, end);
	}

	/**
	 * 相隔多少分钟
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long dateTimediffMinutes(LocalDateTime start, LocalDateTime end) {
		return ChronoUnit.MINUTES.between(start, end);
	}

	/**
	 * 得到本月的第一天 ，格式："yyyy-MM-dd"
	 * 
	 * @return
	 */
	public static String getFirstDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return CTime.formatDate(calendar.getTime(), "yyyy-MM-dd");

	}

	public static LocalDate getFirstDayOfMonth(LocalDate date) {

		return date.with(TemporalAdjusters.firstDayOfMonth());

	}

	/**
	 * 得到本月的最后一天 ，格式："yyyy-MM-dd"
	 * 
	 * @return
	 */
	public static String getLastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return CTime.formatDate(calendar.getTime(), "yyyy-MM-dd");
	}

	public static LocalDate getLastDayOfMonth(LocalDate date) {

		return date.with(TemporalAdjusters.lastDayOfMonth());
	}

	/**
	 * 得到date后days天的日期对象
	 * 
	 * @param date
	 * @param days 可以为负数
	 * @return
	 */
	public static Date addDays(Date date, int days) {
		try {
			TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, days);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			log.error("", e);
		}
		return null;
	}

	public static Date addHours(Date date, int hours) {
		try {
			TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTime(date);
			calendar.add(Calendar.HOUR_OF_DAY, hours);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			log.error("", e);
		}
		return null;
	}

	public static Date addMinutes(Date date, int minutes) {
		try {
			TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTime(date);
			calendar.add(Calendar.MINUTE, minutes);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			log.error("", e);
		}
		return null;
	}

	public static Date addSenconds(Date date, int seconds) {
		try {
			TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTime(date);
			calendar.add(Calendar.SECOND, seconds);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			log.error("", e);
		}
		return null;
	}

	public static Date addMonths(int month) {
		return addMonths(CTime.getCurrentDateTime(), month);
	}

	public static long getLongMillsecs() {
		Long milliSecond = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
		return milliSecond;
	}

	public static long getLongMillsecs(LocalDateTime lt) {
		Long milliSecond = lt.toInstant(ZoneOffset.of("+8")).toEpochMilli();
		return milliSecond;
	}

	public static long getLongsecs() {
		Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
		return second;
	}

	public static long getLongsecs(LocalDateTime lt) {
		Long second = lt.toEpochSecond(ZoneOffset.of("+8"));
		return second;
	}

	public static LocalDateTime fromLongMillsecs(long timestamp) {
		return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
	}

	public static Date addMonths(Date date, int month) {
		try {
			TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, month);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			log.error("", e);
		}
		return null;
	}

	/**
	 * 根据指定的日期，延后多少个月，针对1月29，30，31号，都回延后到3月
	 * 
	 * @param date
	 * @param month 延后多少个月
	 * @return
	 */
	public static Date nextMonthsWithCurDay(Date date, int month) {
		try {
			TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTime(date);
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + month);
			return calendar.getTime();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 按30天来加
	 * 
	 * @param date
	 * @param month
	 * @return
	 */
	public static Date addMonthsWithCurDay(Date date, int month) {

		return addDays(date, 30 * month);
	}

	public static Date addYears(int year) {
		return addYears(CTime.getCurrentDateTime(), year);
	}

	public static Date addYears(Date date, int year) {
		try {
			TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTime(date);
			calendar.add(Calendar.YEAR, year);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			log.error("", e);
		}
		return null;
	}

	public static String formatLocalDateTime(LocalDateTime lt) {
		return lt.format(DTF_YMD_HH_MM_SS_SSS);
	}

	public static String formatLocalDateTime() {
		return LocalDateTime.now().format(DTF_YMD_HH_MM_SS_SSS);
	}

	public static String formatDateTime(LocalDateTime lt, DateTimeFormatter format) {
		return lt.format(format);
	}

	public static String formatLocalDate(LocalDate lt) {
		return lt.format(DTF_YMD);
	}

	public static String formatLocalDate() {
		return LocalDateTime.now().format(DTF_YMD);
	}

	public static String formatLocalDate(LocalDate lt, DateTimeFormatter format) {
		return lt.format(format);
	}

	public static String formatLocalTime(LocalTime lt, DateTimeFormatter format) {
		return lt.format(format);
	}

	public static String formatLocalTime(LocalTime lt) {
		return lt.format(CTime.DTF_HH_MM_SS_SSS);
	}

	/**
	 * 得到date后days天的日期对象
	 *
	 * @param days 可以为负数
	 * @return
	 */
	public static Date addDays(int days) {
		return addDays(CTime.getCurrentDateTime(), days);
	}

	public static Date addHours(int hours) {
		return addHours(CTime.getCurrentDateTime(), hours);
	}

	public static Date addMinutes(int minutes) {
		return addMinutes(CTime.getCurrentDateTime(), minutes);
	}

	public static Date addSenconds(int seconds) {
		return addMinutes(CTime.getCurrentDateTime(), seconds);
	}

	// 01. java.util.Date --> java.time.LocalDateTime
	public static LocalDateTime DateToLocalDateTime() {
		Date date = new Date();
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		return localDateTime;
	}

	public static LocalDateTime DateToLocalDateTime(Date ldt) {
		;
		Instant instant = ldt.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		return localDateTime;
	}

	// 02. java.util.Date --> java.time.LocalDate
	public static LocalDate DateToLocalDate() {
		Date date = new Date();
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		LocalDate localDate = localDateTime.toLocalDate();
		return localDate;
	}

	public static LocalDate DateToLocalDate(Date date) {
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		LocalDate localDate = localDateTime.toLocalDate();
		return localDate;
	}

	// 03. java.util.Date --> java.time.LocalTime
	public static LocalTime DateToLocalTime() {
		Date date = new Date();
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		LocalTime localTime = localDateTime.toLocalTime();
		return localTime;
	}

	public static LocalTime DateToLocalTime(Date date) {
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		LocalTime localTime = localDateTime.toLocalTime();
		return localTime;
	}

	// 04. java.time.LocalDateTime --> java.util.Date
	public static Date LocalDateTimeToDate() {
		LocalDateTime localDateTime = LocalDateTime.now();
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDateTime.atZone(zone).toInstant();
		Date date = Date.from(instant);
		return date;
	}

	public static Date LocalDateTimeToDate(LocalDateTime ldatetime) {
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = ldatetime.atZone(zone).toInstant();
		Date date = Date.from(instant);
		return date;
	}

	public static LocalDateTime firstDayTimeCurMonth() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime lt = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0);
		return lt;
	}

	public static LocalDateTime firstDayTimeNextMonth() {

		LocalDate firstDayNextMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);
		LocalDateTime lt = LocalDateTime.of(firstDayNextMonth.getYear(), firstDayNextMonth.getMonth(), 1, 0, 0, 0);
		return lt;
	}

	public static LocalDate firstDayCurMonth() {

		LocalDate lastDayCurMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
		return lastDayCurMonth;
	}

	public static LocalDateTime lastDayTimeCurMonth() {

		LocalDate firstDayNextMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
		LocalDateTime lt = LocalDateTime.of(firstDayNextMonth.getYear(), firstDayNextMonth.getMonth(),
				firstDayNextMonth.getDayOfMonth(), 23, 59, 59);
		return lt;
	}

	public static LocalDate lastDayCurMonth() {

		LocalDate lastDayCurMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
		return lastDayCurMonth;
	}
	public static LocalDate lastDayInMonth(LocalDate ld) {

		LocalDate lastDayCurMonth = ld.with(TemporalAdjusters.lastDayOfMonth());
		return lastDayCurMonth;
	}
	public static LocalDate firstDayNextMonth() {

		LocalDate firstDayNextMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);
		return firstDayNextMonth;
	}

	// 05. java.time.LocalDate --> java.util.Date
	public static Date LocalDateToDate() {
		LocalDate localDate = LocalDate.now();
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
		Date date = Date.from(instant);
		return date;
	}

	public static Date LocalDateToDate(LocalDate localDate) {

		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
		Date date = Date.from(instant);
		return date;
	}

	// 06. java.time.LocalTime --> java.util.Date
	public static Date LocalTimeToDate(LocalTime localTime) {

		LocalDate localDate = LocalDate.now();
		LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDateTime.atZone(zone).toInstant();
		Date date = Date.from(instant);
		return date;
	}

	public static void main(String[] args) throws Exception {

		// System.out.println(getCurrentDate());
		// System.out.println(datediff("2009-02-01"));

		// String spider_time = CTime.formatWholeDate();// 抓取时间
		// String pubtime = spider_time;
		// String yy = CTime.getYYYY(spider_time);
		// String mm = CTime.getMM(spider_time);
		// String dd = CTime.getDD(spider_time);
		// System.out.println(spider_time+"--"+yy+"-"+mm+"-"+dd);
		// System.out.println(CTime.formatWholeDate(CTime
		// .parseWholeDate("2009-12-13 12:34:35")));
		//
		// // System.out.println(CTime.formatWholeDate(CTime.addHours(48)));
		// String s = "2009年12月12日";
		// Date dd = CTime.parseDate(s, "yyyy年MM月dd日");
		// System.out.println(CTime.formatWholeAllDate(new Date()));
//		System.out.println(CTime.parseDateTime("2011-04-12 10:23:12 "));
//		System.out.println();
//		// System.out.println(monthdiff(CTime.parseWholeAllDate("2011-12-12
//		// 19:20:12"),
//		// CTime.parseWholeAllDate("2011-11-2 19:20:12")));
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		String dateString2 = "2016-10-31"; // 2016-10-01 1
//		Date date = sdf.parse(dateString2);
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(date);
//		int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
//		System.out.println(weekOfMonth);
//		LocalDateTime now = LocalDateTime.now();
//		LocalDateTime lt1 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
//		LocalDateTime lt2 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 14, 0);
//		LocalDate lt22 = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);
//		System.out.println(CTime.formatLocalDateTime(CTime.firstDayTimeNextMonth()));
//		DateTime dt = new DateTime();
//		dt.setYear(2017);
//		dt.setMonth(1);
//		dt.setDay(1);
//		Date dd = CTime.addMonthsWithCurDay(dt.getDate(), 6);
//		System.out.println(CTime.formatRealDate(dd));
//		// System.out.println(dateTimediffMinutes(lt1,lt2));
//
//		System.out.println(CTime.datediff(dt.getDate(), dd));
//		System.out.println(CTime.addMonths(12 * 20).getTime());
//		// System.out.println(CTime.dateTimediffMinutes(start, end));
//		Date dttt = LocalDateToDate();
//		System.out.println(CTime.formatWholeAllDate(dttt));
//		System.out.println(isWeekday(LocalDate.now()));
		
		Date dt=nextMonthsWithCurDay(CTime.LocalDateToDate(LocalDate.of(2019, 1, 31)),3);
		System.out.println(CTime.formatRealDate(dt));
		dt=addMonths(CTime.LocalDateToDate(LocalDate.of(2019, 1, 31)),3);
		System.out.println(CTime.formatRealDate(dt));
		

	}

}
