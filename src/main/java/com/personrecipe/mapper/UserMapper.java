package com.personrecipe.mapper;

import com.personrecipe.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

	User findByOpenid(@Param("openid") String openid);

	/**
	 * 插入后回填自增主键到 user.id。
	 */
	int insert(User user);

	int update(User user);

	int clearSessionKey(@Param("id") Long id);
}
