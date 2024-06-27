package com.app.service;

import com.app.payloads.CartDto;

public interface CartService {

	// 根據使用者的電子信箱查詢購物車主表
	CartDto findCartByUserEmail(String email);
	
	// 根據使用者的電子信箱查詢購物車主表的總金額
	Integer findTotalPriceByUserEmail(String email);
	
	// 根據使用者的電子信箱查詢購物車主表的運送費用
	Integer findDeliveryPriceByUserEmail(String email);
}
