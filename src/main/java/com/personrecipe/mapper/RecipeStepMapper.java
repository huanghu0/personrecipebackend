package com.personrecipe.mapper;

import com.personrecipe.entity.RecipeStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeStepMapper {

	int insertBatch(@Param("list") List<RecipeStep> list);

	List<RecipeStep> findByRecipeId(@Param("recipeId") Long recipeId);

	int deleteByRecipeId(@Param("recipeId") Long recipeId);
}
