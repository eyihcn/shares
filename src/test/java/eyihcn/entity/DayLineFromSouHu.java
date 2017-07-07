package eyihcn.entity;

public class DayLineFromSouHu extends BaseEntity<Integer> {

	private static final long serialVersionUID = 5115664585479747954L;
	private Integer id;
	private String sharesCode; // 股票编码
	private String date;// 日期
	private float openingPrice; // 开盘价
	private float closingPrice; // 收盘价
	private float fluctuation;// 涨跌额 正数代表涨，负数代表跌
	private float fluctuationPercent;// 涨跌百分比 %
	private float highestPrice; // 最高价
	private float lowestPrice; // 最低价
	private int volume; // 成交量（手）
	private float tradedAmount; // 成交金额（万）
	private float turnoverRate;// 换手率 %

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

	public float getOpeningPrice() {
		return openingPrice;
	}

	public void setOpeningPrice(float openingPrice) {
		this.openingPrice = openingPrice;
	}

	public float getClosingPrice() {
		return closingPrice;
	}

	public void setClosingPrice(float closingPrice) {
		this.closingPrice = closingPrice;
	}

	public float getFluctuation() {
		return fluctuation;
	}

	public void setFluctuation(float fluctuation) {
		this.fluctuation = fluctuation;
	}

	public float getFluctuationPercent() {
		return fluctuationPercent;
	}

	public void setFluctuationPercent(float fluctuationPercent) {
		this.fluctuationPercent = fluctuationPercent;
	}

	public float getHighestPrice() {
		return highestPrice;
	}

	public void setHighestPrice(float highestPrice) {
		this.highestPrice = highestPrice;
	}

	public float getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(float lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public float getTradedAmount() {
		return tradedAmount;
	}

	public void setTradedAmount(float tradedAmount) {
		this.tradedAmount = tradedAmount;
	}

	public float getTurnoverRate() {
		return turnoverRate;
	}

	public void setTurnoverRate(float turnoverRate) {
		this.turnoverRate = turnoverRate;
	}

}
