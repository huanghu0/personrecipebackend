package com.personrecipe.mapper;

import com.personrecipe.entity.RecipeImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeImageMapper {

	int insert(RecipeImage image);

	List<RecipeImage> findByRecipeId(@Param("recipeId") Long recipeId);

	int deleteByRecipeId(@Param("recipeId") Long recipeId);
}
