-- =============================================
-- 个人菜谱小程序 - 完整建表脚本
-- 引擎: InnoDB | 字符集: utf8mb4
-- =============================================

-- ---------------------------------------------
-- 1. 用户表（微信登录）
-- ---------------------------------------------

CREATE TABLE `users` (
                         `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '内部用户ID',
                         `openid`        VARCHAR(64)  NOT NULL COMMENT '微信openid，同一小程序内唯一',
                         `unionid`       VARCHAR(64)  DEFAULT NULL COMMENT '微信unionid，多端打通时用',
                         `nickname`      VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
                         `avatar_url`    VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
                         `session_key`   VARCHAR(128) DEFAULT NULL COMMENT '微信session_key，仅服务端使用',
                         `last_login_at` DATETIME     DEFAULT NULL COMMENT '最近登录时间',
                         `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_users_openid` (`openid`),
                         UNIQUE KEY `uk_users_unionid` (`unionid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';


-- ---------------------------------------------
-- 2. 菜谱主表
-- ---------------------------------------------
CREATE TABLE `recipes` (
                           `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜谱ID',
                           `user_id`            BIGINT UNSIGNED NOT NULL COMMENT '所属用户',
                           `name`               VARCHAR(100)   NOT NULL COMMENT '菜名',
                           `description`        VARCHAR(500)   DEFAULT NULL COMMENT '列表卡片简短说明',
                           `cover_image_url`    VARCHAR(512)   DEFAULT NULL COMMENT '列表封面图URL',
                           `servings`           INT UNSIGNED   DEFAULT NULL COMMENT '几人份',
                           `cook_time_minutes`  INT UNSIGNED   DEFAULT NULL COMMENT '烹饪耗时（分钟）',
                           `deleted_at`         DATETIME       DEFAULT NULL COMMENT '软删除时间，非空表示已删除',
                           `created_at`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_at`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_recipes_user_id` (`user_id`),
                           KEY `idx_recipes_user_updated` (`user_id`, `updated_at`),
                           KEY `idx_recipes_deleted_at` (`deleted_at`),
                           CONSTRAINT `fk_recipes_user`
                               FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
                                   ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜谱主表';

-- ---------------------------------------------
-- 3. 食材表
-- ---------------------------------------------
CREATE TABLE `recipe_ingredients` (
                                      `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '食材行ID',
                                      `recipe_id`  BIGINT UNSIGNED NOT NULL COMMENT '所属菜谱',
                                      `name`       VARCHAR(100)   NOT NULL COMMENT '食材名称',
                                      `quantity`   VARCHAR(50)    DEFAULT NULL COMMENT '数量，如 2个 / 200g / 适量',
                                      `image_url`  VARCHAR(512)   DEFAULT NULL COMMENT '食材图片URL',
                                      `note`       VARCHAR(200)   DEFAULT NULL COMMENT '备注，如去皮、室温',
                                      `sort_order` INT UNSIGNED   NOT NULL DEFAULT 0 COMMENT '展示顺序，从小到大',
                                      `created_at` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updated_at` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_recipe_ingredients_recipe_id` (`recipe_id`),
                                      KEY `idx_recipe_ingredients_recipe_sort` (`recipe_id`, `sort_order`),
                                      CONSTRAINT `fk_recipe_ingredients_recipe`
                                          FOREIGN KEY (`recipe_id`) REFERENCES `recipes` (`id`)
                                              ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜谱食材';

-- ---------------------------------------------
-- 4. 做法步骤表
-- ---------------------------------------------
CREATE TABLE `recipe_steps` (
                                `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '步骤ID',
                                `recipe_id`    BIGINT UNSIGNED NOT NULL COMMENT '所属菜谱',
                                `step_number`  INT UNSIGNED   NOT NULL COMMENT '第几步，从1开始',
                                `description`  TEXT           NOT NULL COMMENT '步骤文字说明',
                                `image_url`    VARCHAR(512)   DEFAULT NULL COMMENT '步骤配图URL',
                                `sort_order`   INT UNSIGNED   NOT NULL DEFAULT 0 COMMENT '展示顺序，一般等于step_number',
                                `created_at`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_at`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_recipe_steps_recipe_id` (`recipe_id`),
                                KEY `idx_recipe_steps_recipe_sort` (`recipe_id`, `sort_order`),
                                UNIQUE KEY `uk_recipe_steps_recipe_step` (`recipe_id`, `step_number`),
                                CONSTRAINT `fk_recipe_steps_recipe`
                                    FOREIGN KEY (`recipe_id`) REFERENCES `recipes` (`id`)
                                        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜谱做法步骤';

-- ---------------------------------------------
-- 5. 成品图表
-- ---------------------------------------------
CREATE TABLE `recipe_images` (
                                 `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '图片ID',
                                 `recipe_id`  BIGINT UNSIGNED NOT NULL COMMENT '所属菜谱',
                                 `image_url`  VARCHAR(512)   NOT NULL COMMENT '成品图URL',
                                 `caption`    VARCHAR(200)   DEFAULT NULL COMMENT '图片说明',
                                 `is_cover`   TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '是否封面：1是 0否',
                                 `sort_order` INT UNSIGNED   NOT NULL DEFAULT 0 COMMENT '展示顺序',
                                 `created_at` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updated_at` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_recipe_images_recipe_id` (`recipe_id`),
                                 KEY `idx_recipe_images_recipe_sort` (`recipe_id`, `sort_order`),
                                 CONSTRAINT `fk_recipe_images_recipe`
                                     FOREIGN KEY (`recipe_id`) REFERENCES `recipes` (`id`)
                                         ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜谱成品图';
