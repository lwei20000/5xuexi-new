package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Dictionary;
import com.struggle.common.system.entity.DictionaryData;
import com.struggle.common.system.param.DictionaryParam;
import com.struggle.common.system.service.DictionaryDataService;
import com.struggle.common.system.service.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 字典控制器
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:03
 */
@Tag(name = "字典管理",description = "DictionaryController")
@RestController
@RequestMapping("/api/system/dictionary")
public class DictionaryController extends BaseController {
    @Resource
    private DictionaryService dictionaryService;
    @Resource
    private DictionaryDataService dictionaryDataService;

    @AuthorityAnnotation("sys:dict:list:208b9b11d0b943c99c45a77441ac9774")
    @OperationLog
    @Operation(method = "page",description="分页查询字典")
    @GetMapping("/page")
    public ApiResult<PageResult<Dictionary>> page(DictionaryParam param) {
        return success(dictionaryService.pageRel(param));
    }

    @AuthorityAnnotation("sys:dict:list:208b9b11d0b943c99c45a77441ac9774")
    @OperationLog
    @Operation(method = "list",description="查询全部字典")
    @GetMapping()
    public ApiResult<List<Dictionary>> list(DictionaryParam param) {
        PageParam<Dictionary, DictionaryParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        return success(dictionaryService.list(page.getOrderWrapper()));
    }

    @AuthorityAnnotation("sys:dict:save:7fd53be57a844cda8779dda31aa9e8a4")
    @Operation(method = "add",description="添加字典")
    @PostMapping()
    public ApiResult<?> add(@RequestBody Dictionary dictionary) {
        ValidatorUtil._validBean(dictionary);
        if (dictionaryService.count(new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getDictCode, dictionary.getDictCode())) > 0) {
            return fail("字典标识已存在");
        }
        if (dictionaryService.count(new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getDictName, dictionary.getDictName())) > 0) {
            return fail("字典名称已存在");
        }
        if (dictionaryService.save(dictionary)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:dict:update:326cd252f8e8430a8d7e7c0a35c5f7dc")
    @OperationLog
    @Operation(method = "update",description="修改字典")
    @PostMapping("/update")
    public ApiResult<?> update(@RequestBody Dictionary dictionary) {
        ValidatorUtil._validBean(dictionary);
        if (dictionaryService.count(new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getDictCode, dictionary.getDictCode()).ne(Dictionary::getDictId, dictionary.getDictId())) > 0) {
            return fail("字典标识已存在");
        }
        if (dictionaryService.count(new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getDictName, dictionary.getDictName()).ne(Dictionary::getDictId, dictionary.getDictId())) > 0) {
            return fail("字典名称已存在");
        }
        if (dictionaryService.update(dictionary, Wrappers.<Dictionary>lambdaUpdate().set(Dictionary::getComments,dictionary.getComments()).eq(Dictionary::getDictId,dictionary.getDictId()))) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:dict:remove:45e817c5f872460dba307cee60df2bc0")
    @OperationLog
    @Operation(method = "remove",description="删除字典")
    @PostMapping("/{id}")
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if(dictionaryDataService.count(Wrappers.<DictionaryData>lambdaQuery().eq(DictionaryData::getDictId,id))>0){
            return fail("先删除字典数据后，再删除字典");
        }
        if (dictionaryService.removeById(id)) {
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:dict:remove:45e817c5f872460dba307cee60df2bc0")
    @OperationLog
    @Operation(method = "removeBatch",description="批量删除字典")
    @PostMapping("/batch")
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        if(dictionaryDataService.count(Wrappers.<DictionaryData>lambdaQuery().in(DictionaryData::getDictId,ids))>0){
            return fail("先删除字典数据后，再删除字典");
        }
        if (dictionaryService.removeByIds(ids)) {
            return success("删除成功");
        }
        return fail("删除失败");
    }
}
