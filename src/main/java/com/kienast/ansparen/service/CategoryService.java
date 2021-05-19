package com.kienast.ansparen.service;

import com.kienast.ansparen.model.Category;

public interface CategoryService {

	Category getCategoryByDescription(String description);
	
	Category getCategoryById(Long id);
}
