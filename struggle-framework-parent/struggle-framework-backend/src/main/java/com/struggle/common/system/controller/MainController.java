package com.struggle.common.system.controller;

import com.struggle.common.core.web.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录认证控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:11
 */
@Tag(name = "登录认证",description = "MainController")
@RestController
@RequestMapping("/api")
public class MainController extends BaseController {

}
