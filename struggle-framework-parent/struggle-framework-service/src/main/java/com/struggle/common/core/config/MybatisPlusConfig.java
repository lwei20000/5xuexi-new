package com.struggle.common.core.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.utils.CommonUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * MybatisPlus配置
 *
 * @author EleAdmin
 * @since 2018-02-22 11:29:28
 */
@Configuration
public class MybatisPlusConfig {

    @Resource
    private TenantProperties tenantProperties;
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 多租户插件配置
        TenantLineHandler tenantLineHandler = new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return getLoginUserTenantId();
            }
            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {

                return ThreadLocalContextHolder.getCloseTenant() || tenantProperties.getIgnoreTableNameMap().containsKey(tableName);
            }
        };
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(tenantLineHandler));

        //分表实现
        Map<String, Object> tableNameMap = tenantProperties.getTableNameMap();
        if(!CollectionUtils.isEmpty(tableNameMap)){
            DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
            dynamicTableNameInnerInterceptor.setTableNameHandler(new TenantTableNameHandler(tableNameMap));
            interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        }

        //针对 update 和 delete 语句 作用: 阻止恶意的全表更新删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        // 分页插件配置
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());

        return interceptor;
    }

    /**
     * 获取当前登录用户的租户id
     *
     * @return Integer
     */
    public Expression getLoginUserTenantId() {
        try {
            Integer tenantId = ThreadLocalContextHolder.getTenant();
            if (tenantId != null) {
              return new LongValue(tenantId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new NullValue();
    }

    /**
     * 按租户参数，组成动态表名
     */
     class TenantTableNameHandler implements TableNameHandler {
        //用于记录哪些表可以使用该月份动态表名处理器
        private Map<String, Object> _tableNameMap;
        //构造函数，构造动态表名处理器的时候，传递tableNames参数
        public TenantTableNameHandler(Map<String, Object> tableNameMap) {
            this._tableNameMap = tableNameMap;
        }

        //动态表名接口实现方法
        @Override
        public String dynamicTableName(String sql, String tableName) {
            Integer tenantId = ThreadLocalContextHolder.getTenant();
            if (tenantId !=null && this._tableNameMap.containsKey(tableName)){
                return CommonUtil.stringJoiner(tableName,tenantId.toString());  //表名增加租户ID
            }else{
                return tableName;   //表名原样返回
            }
        }
    }
}
