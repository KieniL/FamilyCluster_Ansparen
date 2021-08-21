package com.kienast.ansparen.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import javax.validation.Valid;

import com.kienast.ansparen.exception.BadRequestException;
import com.kienast.ansparen.exception.NotAuthorizedException;
import com.kienast.ansparen.model.Amount;
import com.kienast.ansparen.model.TokenVerificationResponse;
import com.kienast.ansparen.rest.api.AnsparenApi;
import com.kienast.ansparen.rest.api.model.AnsparEntryModel;
import com.kienast.ansparen.rest.api.model.CategoryResponseModel;
import com.kienast.ansparen.service.AmountService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Mono;

@RestController
public class AnsparController implements AnsparenApi {

	@Autowired
	private AmountService amountService;

	private static Logger logger = LogManager.getLogger(AnsparController.class.getName());

	@Value("${logging.level.com.kienast.ansparservice}")
	private String loglevel;

	// Used for WebTemplate
	@Autowired
	private WebClient.Builder webClientBuilder;

	@Value("${authURL}")
	private String authURL;

	@Override
	@Operation(description = "addEntry")
	public ResponseEntity<AnsparEntryModel> addEntry(String JWT, String xRequestID, String SOURCE_IP,
			@Valid AnsparEntryModel ansparEntryModel) {

		initializeLogInfo(xRequestID, SOURCE_IP, "");
		logger.info("Got Request (Add Anspar Entry) for " + ansparEntryModel.getDescription());

		try {
			TokenVerificationResponse tokenResponse = new TokenVerificationResponse();

			logger.info("Call Authentication Microservice for JWT Verification");
			tokenResponse = webClientBuilder.build().post() // RequestMethod
					.uri(authURL + "/jwt").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).header("JWT", JWT)
					.header("X-Request-ID", xRequestID).header("SOURCE_IP", SOURCE_IP).retrieve() // run command
					.onStatus(HttpStatus::is4xxClientError, response -> {
						return Mono.error(new NotAuthorizedException(String.format("Failed JWT Verification")));
					}).bodyToMono(TokenVerificationResponse.class) // convert Response
					.block(); // do as Synchronous call

			initializeLogInfo(xRequestID, SOURCE_IP, tokenResponse.getUserId());
			logger.info("Added userId to log");
		} catch (Exception e) {

			logger.error("Error on verifiying jwt" + e.getMessage());
			throw new BadRequestException("Verification Failure");
		}

		Amount entity = null;
		AnsparEntryModel response = new AnsparEntryModel();
		try {

			logger.info("Try to add Entry for " + ansparEntryModel.getDescription());
			entity = amountService.addAmount(ansparEntryModel.getValue().doubleValue(),
					Timestamp.valueOf(ansparEntryModel.getDate().atTime(LocalTime.now())), ansparEntryModel.getDescription());

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
	@Operation(description = "get Entries")
	
	public ResponseEntity<List<CategoryResponseModel>> getCategories(String JWT, String xRequestID, String SOURCE_IP) {

		List<CategoryResponseModel> categoryResponse = null;

		initializeLogInfo(xRequestID, SOURCE_IP, "");
		logger.info("Got Request (Get Entries)");

		try {
			TokenVerificationResponse tokenResponse = new TokenVerificationResponse();

			logger.info("Call Authentication Microservice for JWT Verification");
			tokenResponse = webClientBuilder.build().post() // RequestMethod
					.uri(authURL + "/jwt").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).header("JWT", JWT)
					.header("X-Request-ID", xRequestID).header("SOURCE_IP", SOURCE_IP).retrieve() // run command
					.onStatus(HttpStatus::is4xxClientError, response -> {
						return Mono.error(new NotAuthorizedException(String.format("Failed JWT Verification")));
					}).bodyToMono(TokenVerificationResponse.class) // convert Response
					.block(); // do as Synchronous call

			initializeLogInfo(xRequestID, SOURCE_IP, tokenResponse.getUserId());
			logger.info("Added userId to log");

			categoryResponse = amountService.getAmounts();

		} catch (Exception e) {

			logger.error("Error on verifiying jwt" + e.getMessage());
		}

		logger.info("Successfully loaded entries");

		return ResponseEntity.ok(categoryResponse);
	}

	@Override
	@Operation(description = "get Entry by categoryName")
	public ResponseEntity<CategoryResponseModel> getCategory(String description, String JWT, String xRequestID,
			String SOURCE_IP) {

		CategoryResponseModel categoryResponse = null;
		initializeLogInfo(xRequestID, SOURCE_IP, "");
		logger.info("Got Request (Get Entry) for name " + description);

		try {
			TokenVerificationResponse tokenResponse = new TokenVerificationResponse();

			logger.info("Call Authentication Microservice for JWT Verification");
			tokenResponse = webClientBuilder.build().post() // RequestMethod
					.uri(authURL + "/jwt").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).header("JWT", JWT)
					.header("X-Request-ID", xRequestID).header("SOURCE_IP", SOURCE_IP).retrieve() // run command
					.onStatus(HttpStatus::is4xxClientError, response -> {
						return Mono.error(new NotAuthorizedException(String.format("Failed JWT Verification")));
					}).bodyToMono(TokenVerificationResponse.class) // convert Response
					.block(); // do as Synchronous call

			initializeLogInfo(xRequestID, SOURCE_IP, tokenResponse.getUserId());
			logger.info("Added userId to log");

			categoryResponse = amountService.getAmountsByCategory(description);

		} catch (Exception e) {

			logger.error("Error on verifiying jwt " + e.getMessage());
		}

		logger.info("Successfully loaded entry on " + description);

		return ResponseEntity.ok(categoryResponse);
	}

	private void initializeLogInfo(String requestId, String sourceIP, String userId) {
		MDC.put("SYSTEM_LOG_LEVEL", loglevel);
		MDC.put("REQUEST_ID", requestId);
		MDC.put("SOURCE_IP", sourceIP);
		MDC.put("USER_ID", userId);
	}

}
