package com.personrecipe.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Recipe {

	private Long id;
	private Long userId;
	private String name;
	private String description;
	private String coverImageUrl;
	private Integer servings;
	private Integer cookTimeMinutes;
	private LocalDateTime deletedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
