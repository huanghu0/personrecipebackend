package com.personrecipe.service;

import com.personrecipe.common.BusinessException;
import com.personrecipe.common.UserContext;
import com.personrecipe.dto.CreateRecipeRequest;
import com.personrecipe.dto.CreateRecipeResponse;
import com.personrecipe.dto.RecipeDetailVO;
import com.personrecipe.dto.RecipeListItemVO;
import com.personrecipe.entity.Recipe;
import com.personrecipe.entity.RecipeImage;
import com.personrecipe.entity.RecipeIngredient;
import com.personrecipe.entity.RecipeStep;
import com.personrecipe.mapper.RecipeImageMapper;
import com.personrecipe.mapper.RecipeIngredientMapper;
import com.personrecipe.mapper.RecipeMapper;
import com.personrecipe.mapper.RecipeStepMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecipeService {

	private final RecipeMapper recipeMapper;
	private final RecipeIngredientMapper recipeIngredientMapper;
	private final RecipeStepMapper recipeStepMapper;
	private final RecipeImageMapper recipeImageMapper;

	@Transactional
	public CreateRecipeResponse create(CreateRecipeRequest request) {
		Long userId = UserContext.requireUserId();

		Recipe recipe = new Recipe();
		recipe.setUserId(userId);
		recipe.setName(request.getName().trim());
		recipe.setDescription(request.getDescription());
		recipe.setCoverImageUrl(request.getCoverImageUrl());
		recipe.setServings(request.getServings());
		recipe.setCookTimeMinutes(request.getCookTimeMinutes());
		recipeMapper.insert(recipe);

		insertCoverImage(recipe.getId(), request.getCoverImageUrl());
		recipeIngredientMapper.insertBatch(buildIngredients(recipe.getId(), request.getIngredients()));
		recipeStepMapper.insertBatch(buildSteps(recipe.getId(), request.getSteps()));

		return CreateRecipeResponse.builder()
				.id(recipe.getId())
				.name(recipe.getName())
				.build();
	}

	/**
	 * 更新菜谱：覆盖基本信息，并整体替换食材 / 步骤 / 成品图关联。
	 * 仅删除数据库中的旧关联记录，本地图片文件保留。
	 */
	@Transactional
	public RecipeDetailVO update(Long id, CreateRecipeRequest request) {
		Long userId = UserContext.requireUserId();
		Recipe existing = recipeMapper.findByIdAndUserId(id, userId);
		if (existing == null) {
			throw new BusinessException(404, "菜谱不存在");
		}

		existing.setName(request.getName().trim());
		existing.setDescription(request.getDescription());
		existing.setCoverImageUrl(request.getCoverImageUrl());
		existing.setServings(request.getServings());
		existing.setCookTimeMinutes(request.getCookTimeMinutes());
		recipeMapper.update(existing);

		// 先删库中旧关联，再写入新关联；本地文件不动
		recipeIngredientMapper.deleteByRecipeId(id);
		recipeStepMapper.deleteByRecipeId(id);
		recipeImageMapper.deleteByRecipeId(id);

		insertCoverImage(id, request.getCoverImageUrl());
		recipeIngredientMapper.insertBatch(buildIngredients(id, request.getIngredients()));
		recipeStepMapper.insertBatch(buildSteps(id, request.getSteps()));

		return getDetail(id);
	}

	/**
	 * 软删除菜谱：仅置 deleted_at，食材 / 步骤 / 图片关联及本地文件均保留。
	 */
	@Transactional
	public void delete(Long id) {
		Long userId = UserContext.requireUserId();
		int affected = recipeMapper.softDelete(id, userId);
		if (affected == 0) {
			throw new BusinessException(404, "菜谱不存在");
		}
	}

	/**
	 * 当前用户全部菜谱（未删除），按更新时间倒序。
	 * 不分页，由小程序虚拟列表自行优化渲染。
	 */
	public List<RecipeListItemVO> listAll() {
		Long userId = UserContext.requireUserId();
		return recipeMapper.findAllByUserId(userId).stream()
				.map(recipe -> RecipeListItemVO.builder()
						.id(recipe.getId())
						.name(recipe.getName())
						.description(recipe.getDescription())
						.coverImageUrl(recipe.getCoverImageUrl())
						.servings(recipe.getServings())
						.cookTimeMinutes(recipe.getCookTimeMinutes())
						.updatedAt(recipe.getUpdatedAt())
						.build())
				.toList();
	}

	/**
	 * 菜谱详情：基本信息 + 食材 + 步骤 + 成品图。
	 * 只能查看自己的菜谱。
	 */
	public RecipeDetailVO getDetail(Long id) {
		Long userId = UserContext.requireUserId();
		Recipe recipe = recipeMapper.findByIdAndUserId(id, userId);
		if (recipe == null) {
			throw new BusinessException(404, "菜谱不存在");
		}

		List<RecipeDetailVO.IngredientVO> ingredients = recipeIngredientMapper.findByRecipeId(id).stream()
				.map(item -> RecipeDetailVO.IngredientVO.builder()
						.id(item.getId())
						.name(item.getName())
						.quantity(item.getQuantity())
						.imageUrl(item.getImageUrl())
						.note(item.getNote())
						.sortOrder(item.getSortOrder())
						.build())
				.toList();

		List<RecipeDetailVO.StepVO> steps = recipeStepMapper.findByRecipeId(id).stream()
				.map(item -> RecipeDetailVO.StepVO.builder()
						.id(item.getId())
						.stepNumber(item.getStepNumber())
						.description(item.getDescription())
						.imageUrl(item.getImageUrl())
						.sortOrder(item.getSortOrder())
						.build())
				.toList();

		List<RecipeDetailVO.ImageVO> images = recipeImageMapper.findByRecipeId(id).stream()
				.map(item -> RecipeDetailVO.ImageVO.builder()
						.id(item.getId())
						.imageUrl(item.getImageUrl())
						.caption(item.getCaption())
						.cover(item.getIsCover() != null && item.getIsCover() == 1)
						.sortOrder(item.getSortOrder())
						.build())
				.toList();

		return RecipeDetailVO.builder()
				.id(recipe.getId())
				.name(recipe.getName())
				.description(recipe.getDescription())
				.coverImageUrl(recipe.getCoverImageUrl())
				.servings(recipe.getServings())
				.cookTimeMinutes(recipe.getCookTimeMinutes())
				.createdAt(recipe.getCreatedAt())
				.updatedAt(recipe.getUpdatedAt())
				.ingredients(ingredients)
				.steps(steps)
				.images(images)
				.build();
	}

	private void insertCoverImage(Long recipeId, String coverImageUrl) {
		RecipeImage coverImage = new RecipeImage();
		coverImage.setRecipeId(recipeId);
		coverImage.setImageUrl(coverImageUrl);
		coverImage.setIsCover(1);
		coverImage.setSortOrder(0);
		recipeImageMapper.insert(coverImage);
	}

	private List<RecipeIngredient> buildIngredients(Long recipeId, List<CreateRecipeRequest.IngredientItem> items) {
		List<RecipeIngredient> list = new ArrayList<>(items.size());
		for (int i = 0; i < items.size(); i++) {
			CreateRecipeRequest.IngredientItem item = items.get(i);
			RecipeIngredient ingredient = new RecipeIngredient();
			ingredient.setRecipeId(recipeId);
			ingredient.setName(item.getName().trim());
			ingredient.setQuantity(item.getQuantity());
			ingredient.setImageUrl(item.getImageUrl());
			ingredient.setNote(item.getNote());
			ingredient.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : i);
			list.add(ingredient);
		}
		return list;
	}

	private List<RecipeStep> buildSteps(Long recipeId, List<CreateRecipeRequest.StepItem> items) {
		List<RecipeStep> list = new ArrayList<>(items.size());
		Set<Integer> stepNumbers = new HashSet<>();
		for (int i = 0; i < items.size(); i++) {
			CreateRecipeRequest.StepItem item = items.get(i);
			int stepNumber = item.getStepNumber() != null ? item.getStepNumber() : (i + 1);
			if (stepNumber < 1) {
				throw new BusinessException("步骤序号必须从 1 开始");
			}
			if (!stepNumbers.add(stepNumber)) {
				throw new BusinessException("步骤序号不能重复: " + stepNumber);
			}

			RecipeStep step = new RecipeStep();
			step.setRecipeId(recipeId);
			step.setStepNumber(stepNumber);
			step.setDescription(item.getDescription().trim());
			step.setImageUrl(item.getImageUrl());
			step.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : stepNumber);
			list.add(step);
		}
		return list;
	}
}
