package com.personrecipe.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecipeImage {

	private Long id;
	private Long recipeId;
	private String imageUrl;
	private String caption;
	private Integer isCover;
	private Integer sortOrder;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
