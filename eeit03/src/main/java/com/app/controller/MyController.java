package com.app.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entities.Market;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class MyController {

	@Autowired
	private NamedParameterJdbcTemplate jdbc;

	@GetMapping("/market")
	public Boolean insertMarkets() throws Exception {

		// 取得網路連結點
		URL url = new URL("https://data.moa.gov.tw/Service/OpenData/FromM/FarmerMarketData.aspx");

		// 創建接收串流物件
		InputStreamReader ir = new InputStreamReader(url.openConnection().getInputStream());

		// 創建串流緩衝區
		BufferedReader reader = new BufferedReader(ir);

		// 創建字串緩衝接受物件
		String line;
		StringBuffer sb = new StringBuffer();

		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		reader.close();

		String json = sb.toString().replace("市集名稱", "name").replace("電話", "tel");

		ObjectMapper mapper = new ObjectMapper();
		List<Market> marketList = mapper.readValue(json, new TypeReference<List<Market>>() {

		});

		// 除錯機制
		for (Market market : marketList) {
			System.out.println(market.getName());
		}

		String sql = "INSERT INTO market (name,tel) VALUES (:name,:tel)";
		MapSqlParameterSource[] sources = new MapSqlParameterSource[marketList.size()];
		for (int i = 0; i < marketList.size(); i++) {
			sources[i] = new MapSqlParameterSource().addValue("name", marketList.get(i).getName()).addValue("tel",
					marketList.get(i).getTel());
		}
		jdbc.batchUpdate(sql, sources);

		return true;
	}

//	public Market query(@PathVariable Integer id) {
//		String sql;
//		Map<String, Object> map = new HashMap<>();
//		Market market = null;
//
//		return null;
//	}
}
