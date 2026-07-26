package com.personrecipe.service;

import com.personrecipe.common.BusinessException;
import com.personrecipe.common.UserContext;
import com.personrecipe.dto.UpdateProfileRequest;
import com.personrecipe.dto.UserInfoVO;
import com.personrecipe.entity.User;
import com.personrecipe.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserMapper userMapper;
	private final FileStorageService fileStorageService;

	public UserInfoVO getProfile() {
		return toUserInfoVO(requireCurrentUser());
	}

	/**
	 * 部分更新资料：可只改昵称、只改头像，或两者一起改。
	 */
	@Transactional
	public UserInfoVO updateProfile(UpdateProfileRequest request) {
		boolean updateNickname = request.getNickname() != null;
		boolean updateAvatar = request.getAvatarUrl() != null;
		if (!updateNickname && !updateAvatar) {
			throw new BusinessException("请至少提供昵称或头像");
		}

		User user = requireCurrentUser();
		if (updateNickname) {
			String nickname = request.getNickname().trim();
			if (!StringUtils.hasText(nickname)) {
				throw new BusinessException("昵称不能为空");
			}
			user.setNickname(nickname);
		}
		if (updateAvatar) {
			String avatarUrl = request.getAvatarUrl().trim();
			if (!StringUtils.hasText(avatarUrl)) {
				throw new BusinessException("头像地址不能为空");
			}
			user.setAvatarUrl(avatarUrl);
		}
		userMapper.update(user);
		return toUserInfoVO(userMapper.findById(user.getId()));
	}

	/**
	 * 单独修改昵称。
	 */
	@Transactional
	public UserInfoVO updateNickname(String nickname) {
		User user = requireCurrentUser();
		user.setNickname(nickname.trim());
		userMapper.update(user);
		return toUserInfoVO(userMapper.findById(user.getId()));
	}

	/**
	 * 单独上传头像文件并更新。
	 */
	@Transactional
	public UserInfoVO uploadAvatar(MultipartFile file) {
		String url = fileStorageService.storeImage(file);
		User user = requireCurrentUser();
		user.setAvatarUrl(url);
		userMapper.update(user);
		return toUserInfoVO(userMapper.findById(user.getId()));
	}

	private User requireCurrentUser() {
		Long userId = UserContext.requireUserId();
		User user = userMapper.findById(userId);
		if (user == null) {
			throw new BusinessException(404, "用户不存在");
		}
		return user;
	}

	private UserInfoVO toUserInfoVO(User user) {
		return UserInfoVO.builder()
				.id(user.getId())
				.nickname(user.getNickname())
				.avatarUrl(user.getAvatarUrl())
				.build();
	}
}
