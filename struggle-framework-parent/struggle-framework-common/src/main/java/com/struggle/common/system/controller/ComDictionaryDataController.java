package com.struggle.common.system.controller;

import com.struggle.common.core.annotation.NoLoginCheck;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.system.entity.DictionaryData;
import com.struggle.common.system.param.DictionaryDataParam;
import com.struggle.common.system.service.DictionaryDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 字典数据控制器
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@Tag(name = "COM字典数据管理",description = "ComDictionaryDataController")
@RestController
@RequestMapping("/api/system/dictionary-data")
public class ComDictionaryDataController extends BaseController {
    @Resource
    private DictionaryDataService dictionaryDataService;
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private RedisUtil redisUtil;

    @Operation(method="list",description="查询全部字典数据")
    @GetMapping()
    @NoLoginCheck
    public ApiResult<List<DictionaryData>> list(DictionaryDataParam param) {
        String key =  CommonUtil.stringJoiner(configProperties.getDictionaryPrefix(),param.getDictCode());
        if(StringUtils.hasText(param.getParentDictDataCode())){
            key = CommonUtil.stringJoiner(key,"@_@",param.getParentDictDataCode());
        }
        List<DictionaryData> list = redisUtil.getList(key, DictionaryData.class);
        if(!CollectionUtils.isEmpty(list)){
            return success(list);
        }
        list = dictionaryDataService.listRel(param);
        if(!CollectionUtils.isEmpty(list)){
            for(DictionaryData dictionaryData:list){
                dictionaryData.setDeleted(null);
                dictionaryData.setCreateTime(null);
                dictionaryData.setUpdateTime(null);
            }
            redisUtil.set(key, JSONUtil.toJSONString(list),24*60*60L);
        }
        return success(list);
    }
}
