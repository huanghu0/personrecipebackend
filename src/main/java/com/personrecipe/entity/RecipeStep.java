package com.personrecipe.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecipeStep {

	private Long id;
	private Long recipeId;
	private Integer stepNumber;
	private String description;
	private String imageUrl;
	private Integer sortOrder;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
