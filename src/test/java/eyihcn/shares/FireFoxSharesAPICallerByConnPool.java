package eyihcn.shares;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Maps;

public class FireFoxSharesAPICallerByConnPool {

	final Logger log = LoggerFactory.getLogger(FireFoxSharesAPICallerByConnPool.class);

	@Autowired
	@Qualifier("restTemplateConnPool")
	private RestTemplate restTemplate;

	private static final String hisHq = "http://q.stock.sohu.com/hisHq";
	private static final String shareCodePrefix = "cn_";

	/** 一次最多请求100条数据 */
	public String request(String sharesCode, String startDate, String endDate) {
		String generateHisHqUrl = generateHisHqUrl(sharesCode, startDate, endDate);
		log.debug("generateHisHqUrl: "+generateHisHqUrl);
		return restTemplate.getForObject(generateHisHqUrl, String.class);
	}

	/*
	 * http://q.stock.sohu.com/hisHq?
		code=cn_603388
		&start=20170224
		&end=20170604
		&stat=1
		&order=D
		&period=d
		&callback=historySearchHandler
		&rt=jsonp
		&r=0.7529744863517391&0.4995255315817104
	 */
	private String generateHisHqUrl(String sharesCode,String startDate, String endDate) {
		// "http://q.stock.sohu.com/hisHq?code=cn_002848&start=20160518&end=20161005&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r=0.7529744863517391&0.4995255315817104";

		Map<String, String> param = Maps.newHashMap();
		param.put("code", shareCodePrefix + sharesCode);
		param.put("start", startDate.replaceAll("-", ""));
		param.put("end", endDate.replaceAll("-", ""));
		param.put("stat", "1");
		param.put("order", "D");
		param.put("period", "d");
		param.put("callback", "historySearchHandler");
		param.put("rt", "jsonp");
		param.put("r", Math.random() + "");
		StringBuilder urlParam = new StringBuilder();
		for (Map.Entry<String, String> ent : param.entrySet()) {
			urlParam.append(ent.getKey()).append("=").append(ent.getValue()).append("&");
		}
		return hisHq + "?" + urlParam.toString() + Math.random();
	}
}
