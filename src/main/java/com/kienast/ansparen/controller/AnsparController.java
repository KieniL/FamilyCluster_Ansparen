package com.kienast.ansparen.controller;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.kienast.ansparen.exception.BadRequestException;
import com.kienast.ansparen.model.Amount;
import com.kienast.ansparen.rest.api.AnsparenApi;
import com.kienast.ansparen.rest.api.model.AnsparEntryModel;
import com.kienast.ansparen.rest.api.model.CategoryResponseModel;
import com.kienast.ansparen.service.AmountService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class AnsparController implements AnsparenApi {
	
	@Autowired
	private AmountService amountService;
	
	

	@Override
	@Operation(description = "addEntry")
	public ResponseEntity<AnsparEntryModel> addEntry(@Valid AnsparEntryModel ansparEntryModel) {
		
		
		Amount entity= null;
		AnsparEntryModel response = new AnsparEntryModel();
		try {
			
			entity = amountService.addAmount(ansparEntryModel.getValue().doubleValue(), Timestamp.valueOf(ansparEntryModel.getDate().atStartOfDay()), ansparEntryModel.getDescription());
		}catch(Exception e) {
			throw new BadRequestException("Adding");
		}
		
		response.setDate(entity.getDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate());
		response.setDescription(entity.getCategory().getDescription());
		response.setValue(new BigDecimal(entity.getAmount(), MathContext.DECIMAL64));
		
		return ResponseEntity.ok(response);
	}

	


	@Override
	@Operation(description = "get Entry by categoryName")
	public ResponseEntity<CategoryResponseModel> getCategory(String description) {
		CategoryResponseModel response = amountService.getAmountsByCategory(description);
		
		return ResponseEntity.ok(response);
	}



	@Override
	@Operation(description = "get Entries")
	public ResponseEntity<List<CategoryResponseModel>> getCategories() {
		List<CategoryResponseModel> response = amountService.getAmounts();
		
		return ResponseEntity.ok(response);
	}

	

}
