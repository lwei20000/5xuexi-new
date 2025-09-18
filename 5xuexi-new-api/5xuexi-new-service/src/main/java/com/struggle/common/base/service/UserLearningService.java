package com.struggle.common.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.base.entity.Courseware;
import com.struggle.common.base.entity.UserLearning;

public interface UserLearningService extends IService<UserLearning> {

    void updateLearning(Courseware courseware);
}
