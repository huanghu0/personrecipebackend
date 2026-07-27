package com.personrecipe.controller;

import com.personrecipe.common.ApiResponse;
import com.personrecipe.dto.CreateRecipeRequest;
import com.personrecipe.dto.CreateRecipeResponse;
import com.personrecipe.dto.RecipeDetailVO;
import com.personrecipe.dto.RecipeListItemVO;
import com.personrecipe.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

	private final RecipeService recipeService;

	/**
	 * 菜谱列表：返回当前用户全部菜谱（需登录）。
	 * 不分页，前端用虚拟列表渲染。
	 */
	@GetMapping
	public ApiResponse<List<RecipeListItemVO>> list() {
		return ApiResponse.ok(recipeService.listAll());
	}

	/**
	 * 菜谱详情：基本信息 + 食材 + 做法步骤（需登录）。
	 */
	@GetMapping("/{id}")
	public ApiResponse<RecipeDetailVO> detail(@PathVariable Long id) {
		return ApiResponse.ok(recipeService.getDetail(id));
	}

	/**
	 * 更新菜谱（需登录）。请求体与创建相同，整体替换食材 / 步骤关联。
	 * 仅删除数据库旧关联，七牛云对象暂不删除；详情查询只返回当前仍关联的图片。
	 */
	@PutMapping("/{id}")
	public ApiResponse<RecipeDetailVO> update(@PathVariable Long id,
											  @Valid @RequestBody CreateRecipeRequest request) {
		return ApiResponse.ok(recipeService.update(id, request));
	}

	/**
	 * 创建菜谱（需登录）。
	 * 建议先调用 /api/files/upload 上传图片，再把返回的 url 填入本接口。
	 */
	@PostMapping
	public ApiResponse<CreateRecipeResponse> create(@Valid @RequestBody CreateRecipeRequest request) {
		return ApiResponse.ok(recipeService.create(request));
	}

	/**
	 * 删除菜谱（软删除，需登录）。仅标记 deleted_at，数据与图片保留。
	 */
	@DeleteMapping("/{id}")
	public ApiResponse<Void> delete(@PathVariable Long id) {
		recipeService.delete(id);
		return ApiResponse.ok(null);
	}
}
