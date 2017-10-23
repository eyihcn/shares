package eyihcn.entity;

import eyihcn.base.entity.BaseEntity;

public class DayLineFromSouHu extends BaseEntity<Integer> {

	private static final long serialVersionUID = 5115664585479747954L;
	private Integer id;
	private String sharesCode; // 股票编码
	private String date;// 日期
	private double openingPrice; // 开盘价
	private double closingPrice; // 收盘价
	private double fluctuation;// 涨跌额 正数代表涨，负数代表跌
	private double fluctuationPercent;// 涨跌百分比 %
	private double highestPrice; // 最高价
	private double lowestPrice; // 最低价
	private int volume; // 成交量（手）
	private double tradedAmount; // 成交金额（万）
	private double turnoverRate;// 换手率 %

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getSharesCode() {
		return sharesCode;
	}

	public void setSharesCode(String sharesCode) {
		this.sharesCode = sharesCode;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getOpeningPrice() {
		return openingPrice;
	}

	public void setOpeningPrice(double openingPrice) {
		this.openingPrice = openingPrice;
	}

	public double getClosingPrice() {
		return closingPrice;
	}

	public void setClosingPrice(double closingPrice) {
		this.closingPrice = closingPrice;
	}

	public double getFluctuation() {
		return fluctuation;
	}

	public void setFluctuation(double fluctuation) {
		this.fluctuation = fluctuation;
	}

	public double getFluctuationPercent() {
		return fluctuationPercent;
	}

	public void setFluctuationPercent(double fluctuationPercent) {
		this.fluctuationPercent = fluctuationPercent;
	}

	public double getHighestPrice() {
		return highestPrice;
	}

	public void setHighestPrice(double highestPrice) {
		this.highestPrice = highestPrice;
	}

	public double getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(double lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public double getTradedAmount() {
		return tradedAmount;
	}

	public void setTradedAmount(double tradedAmount) {
		this.tradedAmount = tradedAmount;
	}

	public double getTurnoverRate() {
		return turnoverRate;
	}

	public void setTurnoverRate(double turnoverRate) {
		this.turnoverRate = turnoverRate;
	}

}
