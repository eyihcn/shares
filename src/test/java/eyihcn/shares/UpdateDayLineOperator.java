package eyihcn.shares;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import eyihcn.dao.DayLineFromSouHuRepository;
import eyihcn.entity.DayLineFromSouHu;
import eyihcn.entity.HisHqEntity;
import eyihcn.entity.SharesEntity;
import eyihcn.shares.utlis.DateUtils;

public class UpdateDayLineOperator {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool;
	protected DayLineFromSouHuRepository dayLineFromSouHuDao;

	protected Gson gson = new Gson();
	
	protected int pullCounts;// 抓取的日线数据总数

	public UpdateDayLineOperator() {
		super();
	}
	
	public UpdateDayLineOperator(FireFoxSharesAPICallerByConnPool fireFoxSharesAPICallerByConnPool, DayLineFromSouHuRepository dayLineFromSouHuDao) {
		super();
		this.fireFoxSharesAPICallerByConnPool = fireFoxSharesAPICallerByConnPool;
		this.dayLineFromSouHuDao = dayLineFromSouHuDao;
		this.pullCounts = 0 ;
	}

	/**
	 * 
	 * @param sharesEntityList
	 * @param startDay 格式yyyy-MM-dd
	 * @param endDay 格式yyyy-MM-dd
	 */
	public void pull(List<SharesEntity>sharesEntityList, String startDay, String endDay ) {
		try {
			
			if (CollectionUtils.isEmpty(sharesEntityList)) {
				log.info("no data , sharesEntityList is empty !");
				return;
			}
		
			Date today = new Date();
			// 若开始时间为空，则默认为股票上市时间
			if (StringUtils.isNotBlank(startDay)) {
				// 如果拉取时间区间在未来时间，则终止
				if (DateUtils.isAfter(DateUtils.parseDate(startDay), today)){
					log.info("no data , sharesEntityList is empty !");
					return;
				}
			}
			// 若未指定结束日期 或者 结束日期在当天之后，默认为当天为结束日期
			if (StringUtils.isBlank(endDay) 
					|| DateUtils.isAfter(DateUtils.parseDate(endDay), today)) {
				endDay = DateUtils.formatDate(today);
			}
			String cursorDateStr = new String(endDay);
			Date cursorDate = DateUtils.parseDate(cursorDateStr);
			while (DateUtils.isWeekendDay(cursorDate)) {
				if (!isEqualOrBetween(cursorDateStr, startDay, endDay)) {
					log.info("日期 : " + cursorDateStr + " 不在限定日期区间范围之内：" + startDay + "  ~ " + endDay);
					return;
				}
				log.info(cursorDateStr + " 深沪A股休市！");
				// 向前推一天
				cursorDate = DateUtils.decrementDay(cursorDate);
				cursorDateStr = DateUtils.formatDate(cursorDate);
			}
			
			if (!isEqualOrBetween(cursorDateStr, startDay, endDay)) {
				log.info("日期 : " + cursorDateStr + " 不在限定日期区间范围之内：" + startDay + "  ~ " + endDay);
				return;
			}
			endDay = cursorDateStr;
			// 一个线程处理100只股票
			for (SharesEntity sharesEntity : sharesEntityList) {
				String sharesCode = sharesEntity.getSharesCode();
				if (StringUtils.isBlank(sharesCode)) {
					log.info("error : 股票代码为空！" + sharesEntity.getClass() + " id=" + sharesEntity.getId());
					continue;
				}
				// 检查当前的最新数据
				if (dayLineFromSouHuDao.checkExistsByDateAndSharesCode(cursorDateStr, sharesCode)) {
					log.info(sharesEntity.getSharesNameCn() + "[" + sharesCode + "]" + cursorDateStr + "的股票数据已经拉取！");
					continue;
				}

				String startDate = null;
				String endDate = new String(cursorDateStr);

				int perSharesCounts = 0; // 每只股票，在抓取时间区间内的计数
				P: while (true) {
					// 每次查询最多100条数据
					startDate = getStartdateFromEnd(endDate);
					if (StringUtils.isNotBlank(startDay)) {
						if (StringUtils.isNotBlank(endDay)) {
							if (startDay.compareTo(endDate) > 0 || endDay.compareTo(startDate) < 0) {
								log.info("待抓取日期区间 : " + startDate + "  ~ " + endDate + " 不在限定日期区间范围之内：" + startDay + "  ~ " + endDay);
								break;
							} else {
								if (endDay.compareTo(endDate) < 0) {
									endDate = endDay;
								}
								if (startDay.compareTo(startDate) > 0) {
									startDate = startDay;
								}
							}
						} else {
							if (startDay.compareTo(endDate) > 0) {
								log.info("待抓取日期区间 : " + startDate + "  ~ " + endDate + " 不在限定日期区间范围之内：" + startDay + "  ~ " + endDay);
								break;
							} else {
								if (startDay.compareTo(startDate) > 0) {
									startDate = startDay;
								}
							}
						}
					} else {
						if (StringUtils.isNotBlank(endDay)) {
							if (endDay.compareTo(startDate) < 0) {
								log.info("待抓去日期区间 : " + startDate + "  ~ " + endDate + " 不在限定日期区间范围之内：" + startDay + "  ~ " + endDay);
								break;
							} else {
								if (endDay.compareTo(endDate) < 0) {
									endDate = endDay;
								}
							}
						}
					}

					List<List<String>> hq = getDayLineFromSouHu(sharesEntity, startDate, endDate);
					if (CollectionUtils.isEmpty(hq)) {
						log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDate + "  ~ " + endDate + " ;拉取数据量为 0 ，结束");
						break;
					}
					perSharesCounts += hq.size();
					for (List<String> dayLineData : hq) {
						String tempDate = dayLineData.get(0);
						// 若当前日期 已有数据，结束拉取
						if (dayLineFromSouHuDao.checkExistsByDateAndSharesCode(tempDate, sharesCode)) {
							log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDate + "  ~ " + endDate + "; 中断拉取 ，" + tempDate + "的股票数据已经拉取！");
							break P;
						}
						try {
							saveDayLineEntity(sharesCode, dayLineData);
						} catch (Exception e) {
							e.printStackTrace();
							log.info("++++++saveDayLineEntity++++++ sharesCode : " + sharesCode + " 日期区间：" + startDate + "  ~ " + endDate);
						}
					}
					log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDate + "  ~ " + endDate + "; 拉取成功！ 共拉取日线数据： " + hq.size());
					endDate = DateUtils.formatDate(DateUtils.decrementDay(DateUtils.parseDate(startDate)));
					// log.info(Thread.currentThread().getName() + " 当前线程sleep 2
					// 秒");
//					TimeUnit.SECONDS.sleep(1);
				}
				log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDay + "  ~ " + endDay + "; 拉取成功！ 共拉取日线数据： " + perSharesCounts);
				this.pullCounts += perSharesCounts;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private DayLineFromSouHu saveDayLineEntity(String sharesCode, List<String> dayLineData) {

		/*
		 * 日期 0: 2017-07-07 ; 开盘价 1:38.30 ; 收盘价 2:39.33; 涨跌额 3:0.85;
		 * 涨跌幅 4: 2.21%; 最低 5: 38.09; 最高 6:39.49; 成交量(手) 7:36235;
		 * 成交金额(万)8 : 14097.93; 换手率 9: 14.49%
		 */
		DayLineFromSouHu dayLineFromSouHu = new DayLineFromSouHu();
		dayLineFromSouHu.setSharesCode(sharesCode);
		dayLineFromSouHu.setDate(dayLineData.get(0));
		dayLineFromSouHu.setOpeningPrice(Double.valueOf(dayLineData.get(1)));
		dayLineFromSouHu.setClosingPrice(Double.valueOf(dayLineData.get(2)));
		dayLineFromSouHu.setFluctuation(Double.valueOf(dayLineData.get(3)));
		dayLineFromSouHu.setFluctuationPercent(Double.valueOf(dayLineData.get(4).replace("%", "")));
		dayLineFromSouHu.setLowestPrice(Double.valueOf(dayLineData.get(5)));
		dayLineFromSouHu.setHighestPrice(Double.valueOf(dayLineData.get(6)));
		dayLineFromSouHu.setVolume(Integer.valueOf(dayLineData.get(7)));
		dayLineFromSouHu.setTradedAmount(Double.valueOf(dayLineData.get(8)));
		String turnoverRate = dayLineData.get(9);
		if ("-".equals(turnoverRate)) {
			dayLineFromSouHu.setTurnoverRate(0);
		} else {
			dayLineFromSouHu.setTurnoverRate(Double.valueOf(turnoverRate.replace("%", "")));
		}
		dayLineFromSouHuDao.save(dayLineFromSouHu);
		return dayLineFromSouHu;
	}

	private List<List<String>> getDayLineFromSouHu(SharesEntity sharesEntity, String startDate, String endDate) {

		String sharesCode = sharesEntity.getSharesCode();
		log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDate + "  ~ " + endDate);
		String respStr = null;
		try {
			respStr = fireFoxSharesAPICallerByConnPool.request(sharesCode, startDate, endDate);
		} catch (Exception e1) {
			e1.printStackTrace();
			log.info("######getDayLineFromSouHu###### sharesCode : " + sharesCode + " 日期区间：" + startDate + "  ~ " + endDate);
			return Collections.emptyList();
		}
		if ("historySearchHandler({})\n".equals(respStr)) {
			log.info("拉取目标：" + sharesEntity.getSharesNameCn() + "(" + sharesCode + ") ； 日期区间：" + startDate + "  ~ " + endDate + " ; 查询无数据,停止拉取");
			return Collections.emptyList();
		}
		String jsonStr = respStr.replace("historySearchHandler([", "").replace("])", "");
		HisHqEntity en = gson.fromJson(jsonStr, HisHqEntity.class);
		return en.getHq();
	}

	private String getStartdateFromEnd(String endDate) throws ParseException {
		int count = 0;
		Date parseDate = DateUtils.parseDate(endDate);
		while (true) {
			if (count == 100) {
				break;
			}
			parseDate = DateUtils.decrementDay(parseDate);
			if (DateUtils.isWeekendDay(parseDate)) {
				continue;
			}
			count++;
		}
		return DateUtils.formatDate(parseDate);
	}

	private boolean isEqualOrBetween(String cursorDateStr, String startDay, String endDay) {

		if (StringUtils.isBlank(startDay) && StringUtils.isBlank(endDay)) {
			return true;
		}
		if (StringUtils.isNotBlank(startDay) && startDay.compareTo(cursorDateStr) > 0) {
			return false;
		}
		if (StringUtils.isNotBlank(endDay) && endDay.compareTo(cursorDateStr) < 0) {
			return false;
		}
		return true;
	}

	public int getPullCounts() {
		return pullCounts;
	}

}
