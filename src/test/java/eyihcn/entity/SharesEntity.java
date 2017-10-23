package eyihcn.entity;

import eyihcn.base.entity.BaseEntity;

public class SharesEntity extends BaseEntity<Integer> {

	private static final long serialVersionUID = 6411905850616573153L;
	private Integer id;
	private String sharesCode; // 股票代码
	private String sharesNameCn; // 中文名称

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

	public String getSharesNameCn() {
		return sharesNameCn;
	}

	public void setSharesNameCn(String sharesNameCn) {
		this.sharesNameCn = sharesNameCn;
	}

}
