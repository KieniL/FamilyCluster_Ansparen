package com.kienast.ansparen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kienast.ansparen.model.Category;


@Repository
public interface CategoryRespository extends JpaRepository<Category, Long> {

}
