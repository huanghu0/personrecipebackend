package com.personrecipe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRecipeResponse {

	private Long id;
	private String name;
}
