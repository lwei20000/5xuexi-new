package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.system.entity.Doc;
import com.struggle.common.system.mapper.DocMapper;
import com.struggle.common.system.param.DocParam;
import com.struggle.common.system.service.DocService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档服务实现类
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:11
 */
@Service
public class DocServiceImpl extends ServiceImpl<DocMapper, Doc> implements DocService {

    public void getDocChildrenIds(Doc doc, List<Integer> childrenIds) {
        List<Doc> docs = baseMapper.selectList(Wrappers.<Doc>lambdaQuery().select(Doc::getDocId).eq(Doc::getParentId, doc.getDocId()));
        if(!CollectionUtils.isEmpty(docs)){
            for(Doc _doc : docs){
                childrenIds.add(_doc.getDocId());
                this.getDocChildrenIds(_doc,childrenIds);
            }
        }
    }

    @Override
    public List<Doc> listRel(DocParam param) {
        PageParam<Doc, DocParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        QueryWrapper<Doc> orderWrapper = page.getOrderWrapper();
        if(!CollectionUtils.isEmpty(param.getDefaultRoleIds())){
            orderWrapper.in("default_role_id",param.getDefaultRoleIds());
        }
        List<Doc> docs = baseMapper.selectList(orderWrapper);
        return docs;
    }

    @Override
    public boolean saveDoc(Doc doc) {

        return  baseMapper.insert(doc) > 0;
    }

    @Override
    public boolean updateDoc(Doc doc) {
        if(doc.getParentId() !=null && doc.getParentId() !=0){
            if(doc.getDocId().equals(doc.getParentId())){
                throw new BusinessException("上级文档不能是当前文档");
            }

            List<Integer> childrenIds = new ArrayList<>();
            this.getDocChildrenIds(doc,childrenIds);
            if(childrenIds.contains(doc.getParentId())){
                throw new BusinessException("上级文档不能是当前文档的子文档");
            }
        }

        return  baseMapper.update(doc,Wrappers.<Doc>lambdaUpdate()
                .set(Doc::getContent,doc.getContent())
                .eq(Doc::getDocId,doc.getDocId()))>0;
    }
}
