package eyihcn.entity;

import java.util.List;

public class HisHqEntity {

	private Integer status;
	private List<List<String>> hq;
	private String code;
	private List<Object> stat;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<List<String>> getHq() {
		return hq;
	}

	public void setHq(List<List<String>> hq) {
		this.hq = hq;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<Object> getStat() {
		return stat;
	}

	public void setStat(List<Object> stat) {
		this.stat = stat;
	}

}
