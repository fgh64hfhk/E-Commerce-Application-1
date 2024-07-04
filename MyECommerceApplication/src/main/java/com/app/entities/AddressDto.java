package com.app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

	private String address;
	private String pincode;
	
	private String deliveryName;
	private String deliveryTel;
	
	public AddressDto(Address address) {
		this.address = address.getAddress();
		this.pincode = address.getPincode();
		this.deliveryName = address.getDeliveryName();
		this.deliveryTel = address.getDeliveryTel();
	}
}
