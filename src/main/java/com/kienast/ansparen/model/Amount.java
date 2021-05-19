package com.kienast.ansparen.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "amount")
public class Amount extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;
	
	
	@NotBlank
	@Column(name = "date", columnDefinition = "timestamp", nullable = false)
	private Timestamp date;
	
	@NotBlank
	@Column(name = "amount", nullable = false)
    private Double amount;
	
	
	@NotBlank
	@ManyToOne
    @JoinColumn(name="category_id", nullable=false)
    private Category category;
	
	public Amount() {
		
	}


	public Amount(Timestamp date, Double amount, Category category) {
		this.date = date;
		this.amount = amount;
		this.category = category;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Timestamp getDate() {
		return date;
	}


	public void setDate(Timestamp date) {
		this.date = date;
	}


	public Double getAmount() {
		return amount;
	}


	public void setAmount(Double amount) {
		this.amount = amount;
	}


	public Category getCategory() {
		return category;
	}


	public void setCategory(Category category) {
		this.category = category;
	}
	
	
}
