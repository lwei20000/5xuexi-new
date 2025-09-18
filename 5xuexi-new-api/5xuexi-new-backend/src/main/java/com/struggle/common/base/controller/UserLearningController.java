package com.struggle.common.base.controller;

import com.struggle.common.core.web.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学习记录控制器
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Tag(name= "学习记录信息管理",description = "UserLearningController")
@RestController
@RequestMapping("/api/base/user-learning")
public class UserLearningController extends BaseController {


}
