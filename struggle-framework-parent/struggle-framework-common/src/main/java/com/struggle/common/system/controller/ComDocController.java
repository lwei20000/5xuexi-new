package com.struggle.common.system.controller;

import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.system.entity.Doc;
import com.struggle.common.system.entity.Role;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.param.DocParam;
import com.struggle.common.system.service.DocService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:02
 */
@Tag(name = "COM文档管理",description = "ComDocController")
@RestController
@RequestMapping("/api/system/doc")
public class ComDocController extends BaseController {
    @Resource
    private DocService docService;

    @OperationLog
    @Operation(method="list",description="查询全部文档")
    @GetMapping()
    public ApiResult<List<Doc>> list(DocParam param) {
        User user = ThreadLocalContextHolder.getUser();
        if(CollectionUtils.isEmpty(user.getRoles())){
            return success(new ArrayList<>());
        }
        List<Integer> defaultRoleIds = new ArrayList<>();
        for(Role role:user.getRoles()){
            if(role.getSystemDefault() !=null){
                defaultRoleIds.add(role.getSystemDefault());
            }
        }
        if(CollectionUtils.isEmpty(defaultRoleIds)){
            return success(new ArrayList<>());
        }
        param.setDefaultRoleIds(defaultRoleIds);
        return success(docService.listRel(param));
    }
}
