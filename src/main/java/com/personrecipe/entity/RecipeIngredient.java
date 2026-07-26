package com.personrecipe.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecipeIngredient {

	private Long id;
	private Long recipeId;
	private String name;
	private String quantity;
	private String imageUrl;
	private String note;
	private Integer sortOrder;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
