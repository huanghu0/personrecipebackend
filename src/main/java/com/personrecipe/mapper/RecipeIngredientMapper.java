package com.personrecipe.mapper;

import com.personrecipe.entity.RecipeIngredient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeIngredientMapper {

	int insertBatch(@Param("list") List<RecipeIngredient> list);

	List<RecipeIngredient> findByRecipeId(@Param("recipeId") Long recipeId);

	int deleteByRecipeId(@Param("recipeId") Long recipeId);
}
