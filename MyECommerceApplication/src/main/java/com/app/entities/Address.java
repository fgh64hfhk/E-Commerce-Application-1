package com.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long addressId;

	private String address;
	private String pincode;
	
	private String deliveryName;
	private String deliveryTel;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	@JsonBackReference
	private User user;
	
	public Address(AddressDto addressDto) {
		this.address = addressDto.getAddress();
		this.pincode = addressDto.getPincode();
		this.deliveryName = addressDto.getDeliveryName();
		this.deliveryTel = addressDto.getDeliveryTel();
	}
}
