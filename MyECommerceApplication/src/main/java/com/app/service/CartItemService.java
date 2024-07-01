package com.app.service;

import java.util.List;

import com.app.entities.CartItem;
import com.app.payloads.CartItemDto;

public interface CartItemService {
	
	// 根據使用者電子信箱查詢購物車主表，並使用商品變體的識別碼新增購物車清單，回傳該購物車清單列表
	List<CartItemDto> addCartItemByUserEmail(String userEmail, String sku, Integer quantity);

	// 根據使用者電子信箱查詢所有的購物車清單
	List<CartItemDto> getAllCartItemsByUserEmail(String userEmail);

	// 根據購物車清單裡面的商品變體識別碼去修改購物車明細的數量，回傳更新後的選擇數量
	CartItem updateCartItemQuantityBySkuAndUserEmail(Integer quantity, String sku, String userEmail);
	
	// 根據購物車清單裡面的商品變體識別碼去刪除整筆購物車明細
	List<CartItemDto> deleteCartItemBySkuAndUserEmail(String sku, String userEmail);
}
