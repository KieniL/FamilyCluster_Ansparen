package com.kienast.ansparen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kienast.ansparen.model.Amount;
import com.kienast.ansparen.model.Category;


@Repository
public interface AmountRespository extends JpaRepository<Amount, Long> {

	@Query("SELECT DISTINCT category FROM Amount")
	List<Category> findDistinctCategories();
	
}


