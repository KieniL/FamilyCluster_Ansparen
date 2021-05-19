package com.kienast.ansparen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.kienast.ansparen.model.Category;
import com.kienast.ansparen.repository.CategoryRespository;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	private CategoryRespository categoryRespository;

	@Override
	public Category getCategoryByDescription(String description) {
		Optional<Category> optCategory = categoryRespository.findAll().stream().filter(item -> item.getDescription().equals(description)).findAny();
		if(optCategory.isPresent()){
			return optCategory.get();
		}else{
			return null;
		}
	}

	@Override
	public Category getCategoryById(Long id) {
		try {
			if(categoryRespository.findById(id).isPresent()){
				return categoryRespository.findById(id).get();
			}else{
				return null;
			}
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
		
	}
}
