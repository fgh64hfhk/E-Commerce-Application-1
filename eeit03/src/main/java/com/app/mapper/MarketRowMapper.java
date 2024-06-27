package com.app.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.app.entities.Market;

public class MarketRowMapper implements RowMapper<Market> {

	@Override
	public Market mapRow(ResultSet rs, int rowNum) throws SQLException {
		Market market = new Market();
		market.setId(rs.getInt("id"));
		market.setName(rs.getString("name"));
		market.setTel(rs.getString("tel"));
		return market;
	}

}
