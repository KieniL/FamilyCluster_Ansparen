package com.kienast.ansparen.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.List;

import javax.validation.Valid;

import com.kienast.ansparen.exception.BadRequestException;
import com.kienast.ansparen.model.Amount;
import com.kienast.ansparen.rest.api.AnsparenApi;
import com.kienast.ansparen.rest.api.model.AnsparEntryModel;
import com.kienast.ansparen.rest.api.model.CategoryResponseModel;
import com.kienast.ansparen.service.AmountService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class AnsparController implements AnsparenApi {

	@Autowired
	private AmountService amountService;

	private static Logger logger = LogManager.getLogger(AnsparController.class.getName());

	@Value("${logging.level.com.kienast.ansparservice}")
	private String loglevel;

	@Override
	@Operation(description = "addEntry")
	public ResponseEntity<AnsparEntryModel> addEntry(String JWT, String xRequestID, String SOURCE_IP,
			@Valid AnsparEntryModel ansparEntryModel) {

		initializeLogInfo(xRequestID, SOURCE_IP, "1");
		logger.info("Got Request (Add Anspar Entry) for " + ansparEntryModel.getDescription());

		Amount entity = null;
		AnsparEntryModel response = new AnsparEntryModel();
		try {

			logger.info("Try to add Entry for " + ansparEntryModel.getDescription());
			entity = amountService.addAmount(ansparEntryModel.getValue().doubleValue(),
					Timestamp.valueOf(ansparEntryModel.getDate().atStartOfDay()), ansparEntryModel.getDescription());

		} catch (Exception e) {
			logger.error("Failed at setting entry for for " + ansparEntryModel.getDescription() + ": " + e.getMessage());
			throw new BadRequestException("Adding");
		}

		response.setDate(entity.getDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate());
		response.setDescription(entity.getCategory().getDescription());
		response.setValue(BigDecimal.valueOf(entity.getAmount()));

		logger.info("Adding of Entry for " + ansparEntryModel.getDescription() + " was successfull");

		return ResponseEntity.ok(response);
	}

	@Override
	@Operation(description = "get Entry by categoryName")
	public ResponseEntity<List<CategoryResponseModel>> getCategories(String JWT, String xRequestID, String SOURCE_IP) {

		initializeLogInfo(xRequestID, SOURCE_IP, "1");
		logger.info("Got Request (Get Entries)");

		List<CategoryResponseModel> response = amountService.getAmounts();

		logger.info("Successfully loaded entries");

		return ResponseEntity.ok(response);
	}

	@Override
	@Operation(description = "get Entries")
	public ResponseEntity<CategoryResponseModel> getCategory(String description, String JWT, String xRequestID,
			String SOURCE_IP) {

		initializeLogInfo(xRequestID, SOURCE_IP, "1");
		logger.info("Got Request (Get Entry) for name " + description);

		CategoryResponseModel response = amountService.getAmountsByCategory(description);

		logger.info("Successfully loaded entry on " + description);

		return ResponseEntity.ok(response);
	}

	private void initializeLogInfo(String requestId, String sourceIP, String userId) {
		MDC.put("SYSTEM_LOG_LEVEL", loglevel);
		MDC.put("REQUEST_ID", requestId);
		MDC.put("SOURCE_IP", sourceIP);
		MDC.put("USER_ID", userId);
	}

}
