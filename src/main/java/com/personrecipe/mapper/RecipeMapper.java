package com.personrecipe.mapper;

import com.personrecipe.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeMapper {

	int insert(Recipe recipe);

	List<Recipe> findAllByUserId(@Param("userId") Long userId);

	Recipe findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

	int update(Recipe recipe);

	int softDelete(@Param("id") Long id, @Param("userId") Long userId);
}
