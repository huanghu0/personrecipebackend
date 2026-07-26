package com.personrecipe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateRecipeRequest {

	@NotBlank(message = "菜名不能为空")
	@Size(max = 100, message = "菜名最多100字")
	private String name;

	@Size(max = 500, message = "说明最多500字")
	private String description;

	/**
	 * 成品图 / 封面图（同一张）。
	 */
	@NotBlank(message = "成品图不能为空")
	@Size(max = 512, message = "图片地址过长")
	private String coverImageUrl;

	private Integer servings;

	private Integer cookTimeMinutes;

	@NotEmpty(message = "至少添加一种食材")
	@Valid
	private List<IngredientItem> ingredients;

	@NotEmpty(message = "至少添加一个做法步骤")
	@Valid
	private List<StepItem> steps;

	@Data
	public static class IngredientItem {

		@NotBlank(message = "食材名称不能为空")
		@Size(max = 100, message = "食材名称最多100字")
		private String name;

		@Size(max = 50, message = "数量最多50字")
		private String quantity;

		@Size(max = 512, message = "食材图片地址过长")
		private String imageUrl;

		@Size(max = 200, message = "备注最多200字")
		private String note;

		private Integer sortOrder;
	}

	@Data
	public static class StepItem {

		/**
		 * 第几步；不传则按列表顺序自动从 1 开始。
		 */
		private Integer stepNumber;

		@NotBlank(message = "步骤说明不能为空")
		private String description;

		@Size(max = 512, message = "步骤图片地址过长")
		private String imageUrl;

		private Integer sortOrder;
	}
}
