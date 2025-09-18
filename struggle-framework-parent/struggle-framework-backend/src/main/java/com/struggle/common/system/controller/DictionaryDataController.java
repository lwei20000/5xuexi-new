package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Dictionary;
import com.struggle.common.system.entity.DictionaryData;
import com.struggle.common.system.param.DictionaryDataParam;
import com.struggle.common.system.service.DictionaryDataService;
import com.struggle.common.system.service.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 字典数据控制器
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@Tag(name = "字典数据管理",description = "DictionaryDataController" )
@RestController
@RequestMapping("/api/system/dictionary-data")
public class DictionaryDataController extends BaseController {
    @Resource
    private DictionaryDataService dictionaryDataService;
    @Resource
    private DictionaryService dictionaryService;
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private RedisUtil redisUtil;

    @AuthorityAnnotation("sys:dict:list:208b9b11d0b943c99c45a77441ac9774")
    @OperationLog
    @Operation(method = "page",description="分页查询字典数据")
    @GetMapping("/page")
    public ApiResult<PageResult<DictionaryData>> page(DictionaryDataParam param) {
        return success(dictionaryDataService.pageRel(param));
    }

    // TODO 查询全部List,在common包里面

    @AuthorityAnnotation("sys:dict:save:7fd53be57a844cda8779dda31aa9e8a4")
    @OperationLog
    @Operation(method = "add",description="添加字典数据")
    @PostMapping()
    public ApiResult<?> add(@RequestBody DictionaryData dictionaryData) {
        ValidatorUtil._validBean(dictionaryData);
        if (dictionaryDataService.count(new LambdaQueryWrapper<DictionaryData>().eq(DictionaryData::getParentId, dictionaryData.getParentId()).eq(DictionaryData::getDictId, dictionaryData.getDictId()).eq(DictionaryData::getDictDataName, dictionaryData.getDictDataName())) > 0) {
            return fail("字典数据名称已存在");
        }
        if (dictionaryDataService.count(new LambdaQueryWrapper<DictionaryData>().eq(DictionaryData::getDictId, dictionaryData.getDictId()).eq(DictionaryData::getDictDataCode, dictionaryData.getDictDataCode())) > 0) {
            return fail("字典数据标识已存在");
        }
        if (dictionaryDataService.save(dictionaryData)) {
            //删除缓存字典
            this.delByDictIdRedis(dictionaryData.getDictId());
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:dict:update:326cd252f8e8430a8d7e7c0a35c5f7dc")
    @OperationLog
    @Operation(method = "update",description="修改字典数据")
    @PostMapping(value = "/update")
    public ApiResult<?> update(@RequestBody DictionaryData dictionaryData) {
        ValidatorUtil._validBean(dictionaryData);
        if (dictionaryDataService.count(new LambdaQueryWrapper<DictionaryData>().eq(DictionaryData::getDictId, dictionaryData.getDictId()).eq(DictionaryData::getParentId, dictionaryData.getParentId()).eq(DictionaryData::getDictDataName, dictionaryData.getDictDataName()).ne(DictionaryData::getDictDataId, dictionaryData.getDictDataId())) > 0) {
            return fail("字典数据名称已存在");
        }
        if (dictionaryDataService.count(new LambdaQueryWrapper<DictionaryData>().eq(DictionaryData::getDictId, dictionaryData.getDictId()).eq(DictionaryData::getDictDataCode, dictionaryData.getDictDataCode()).ne(DictionaryData::getDictDataId, dictionaryData.getDictDataId())) > 0) {
            return fail("字典数据标识已存在");
        }
        if(dictionaryData.getParentId() !=null && dictionaryData.getParentId() !=0) {
            if (dictionaryData.getDictDataId().equals(dictionaryData.getParentId())) {
                throw new BusinessException("上级字典不能是当前字典");
            }
        }
        if (dictionaryDataService.update(dictionaryData, Wrappers.<DictionaryData>lambdaUpdate().set(DictionaryData::getParentId,dictionaryData.getParentId()).set(DictionaryData::getComments,dictionaryData.getComments()).eq(DictionaryData::getDictDataId,dictionaryData.getDictDataId()))) {
            //删除缓存字典
            this.delByDictIdRedis(dictionaryData.getDictId());
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:dict:remove:45e817c5f872460dba307cee60df2bc0")
    @OperationLog
    @Operation(method = "remove",description="删除字典数据")
    @PostMapping("/{id}")
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if(dictionaryDataService.count(Wrappers.<DictionaryData>lambdaQuery().eq(DictionaryData::getParentId,id))>0){
            return fail("先删除子字典数据后，再删除");
        }
        if (dictionaryDataService.removeById(id)) {
            this.delByDictDataIdRedis(id);
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:dict:remove:45e817c5f872460dba307cee60df2bc0")
    @OperationLog
    @Operation(method = "removeBatch",description="批量删除字典数据")
    @PostMapping("/batch")
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        if(dictionaryDataService.count(Wrappers.<DictionaryData>lambdaQuery().in(DictionaryData::getParentId,ids))>0){
            return fail("先删除子字典数据后，再删除");
        }
        if (dictionaryDataService.removeByIds(ids)) {
            this.delByDictDataIdsRedis(ids);
            return success("删除成功");
        }
        return fail("删除失败");
    }

    private void delByDictIdRedis(Integer dictId){
        Dictionary byId = dictionaryService.getById(dictId);
        if(byId != null){
            redisUtil.dels(CommonUtil.stringJoiner(configProperties.getDictionaryPrefix(),byId.getDictCode()),false);
        }
    }

    private void delByDictDataIdRedis(Integer dictDataId){
        DictionaryData byId = dictionaryDataService.getById(dictDataId);
        if(byId != null){
            this.delByDictIdRedis(byId.getDictId());
        }
    }

    private void delByDictDataIdsRedis(List<Integer> dictDataIds){
        List<DictionaryData> byIds = dictionaryDataService.listByIds(dictDataIds);
        if(!CollectionUtils.isEmpty(byIds)){
            for(DictionaryData dictionaryData:byIds){
                this.delByDictIdRedis(dictionaryData.getDictId());
            }
        }
    }
}
