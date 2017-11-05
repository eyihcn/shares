package eyihcn.shares.utlis;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 日期时间工具类
 * 
 * @author tomtop2016
 */
public final class DateUtils {

	/** 缺省日期格式 */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	/** 缺省时间格式 */
	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

	/** 缺省月格式 */
	public static final String DEFAULT_MONTH = "MONTH";

	/** 缺省年格式 */
	public static final String DEFAULT_YEAR = "YEAR";

	/** 缺省日格式 */
	public static final String DEFAULT_DATE = "DAY";

	/** 缺省小时格式 */
	public static final String DEFAULT_HOUR = "HOUR";

	/** 缺省分钟格式 */
	public static final String DEFAULT_MINUTE = "MINUTE";

	/** 缺省秒格式 */
	public static final String DEFAULT_SECOND = "SECOND";

	/** 缺省长日期格式 */
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH-mm";

	/** 缺省长日期格式,精确到秒 */
	public static final String DEFAULT_DATETIME_FORMAT_SEC = "yyyy-MM-dd HH:mm:ss";

	/** 星期数组 */
	public static final String[] WEEKS = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };

//	private static final SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

//	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	private DateUtils() {
	}

	/** 锁对象 */
	private static final Object lockObj = new Object();

	/** 存放不同的日期模板格式的sdf的Map */
	private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new WeakHashMap<String, ThreadLocal<SimpleDateFormat>>();

	/**
	 * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
	 * 
	 * @param pattern
	 * @return
	 */
	private static SimpleDateFormat getSdf(final String pattern) {
		ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

		// 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
		if (tl == null) {
			synchronized (lockObj) {
				tl = sdfMap.get(pattern);
				if (tl == null) {
					// 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
//					System.out.println("put new sdf of pattern " + pattern + " to map");

					// 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new
					// SimpleDateFormat
					tl = new ThreadLocal<SimpleDateFormat>() {

						@Override
						protected SimpleDateFormat initialValue() {
//							System.out.println("thread: " + Thread.currentThread() + " init pattern: " + pattern);
							return new SimpleDateFormat(pattern);
						}
					};
					sdfMap.put(pattern, tl);
				}
			}
		}

		return tl.get();
	}

	/**
	 * 获得当前日期时间
	 * <p>
	 * 日期时间格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String currentDatetime() {
		return getSdf("yyyy-MM-dd HH:mm:ss").format(now());
	}

	/**
	 * 格式化日期时间
	 * <p>
	 * 日期时间格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String formatDatetime(Date date) {
		return getSdf("yyyy-MM-dd HH:mm:ss").format(date);
	}

	/**
	 * 格式化日期时间
	 * 
	 * @param date
	 * @param pattern
	 *            格式化模式，详见{@link SimpleDateFormat}构造器
	 *            <code>SimpleDateFormat(String pattern)</code>
	 * @return
	 */
	public static String formatDatetime(Date date, String pattern) {
		return getSdf("yyyy-MM-dd HH:mm:ss").format(date);
	}

	/**
	 * 将日期秒置0
	 * 
	 * @param date
	 * @return
	 */
	public static Date setDateTimeSecondToZero(Date date) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.set(Calendar.SECOND, 0);
		return calender.getTime();
	}

	/**
	 * 获得当前日期
	 * <p>
	 * 日期格式yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String currentDate() {
		return getSdf("yyyy-MM-dd").format(now());
	}

	/**
	 * 格式化日期
	 * <p>
	 * 日期格式yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String formatDate(Date date) {
		return getSdf("yyyy-MM-dd").format(date);
	}

	/**
	 * 格式化时间
	 * <p>
	 * 时间格式HH:mm:ss
	 * 
	 * @return
	 */
	public static String formatTime(Date date) {
		return getSdf("HH:mm:ss").format(date);
	}

	/**
	 * 获得当前时间的<code>java.util.Date</code>对象
	 * 
	 * @return
	 */
	public static Date now() {
		return new Date();
	}

	public static Calendar calendar() {
		Calendar cal = GregorianCalendar.getInstance(Locale.CHINESE);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		return cal;
	}

	/**
	 * 获得当前时间的毫秒数
	 * <p>
	 * 详见{@link System#currentTimeMillis()}
	 * 
	 * @return
	 */
	public static long millis() {
		return System.currentTimeMillis();
	}

	/**
	 * 
	 * 获得当前Chinese月份
	 * 
	 * @return
	 */
	public static int month() {
		return calendar().get(Calendar.MONTH) + 1;
	}

	/**
	 * 获得月份中的第几天
	 * 
	 * @return
	 */
	public static int dayOfMonth() {
		return calendar().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 今天是星期的第几天
	 * 
	 * @return
	 */
	public static int dayOfWeek() {
		return calendar().get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 今天是年中的第几天
	 * 
	 * @return
	 */
	public static int dayOfYear() {
		return calendar().get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 判断原日期是否在目标日期之前
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public static boolean isBefore(Date src, Date dst) {
		return src.before(dst);
	}

	/**
	 * 判断原日期是否在目标日期之后
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public static boolean isAfter(Date src, Date dst) {
		return src.after(dst);
	}

	/**
	 * 判断两日期是否相同
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isEqual(Date date1, Date date2) {
		return date1.compareTo(date2) == 0;
	}

	/**
	 * 判断某个日期是否在某个日期范围
	 * 
	 * @param beginDate
	 *            日期范围开始
	 * @param endDate
	 *            日期范围结束
	 * @param src
	 *            需要判断的日期
	 * @return
	 */
	public static boolean between(Date beginDate, Date endDate, Date src) {
		return beginDate.before(src) && endDate.after(src);
	}

	/**
	 * 获得当前月的最后一天
	 * <p>
	 * HH:mm:ss为0，毫秒为999
	 * 
	 * @return
	 */
	public static Date lastDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		// Calendar cal = calendar();
		// cal.set(Calendar.DAY_OF_MONTH, 0); // M月置零
		// cal.set(Calendar.HOUR_OF_DAY, 0);// H置零
		// cal.set(Calendar.MINUTE, 0);// m置零
		// cal.set(Calendar.SECOND, 0);// s置零
		// cal.set(Calendar.MILLISECOND, 0);// S置零
		// cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);// 月份+1
		// cal.set(Calendar.MILLISECOND, -1);// 毫秒-1
		return cal.getTime();
	}

	/**
	 * 获得当前月的第一天
	 * <p>
	 * HH:mm:ss SS为零
	 * 
	 * @return
	 */
	public static Date firstDayOfMonth() {
		Calendar cal = calendar();
		cal.set(Calendar.DAY_OF_MONTH, 1); // M月置1
		cal.set(Calendar.HOUR_OF_DAY, 0);// H置零
		cal.set(Calendar.MINUTE, 0);// m置零
		cal.set(Calendar.SECOND, 0);// s置零
		cal.set(Calendar.MILLISECOND, 0);// S置零
		return cal.getTime();
	}

	public static Date firstDay(Date initDate) {
		String[] initDateArray = toString(initDate, "yyyy-MM-dd").split("-");
		int year = Integer.parseInt(initDateArray[0]);
		int month = Integer.parseInt(initDateArray[1]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date firstDay = cal.getTime();// 考勤的第一天
		return firstDay;
	}

	public static Date lastDay(Date initDate) {
		String[] initDateArray = toString(initDate, "yyyy-MM-dd").split("-");
		int year = Integer.parseInt(initDateArray[0]);
		int month = Integer.parseInt(initDateArray[1]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		// 工时月的最后一天
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.SECOND, -1);
		Date lastDay = cal.getTime();// 考勤的最后一天
		return lastDay;
	}

	private static Date weekDay(int week) {
		Calendar cal = calendar();
		cal.set(Calendar.DAY_OF_WEEK, week);
		return cal.getTime();
	}

	/**
	 * 获得周五日期
	 * <p>
	 * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday
	 * 
	 * @return
	 */
	public static Date friday() {
		return weekDay(Calendar.FRIDAY);
	}

	/**
	 * 获得周六日期
	 * <p>
	 * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday
	 * 
	 * @return
	 */
	public static Date saturday() {
		return weekDay(Calendar.SATURDAY);
	}

	/**
	 * 获得周日日期
	 * <p>
	 * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday
	 * 
	 * @return
	 */
	public static Date sunday() {
		return weekDay(Calendar.SUNDAY);
	}

	/**
	 * 将字符串日期时间转换成java.util.Date类型
	 * <p>
	 * 日期时间格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @param datetime
	 * @return
	 */
	public static Date parseDatetime(String datetime) throws ParseException {
		return getSdf("yyyy-MM-dd HH:mm:ss").parse(datetime);
	}

	/**
	 * 将字符串日期转换成java.util.Date类型
	 * <p>
	 * 日期时间格式yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String date) throws ParseException {
		return getSdf("yyyy-MM-dd").parse(date);
	}

	/**
	 * 将字符串日期转换成java.util.Date类型
	 * <p>
	 * 时间格式 HH:mm:ss
	 * 
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public static Date parseTime(String time) throws ParseException {
		return getSdf("HH:mm:ss").parse(time);
	}

	/**
	 * 根据自定义pattern将字符串日期转换成java.util.Date类型
	 * 
	 * @param datetime
	 * @param pattern
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDatetime(String datetime, String pattern) throws ParseException {
		return getSdf("yyyy-MM-dd").parse(datetime);
	}

	/**
	 * 取当前日期的字符串表示
	 * 
	 * @return 当前日期的字符串 ,如2010-05-28
	 **/
	public static String today() {
		return today(DEFAULT_DATE_FORMAT);
	}

	/**
	 * 根据输入的格式得到当前日期的字符串
	 * 
	 * @param strFormat
	 *            日期格式
	 * @return
	 */
	public static String today(String strFormat) {
		return toString(new Date(), strFormat);
	}

	/**
	 * 取当前时间的字符串表示,
	 * 
	 * @return 当前时间,如:21:10:12
	 **/
	public static String currentTime() {
		return currentTime(DEFAULT_TIME_FORMAT);
	}

	/**
	 * 根据输入的格式获取时间的字符串表示
	 * 
	 * @param 输出格式
	 *            ,如'hh:mm:ss'
	 * @return 当前时间,如:21:10:12
	 **/

	public static String currentTime(String strFormat) {
		return toString(new Date(), strFormat);
	}

	/**
	 * 取得相对于当前时间增加天数/月数/年数后的日期 <br>
	 * 欲取得当前日期5天前的日期,可做如下调用:<br>
	 * getAddDay("DATE", -5).
	 * 
	 * @param field
	 *            ,段,如"year","month","date",对大小写不敏感
	 * @param amount
	 *            ,增加的数量(减少用负数表示),如5,-1
	 * @return 格式化后的字符串 如"2010-05-28"
	 * @throws ParseException
	 **/

	public static String getAddDay(String field, int amount) throws ParseException {
		return getAddDay(field, amount, null);
	}

	/**
	 * 取得相对于当前时间增加天数/月数/年数后的日期,按指定格式输出
	 * 
	 * 欲取得当前日期5天前的日期,可做如下调用:<br>
	 * getAddDay("DATE", -5,'yyyy-mm-dd hh:mm').
	 * 
	 * @param field
	 *            ,段,如"year","month","date",对大小写不敏感
	 * @param amount
	 *            ,增加的数量(减少用负数表示),如5,-1
	 * @param strFormat
	 *            ,输出格式,如"yyyy-mm-dd","yyyy-mm-dd hh:mm"
	 * @return 格式化后的字符串 如"2010-05-28"
	 * @throws ParseException
	 **/
	public static String getAddDay(String field, int amount, String strFormat) throws ParseException {
		return getAddDay(null, field, amount, strFormat);
	}

	/**
	 * 功能：对于给定的时间增加天数/月数/年数后的日期,按指定格式输出
	 * 
	 * @param date
	 *            String 要改变的日期
	 * @param field
	 *            int 日期改变的字段，YEAR,MONTH,DAY
	 * @param amount
	 *            int 改变量
	 * @param strFormat
	 *            日期返回格式
	 * @return
	 * @throws ParseException
	 */
	public static String getAddDay(String date, String field, int amount, String strFormat) throws ParseException {
		if (strFormat == null) {
			strFormat = DEFAULT_DATETIME_FORMAT_SEC;
		}
		Calendar rightNow = Calendar.getInstance();
		if (date != null && !"".equals(date.trim())) {
			rightNow.setTime(parseDate(date, strFormat));
		}
		if (field == null) {
			return toString(rightNow.getTime(), strFormat);
		}
		rightNow.add(getInterval(field), amount);
		return toString(rightNow.getTime(), strFormat);
	}

	/**
	 * 获取时间间隔类型
	 * 
	 * @param field
	 *            时间间隔类型
	 * @return 日历的时间间隔
	 */
	protected static int getInterval(String field) {
		String tmpField = field.toUpperCase();
		if (tmpField.equals(DEFAULT_YEAR)) {
			return Calendar.YEAR;
		} else if (tmpField.equals(DEFAULT_MONTH)) {
			return Calendar.MONTH;
		} else if (tmpField.equals(DEFAULT_DATE)) {
			return Calendar.DATE;
		} else if (DEFAULT_HOUR.equals(tmpField)) {
			return Calendar.HOUR;
		} else if (DEFAULT_MINUTE.equals(tmpField)) {
			return Calendar.MINUTE;
		} else {
			return Calendar.SECOND;
		}
	}

	/**
	 * 获取格式化对象
	 * 
	 * @param strFormat
	 *            格式化的格式 如"yyyy-MM-dd"
	 * @return 格式化对象
	 */
	public static SimpleDateFormat getSimpleDateFormat(String strFormat) {
		if (strFormat != null && !"".equals(strFormat.trim())) {
			return new SimpleDateFormat(strFormat);
		} else {
			return new SimpleDateFormat();
		}
	}

	/**
	 * 得到当前日期的星期数
	 * 
	 * @return 当前日期的星期的字符串
	 * @throws ParseException
	 */
	public static String getWeekOfMonth() throws ParseException {
		return getWeekOfMonth(null, null);
	}

	/**
	 * 根据日期的到给定日期的在当月中的星期数
	 * 
	 * @param date
	 *            给定日期
	 * @return
	 * @throws ParseException
	 */
	public static String getWeekOfMonth(String date, String fromat) throws ParseException {
		Calendar rightNow = Calendar.getInstance();
		if (date != null && !"".equals(date.trim())) {
			rightNow.setTime(parseDate(date, fromat));
		}
		return WEEKS[rightNow.get(Calendar.WEEK_OF_MONTH)];
	}

	/**
	 * 将java.util.date型按照指定格式转为字符串
	 * 
	 * @param date
	 *            源对象
	 * @param format
	 *            想得到的格式字符串
	 * @return 如：2010-05-28
	 */
	public static String toString(Date date, String format) {
		return getSimpleDateFormat(format).format(date);
	}

	/**
	 * 将java.util.date型按照缺省格式转为字符串
	 * 
	 * @param date
	 *            源对象
	 * @return 如：2010-05-28
	 */
	public static String toString(Date date) {
		return toString(date, DEFAULT_DATE_FORMAT);
	}

	/**
	 * 强制类型转换 从串到日期
	 * 
	 * @param sDate
	 *            源字符串，采用yyyy-MM-dd格式
	 * @param sFormat
	 *            ps
	 * @return 得到的日期对象
	 * @throws ParseException
	 */
	public static Date parseDate(String strDate, String format) throws ParseException {
		return getSimpleDateFormat(format).parse(strDate);
	}

	/***
	 * 根据传入的毫秒数和格式，对日期进行格式化输出
	 * 
	 * @version 2011-7-12
	 * @param object
	 * @param format
	 * @return
	 */
	public static String millisecondFormat(Long millisecond, String format) {
		if (millisecond == null || millisecond <= 0) {
			throw new IllegalArgumentException(String.format("传入的时间毫秒数[%s]不合法", "" + millisecond));
		}
		if (format == null || "".equals(format.trim())) {
			format = DEFAULT_DATE_FORMAT;
		}
		return toString(new Date(millisecond), format);
	}

	/**
	 * 强制类型转换 从串到时间戳
	 * 
	 * @param sDate
	 *            源串
	 * @param sFormat
	 *            遵循格式
	 * @return 取得的时间戳对象
	 * @throws ParseException
	 */
	public static Timestamp parseTimestamp(String strDate, String format) throws ParseException {
		Date utildate = getSimpleDateFormat(format).parse(strDate);
		return new Timestamp(utildate.getTime());
	}

	/**
	 * getCurDate 取当前日期
	 * 
	 * @return java.util.Date型日期
	 **/
	public static Date getCurDate() {
		return (new Date());
	}

	/**
	 * getCurTimestamp 取当前时间戳
	 * 
	 * @return java.sql.Timestamp
	 **/
	public static Timestamp getCurTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * getCurTimestamp 取遵循格式的当前时间
	 * 
	 * @param sFormat
	 *            遵循格式
	 * @return java.sql.Timestamp
	 **/
	public static Date getCurDate(String format) throws Exception {
		return getSimpleDateFormat(format).parse(toString(new Date(), format));
	}

	/**
	 * Timestamp按照指定格式转为字符串
	 * 
	 * @param timestamp
	 *            源对象
	 * @param format
	 *            ps（如yyyy.mm.dd）
	 * @return 如：2010-05-28 或2010-05-281 13:21
	 */
	public static String toString(Timestamp timestamp, String format) {
		if (timestamp == null) {
			return "";
		}
		return toString(new Date(timestamp.getTime()), format);
	}

	/**
	 * Timestamp按照缺省格式转为字符串
	 * 
	 * @param ts
	 *            源对象
	 * @return 如：2010-05-28
	 */
	public static String toString(Timestamp ts) {
		return toString(ts, DEFAULT_DATE_FORMAT);
	}

	/**
	 * Timestamp按照缺省格式转为字符串，可指定是否使用长格式
	 * 
	 * @param timestamp
	 *            欲转化之变量Timestamp
	 * @param fullFormat
	 *            是否使用长格式
	 * @return 如：2010-05-28 或2010-05-28 21:21
	 */
	public static String toString(Timestamp timestamp, boolean fullFormat) {
		if (fullFormat) {
			return toString(timestamp, DEFAULT_DATETIME_FORMAT_SEC);
		} else {
			return toString(timestamp, DEFAULT_DATE_FORMAT);
		}
	}

	/**
	 * 将sqldate型按照指定格式转为字符串
	 * 
	 * @param sqldate
	 *            源对象
	 * @param sFormat
	 *            ps
	 * @return 如：2010-05-28 或2010-05-28 00:00
	 */
	public static String toString(java.sql.Date sqldate, String sFormat) {
		if (sqldate == null) {
			return "";
		}
		return toString(new Date(sqldate.getTime()), sFormat);
	}

	/**
	 * 比较两个时间的大小 格式为HH:mm:ss
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int compareTime(String s1, String s2) {
		java.text.DateFormat df = new java.text.SimpleDateFormat("HH:mm:ss");
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		java.util.Calendar c2 = java.util.Calendar.getInstance();
		try {
			c1.setTime(df.parse(s1));
			c2.setTime(df.parse(s2));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		int result = c1.compareTo(c2);
		if (result == 0)
			return 0; // c1==c2
		else if (result < 0)
			return 1; // c1<c2
		else
			return 2; // c1>c2

	}

	/**
	 * 将sqldate型按照缺省格式转为字符串
	 * 
	 * @param sqldate
	 *            源对象
	 * @return 如：2010-05-28
	 */
	public static String toString(java.sql.Date sqldate) {
		return toString(sqldate, DEFAULT_DATE_FORMAT);
	}

	/**
	 * 计算日期时间之间的差值， date1得时间必须大于date2的时间
	 * 
	 * @version 2011-7-12
	 * @param date1
	 * @param date2
	 * @return {@link java.util.Map} Map的键分别为, day(天),
	 *         hour(小时),minute(分钟)和second(秒)。
	 */
	public static Map<String, Long> timeDifference(final Date date1, final Date date2) {
		if (date1 == null || date2 == null) {
			throw new NullPointerException("date1 and date2 can't null");
		}
		long mim1 = date1.getTime();
		long mim2 = date2.getTime();
		if (mim1 < mim2) {
			throw new IllegalArgumentException(String.format("date1[%s] not be less than date2[%s].", mim1 + "", mim2 + ""));
		}
		long m = (mim1 - mim2 + 1) / 1000l;
		long mday = 24 * 3600;
		final Map<String, Long> map = new HashMap<String, Long>();
		map.put("day", m / mday);
		m = m % mday;
		map.put("hour", (m) / 3600);
		map.put("minute", (m % 3600) / 60);
		map.put("second", (m % 3600 % 60));
		return map;
	}

	public static Map<String, Integer> compareTo(final Date date1, final Date date2) {
		if (date1 == null || date2 == null) {
			return null;
		}
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long time = Math.max(time1, time2) - Math.min(time1, time2);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("year", (calendar.get(Calendar.YEAR) - 1970) > 0 ? (calendar.get(Calendar.YEAR) - 1970) : 0);
		map.put("month", (calendar.get(Calendar.MONTH) - 1) > 0 ? (calendar.get(Calendar.MONTH) - 1) : 0);
		map.put("day", (calendar.get(Calendar.DAY_OF_MONTH) - 1) > 0 ? (calendar.get(Calendar.DAY_OF_MONTH) - 1) : 0);
		map.put("hour", (calendar.get(Calendar.HOUR_OF_DAY) - 8) > 0 ? (calendar.get(Calendar.HOUR_OF_DAY) - 8) : 0);
		map.put("minute", calendar.get(Calendar.MINUTE) > 0 ? calendar.get(Calendar.MINUTE) : 0);
		map.put("second", calendar.get(Calendar.SECOND) > 0 ? calendar.get(Calendar.SECOND) : 0);
		return map;
	}

	/**
	 * 将字符串日期转换成java.util.Date类型 返回当前月份的最后一天
	 * <p>
	 * 日期时间格式yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date lastDay(String date) throws ParseException {
		return lastDay(parseDate(date));
	}

	/**
	 * 将字符串日期转换成java.util.Date类型 返回当前月份的第一天
	 * <p>
	 * 日期时间格式yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date firstDay(String date) throws ParseException {
		return firstDay(parseDate(date));
	}

	/**
	 * 判断时间戳差是否大于mins分钟
	 */
	public static boolean isDifferThanMins(Date time1, Date time2, long mins) {
		return Math.abs(differTime(time1, time2, Calendar.MINUTE)) > mins;
	}

	/** 计算两个日期之间的type类型的差值(date1-date2) */
	public static long differTime(Date date1, Date date2, int type) {
		if (type == Calendar.SECOND)
			return date1.getTime() / 1000 - date2.getTime() / 1000;
		else if (type == Calendar.MINUTE)
			return date1.getTime() / 60000 - date2.getTime() / 60000;
		else if (type == Calendar.HOUR)
			return date1.getTime() / 3600000 - date2.getTime() / 3600000;
		else if (type == Calendar.DAY_OF_MONTH)
			// return date1.getTime() / (24*60*60*1000) - date2.getTime() /
			// (24*60*60*1000);
			return date1.getTime() / 86400000 - date2.getTime() / 86400000; // 用立即数，减少乘法计算的开销
		else
			// 否则一律按毫秒
			return date1.getTime() - date2.getTime();
	}

	/**
	 * 组合日期和时间为Date类型
	 * 
	 * @param date
	 *            日期 yyyy-MM-dd
	 * @param time
	 *            时间 HH:mm:ss
	 * @return 结果的Date值
	 */
	public static Date combineDateTime(Date date, Date time) {
		Calendar timeCal = Calendar.getInstance();
		timeCal.setTime(time);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
		return cal.getTime();
	}

	/**
	 * 组合日期和时间为Date类型 日期 yyyy-MM-dd 时间 HH:mm:ss
	 */
	public static Date combineDateTime(String date, String time) throws Exception {
		return combineDateTime(DateUtils.parseDate(date), new java.sql.Time(DateUtils.parseDate(time).getTime()));
	}

	/**
	 * 组合日期和时间为Date类型 日期 yyyy-MM-dd 时间 HH:mm:ss
	 */
	public static Date combineDateTime(Date date, String time) throws Exception {
		return combineDateTime(date, DateUtils.parseDatetime(time, DEFAULT_TIME_FORMAT));
	}

	/**
	 * 组合日期和时间为Date类型 日期 yyyy-MM-dd 时间 HH:mm:ss
	 */
	public static Date combineDateTime(String date, Date time) throws Exception {
		return combineDateTime(date, time);
	}

	/**
	 * 返回 HH:mm:ss 时间类型
	 */
	public static Date getDateTime(Date record) throws Exception {
		return parseDatetime(formatDatetime(record, DEFAULT_TIME_FORMAT), DEFAULT_TIME_FORMAT);
	}

	/** 日期增加1天 */
	public static Date incrementDate(Date date) {
		return incrementDate(date, 1);
	}

	/** 日期增加n天 */
	public static Date incrementDate(Date date, int offset) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, offset);
		return cal.getTime();
	}

	/** 日期减去1天 */
	public static Date decrementDay(Date date) {
		return decrementDay(date, 1);
	}

	/** 日期减去n天 */
	public static Date decrementDay(Date date, int offset) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -offset);
		date = cal.getTime();
		return date;
	}

	public static int getDay_Of_Year(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_YEAR);
	}

	public static int getDay_Of_Week(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static int getDay_Of_Month(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static int getDay(Date date, int type) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(type);
	}

	@SuppressWarnings("deprecation")
	public static boolean isSameYear(Date date1, Date date2) {
		return date1.getYear() == date2.getYear();
	}

	/** 判断date1和date2是否在同一天 */
	public static boolean isSameDay(Date date1, Date date2) {
		int beginDay = DateUtils.getDay_Of_Year(date1);
		int endDay = DateUtils.getDay_Of_Year(date2);
		if (DateUtils.isSameYear(date1, date2)) {
			return beginDay == endDay;
		}
		return false;
	}

	/** 判断src是否和dst相同或者在dst之后 */
	public static boolean isAfterOrEqual(Date src, Date dst) {
		return src.compareTo(dst) >= 0;
	}

	/** 判断src是否和dst相同或者在dst之前 */
	public static boolean isBeforeOrEqual(Date src, Date dst) {
		return src.compareTo(dst) <= 0;
	}

	/**
	 * @author chenyi
	 * @version 创建时间：2015年9月1日 下午2:01:12
	 * @param recordDate
	 * @return
	 */
	public static boolean isSunday(Date currDate) {
		return DateUtils.getDay_Of_Week(currDate) == Calendar.SUNDAY;
	}

	/**
	 * @author chenyi
	 * @version 创建时间：2015年9月1日 下午2:01:55
	 * @param recordDate
	 * @return
	 */
	public static boolean isSaturday(Date currDate) {
		return DateUtils.getDay_Of_Week(currDate) == Calendar.SATURDAY;
	}

	/**
	 * @author chenyi
	 * @version 创建时间：2015年9月2日 下午5:03:47
	 * @param currDate
	 * @return
	 */
	public static boolean isWeekendDay(Date currDate) {
		return isSaturday(currDate) || isSunday(currDate);
	}

	/**
	 * @author chenyi
	 * @version 创建时间：2015年9月6日 下午1:10:24
	 * @param currDate
	 *            日期的格式yyyy-MM-dd
	 * @param hour_of_day
	 *            时刻点(0~23)
	 * @param minute
	 *            分钟点(0~59)
	 * @param second
	 *            秒钟点(0~59)
	 * @return 返回一个日期Date类型 格式yyyy-MM-dd HH:mm:ss
	 */
	public static Date newDate(Date currDate, int hour_of_day, int minute, int second) {

		Calendar date = Calendar.getInstance();
		date.setTime(currDate);
		date.set(Calendar.HOUR_OF_DAY, hour_of_day);
		date.set(Calendar.MINUTE, minute);
		date.set(Calendar.SECOND, second);
		return date.getTime();
	}

	/**
	 * @author chenyi
	 * @version 创建时间：2015年9月6日 下午5:02:44
	 * @param date
	 * @return
	 */
	public static int getHour_of_day(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * @author chenyi
	 * @version 创建时间：2015年9月6日 下午5:32:34
	 * @param date
	 * @return
	 */
	public static int getMin_of_date(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MINUTE);
	}

	/**
	 * @author chenyi
	 * @version 创建时间：2015年9月10日 下午4:50:45
	 * @param date
	 * @param mins
	 * @return
	 */
	public static Date addMins_of_date(Date date, int mins) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, mins);
		return cal.getTime();
	}

	public static Calendar newCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	/** 判断当天是否为本月的第五个周六 */
	public static boolean isFifthSaturdayOfMonth(Date attendDate) {

		if (!DateUtils.isSaturday(attendDate)) {
			return false;
		}
		// 1.找出第一个周六在本月的第几天
		// 2.在往后推算第五个周六是本月的第几天
		// 3.可以判断是否存在第五个周六
		if (!DateUtils.hasFifthSaturdayOfMonth(attendDate)) {
			return false;
		}
		// 4.判断当天是否为第五个周六
		if (DateUtils.getDay(attendDate, Calendar.DAY_OF_MONTH) != getFifthSaturNumOfMonth(attendDate)) {
			return false;
		}
		return true;
	}

	/** 从月份的第一个星期六开始，获得第五个星期六在第几天（可能大于31） */
	private static int getFifthSaturNumOfMonth(Date attendDate) {
		return getFirstSaturdayNumOfMonth(attendDate) + 28;
	}

	/** 找出第一个周六在本月的第几天 */
	public static int getFirstSaturdayNumOfMonth(Date date) {

		int weekNum = DateUtils.getDay(DateUtils.firstDay(date), Calendar.DAY_OF_WEEK);
		int dayNum = 1;
		while (true) {
			if (weekNum == 7) {
				break;
			}
			weekNum++;
			dayNum++;
		}
		return dayNum;
	}

	/** 判断一个月是否有第五个星期六 */
	public static boolean hasFifthSaturdayOfMonth(Date date) {
		return DateUtils.getFifthSaturNumOfMonth(date) <= DateUtils.getDay(DateUtils.lastDay(date), Calendar.DAY_OF_MONTH);
	}

	/** 获得一个月的总天数 */
	public static int getDayCountsOfMonth(Date date) {
		return DateUtils.getDay(DateUtils.lastDay(date), Calendar.DAY_OF_MONTH);
	}

	/** 获得一个月中周末的总天数 */
	public static int getWeekendDayCountsOfMonth(Date yfDate) {

		int weekendDayCounts = 0; // 周末的天数
		// 判断本月的第一天是否是星期天
		if (DateUtils.getDay(DateUtils.firstDay(yfDate), Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) { // 当月的第一天是周日，
			weekendDayCounts++;
		}
		// 判断当月的最后一天是否为星期六
		if (DateUtils.getDay(DateUtils.lastDay(yfDate), Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			weekendDayCounts--;
		}
		// 判断是否存在第五个周六
		if (DateUtils.hasFifthSaturdayOfMonth(yfDate)) {
			weekendDayCounts += 10;
		} else {
			weekendDayCounts += 8;
		}
		return weekendDayCounts;
	}

	/** 获得一个月中星期天的总天数 */
	public static int getSundayCountsOfMonth(Date date) {
		return DateUtils.getWeekendDayCountsOfMonth(date) - DateUtils.getSaturdayCountsOfMonth(date);
	}

	/** 获得一个月中星期六的总天数 */
	public static int getSaturdayCountsOfMonth(Date date) {
		return DateUtils.hasFifthSaturdayOfMonth(date) ? 5 : 4;
	}

	/** 获得一个月中星期一至周五的总天数 */
	public static int getWeekdayCountsOfMonth(Date date) {
		return DateUtils.getDayCountsOfMonth(date) - DateUtils.getWeekendDayCountsOfMonth(date);
	}

	/** 获得一个月中除星期天外的总天数 */
	public static int getDayCountsOfMonthExceptSunday(Date date) {
		return DateUtils.getDayCountsOfMonth(date) - DateUtils.getSundayCountsOfMonth(date);
	}

	/** 判断两个日期是否同年同月 */
	public static boolean isSameMonth(Date date1, Date date2) {
		return DateUtils.isSameYear(date1, date2) ? (DateUtils.getDay(date1, Calendar.MONTH) == DateUtils.getDay(date2, Calendar.MONTH) ? true : false) : false;
	}

	/** 同年同月的start和end之间的 总天数。 0:总天数;1:周一至周五的天数;2:周六的天数 */
	public static int[] getCountsBetweenDate(Date start, Date end) {

		int[] results = { 0, 0, 0 };

		if (DateUtils.isSameMonth(start, end) && DateUtils.isBeforeOrEqualDay(start, end)) {

			int weekNum = DateUtils.getDay(start, Calendar.DAY_OF_WEEK);
			int startDayNum = DateUtils.getDay(start, Calendar.DAY_OF_MONTH);
			int endDayNum = DateUtils.getDay(end, Calendar.DAY_OF_MONTH);

			results[0] = endDayNum - startDayNum + 1;// 总天数

			for (; startDayNum <= endDayNum; startDayNum++) {
				if (weekNum == 1) {
					if (++weekNum == 8) {
						weekNum = 1;
					}
					continue;
				}
				if (weekNum == 7) { // 星期六
					if (++weekNum == 8) {
						weekNum = 1;
					}
					results[2]++;
					continue;
				}
				results[1]++;
				if (++weekNum == 8) {
					weekNum = 1;
				}
			}
		}
		System.out.println("start:" + start + " end:" + end + " " + results[0] + " " + results[1] + " " + " " + results[2]);
		return results;
	}

	/** 计算同年同月的日期相差的天数 */
	public static int differDaysOfSameMonth(Date vacEnd, Date vacStart) {
		return DateUtils.getDay(vacEnd, Calendar.DAY_OF_MONTH) - DateUtils.getDay(vacStart, Calendar.DAY_OF_MONTH);
	}

	/** 只比较天数，忽略时间,start是否在end之前或者相等 */
	public static boolean isBeforeOrEqualDay(Date start, Date end) {
		return DateUtils.isBeforeDay(start, end) || DateUtils.isEqualDay(start, end);
	}

	/** 只比较天数，忽略时间，start是否在end之前 */
	public static boolean isBeforeDay(Date start, Date end) {

		if (DateUtils.isAfter(start, end, Calendar.YEAR)) {
			return false;
		}
		if (DateUtils.isBefore(start, end, Calendar.YEAR)) {
			return true;
		}
		// 同年
		if (DateUtils.isAfter(start, end, Calendar.MONTH)) {
			return false;
		}
		if (DateUtils.isBefore(start, end, Calendar.MONTH)) {
			return true;
		}
		// 同月
		if (DateUtils.isAfter(start, end, Calendar.DAY_OF_MONTH) || DateUtils.isEqual(start, end, Calendar.DAY_OF_MONTH)) {
			return false;
		}
		return true;
	}

	/** 只比较天数，忽略时间，start是否和end相对等 */
	public static boolean isEqualDay(Date start, Date end) {
		return DateUtils.isEqual(start, end, Calendar.YEAR) && DateUtils.isEqual(start, end, Calendar.MONTH) && DateUtils.isEqual(start, end, Calendar.DAY_OF_MONTH);
	}

	/** 只比较天数，忽略时间，start是否在end之后 */
	public static boolean isAfterDay(Date start, Date end) {
		return !DateUtils.isBeforeDay(start, end) && !DateUtils.isEqualDay(start, end);
	}

	/** 只比较天数，忽略时间，start是否在end之后或者相等 */
	public static boolean isAfterOrEqualDay(Date start, Date end) {
		return DateUtils.isAfterDay(start, end) || DateUtils.isEqualDay(start, end);
	}

	private static boolean isBefore(Date start, Date end, int type) {
		return DateUtils.getDay(start, type) < DateUtils.getDay(end, type);
	}

	private static boolean isEqual(Date start, Date end, int type) {
		return DateUtils.getDay(start, type) == DateUtils.getDay(end, type);
	}

	private static boolean isAfter(Date start, Date end, int type) {
		return DateUtils.getDay(start, type) > DateUtils.getDay(end, type);
	}

	/**
	 * 数组下标 0:按天算的应出勤天数 ；1：周一到周五的应出勤天数
	 * 
	 * @param isOneRest
	 * @param firstAttendDate
	 * @param lastAttendDate
	 * @param yfDate
	 * @return
	 */
	public static int[] getShouldAttendDays(boolean isOneRest, Date firstAttendDate, Date lastAttendDate, Date yfDate) {

		int[] result = { 0, 0 };// 0:按天算的应出勤天数 ；1：周一到周五的应出勤天数
		Date lastDay = DateUtils.lastDay(yfDate);
		Date firstDay = DateUtils.firstDay(yfDate);
		if (firstAttendDate != null && DateUtils.isSameMonth(yfDate, firstAttendDate)) {
			firstDay = firstAttendDate;
		}
		if (lastAttendDate != null && DateUtils.isSameMonth(yfDate, lastAttendDate)) {
			lastDay = lastAttendDate;
		}
		int[] temp = null;
		// 没有假期
		temp = DateUtils.getCountsBetweenDate(firstDay, lastDay);
		result[0] = temp[1];
		result[1] = temp[1];
		if (isOneRest) {
			result[0] += temp[2];// 单休=双休+周六
		}
		return result;
	}

}
