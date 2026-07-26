package com.personrecipe.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜谱列表卡片字段，供小程序虚拟列表渲染。
 */
@Data
@Builder
public class RecipeListItemVO {

	private Long id;
	private String name;
	private String description;
	private String coverImageUrl;
	private Integer servings;
	private Integer cookTimeMinutes;
	private LocalDateTime updatedAt;
}
