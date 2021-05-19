package com.kienast.ansparen.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kienast.ansparen.model.Amount;
import com.kienast.ansparen.model.Category;
import com.kienast.ansparen.repository.AmountRespository;
import com.kienast.ansparen.rest.api.model.AmountEntryModel;
import com.kienast.ansparen.rest.api.model.CategoryResponseModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmountServiceImpl implements AmountService {
	
	@Autowired
	private AmountRespository amountRespository;
	
	@Autowired
	private CategoryService categoryService;

	@Override
	public Amount addAmount(Double amountInput, Timestamp date, String categoryDescription) {
		
		Amount entity = null;
		
		
		try {
			Category category = categoryService.getCategoryByDescription(categoryDescription);
			
			entity = amountRespository.save(new Amount(date, amountInput, category));
		}catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return entity;
	}

	@Override
	public CategoryResponseModel getAmountsByCategory(String categoryDescription) {
		
		CategoryResponseModel response = null;
		try {
			
			Category category = categoryService.getCategoryByDescription(categoryDescription);
			
			List<Amount> amounts = GetAmountsByCategory(category);
			
			//Order the entries by date
			amounts.sort((o1,o2) -> o1.getDate().compareTo(o2.getDate()));
			
			response = new CategoryResponseModel();
			
			response.setDescription(category.getDescription());
			List<AmountEntryModel> entries = new ArrayList<>();
			
			for(Amount a: amounts) {
				AmountEntryModel amountEntryModel = new AmountEntryModel();
				
				amountEntryModel.setAmount(new BigDecimal(a.getAmount(), MathContext.DECIMAL64));
				amountEntryModel.setDate(a.getDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate());
				
				entries.add(amountEntryModel);
			}
			response.setEntries(entries);
		}catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return response;	
	}

	

	@Override
	public List<CategoryResponseModel> getAmounts() {
		List<CategoryResponseModel> response = new ArrayList<>();
		try {
			
			List<Category> categories = amountRespository.findDistinctCategories();
			
			
			Map<Timestamp, Double> totalPerMonth = new LinkedHashMap<>();
			
			for (Category category : categories) {
				
				CategoryResponseModel responseCategory = new CategoryResponseModel();
				responseCategory.setDescription(category.getDescription());
				List<AmountEntryModel> entries = new ArrayList<>();
				
				List<Amount> amounts = GetAmountsByCategory(category);
				
				//Order the entries by date
				amounts.sort((o1,o2) -> o1.getDate().compareTo(o2.getDate()));
				
				for(Amount a: amounts) {
					AmountEntryModel amountEntryModel = new AmountEntryModel();
					
					
					if(totalPerMonth.get(a.getDate()) != null) {
						totalPerMonth.put(a.getDate(), totalPerMonth.get(a.getDate()) +  a.getAmount());
					}else {
						totalPerMonth.put(a.getDate(), a.getAmount());
					}
					
					amountEntryModel.setAmount(new BigDecimal(a.getAmount(), MathContext.DECIMAL64));
					amountEntryModel.setDate(a.getDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate());
					
					entries.add(amountEntryModel);
				}
				responseCategory.setEntries(entries);
				response.add(responseCategory);
			}
			CategoryResponseModel total = new CategoryResponseModel();
			
			total.setDescription("Gesamt");
			
			List<AmountEntryModel> entries = new ArrayList<>();
			
			
			for(Map.Entry<Timestamp, Double> entry : totalPerMonth.entrySet()) {
				AmountEntryModel amountEntryModel = new AmountEntryModel();
				
				amountEntryModel.setDate(entry.getKey().toInstant().atZone(ZoneId.of("UTC")).toLocalDate());
				amountEntryModel.setAmount(new BigDecimal(entry.getValue(), MathContext.DECIMAL64));
				entries.add(amountEntryModel);
			}
			total.setEntries(entries);
			response.add(total);
			
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return response;
	}
	
	private List<Amount> GetAmountsByCategory(Category category) {
		List<Amount> amounts = amountRespository.findAll().stream().filter(item -> item.getCategory().equals(category)).collect(Collectors.toList());
		return amounts;
	}

}
