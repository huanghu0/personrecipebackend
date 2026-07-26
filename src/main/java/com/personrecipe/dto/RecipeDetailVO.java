package com.personrecipe.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecipeDetailVO {

	private Long id;
	private String name;
	private String description;
	private String coverImageUrl;
	private Integer servings;
	private Integer cookTimeMinutes;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<IngredientVO> ingredients;
	private List<StepVO> steps;
	private List<ImageVO> images;

	@Data
	@Builder
	public static class IngredientVO {
		private Long id;
		private String name;
		private String quantity;
		private String imageUrl;
		private String note;
		private Integer sortOrder;
	}

	@Data
	@Builder
	public static class StepVO {
		private Long id;
		private Integer stepNumber;
		private String description;
		private String imageUrl;
		private Integer sortOrder;
	}

	@Data
	@Builder
	public static class ImageVO {
		private Long id;
		private String imageUrl;
		private String caption;
		private Boolean cover;
		private Integer sortOrder;
	}
}
