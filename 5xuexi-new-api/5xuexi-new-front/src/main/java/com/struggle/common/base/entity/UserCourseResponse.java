package com.struggle.common.base.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.system.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 此表封装学生用户的所有相关信息
 * @Auther: weiliang
 * @Date: 2025/4/11 23:34
 * @Description:
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="UserCourseResponse", description = "学生用户的所有相关信息")
public class UserCourseResponse {
    @Schema(description="用户")
    private User user;

    @Schema(description="用户专业")
    private UserMajor userMajor;

    @Schema(description="用户课程")
    List<UserCourse> userCourseList;

}
