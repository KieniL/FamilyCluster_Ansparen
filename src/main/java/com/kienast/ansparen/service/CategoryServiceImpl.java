package com.kienast.ansparen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kienast.ansparen.model.Category;
import com.kienast.ansparen.repository.CategoryRespository;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Autowired
	private CategoryRespository categoryRespository;

	@Override
	public Category getCategoryByDescription(String description) {
		return categoryRespository.findAll().stream().filter(item -> item.getDescription().equals(description)).findAny().get();
	}

	@Override
	public Category getCategoryById(Long id) {
		try {
			return categoryRespository.findById(id).get();
		}catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
		
	}
}
