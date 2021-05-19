package com.kienast.ansparen.service;

import java.sql.Timestamp;
import java.util.List;

import com.kienast.ansparen.model.Amount;
import com.kienast.ansparen.rest.api.model.CategoryResponseModel;

public interface AmountService {
	
	Amount addAmount(Double amountInput, Timestamp date, String categoryDescription);
	
	
	CategoryResponseModel getAmountsByCategory(String categoryDescription);
	
	
	List<CategoryResponseModel> getAmounts();
	

}
