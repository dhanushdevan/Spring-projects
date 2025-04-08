package com.iagami.Paymentgateway.Dto;

public class PaymentGetData {

	public Integer amount;
	public String currency;
	public String recepitId;
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getRecepitId() {
		return recepitId;
	}
	public void setRecepitId(String recepitId) {
		this.recepitId = recepitId;
	}
	
}
