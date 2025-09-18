<!-- 搜索表单 -->
<template>
  <el-form
    label-width="77px"
    class="ele-form-search"
    @keyup.enter.native="search"
    @submit.native.prevent
  >
    <el-row :gutter="15">
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="应用类型:">
         <app-type-select :params="{dictCode: 'app_type'}" :value="value" @input="inputsearch" ></app-type-select>
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="菜单名称:">
          <el-input clearable v-model="where.title" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="菜单地址:">
          <el-input clearable v-model="where.path" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="权限标识:">
          <el-input clearable v-model="where.authority" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <div class="ele-form-actions">
          <el-button
            type="primary"
            icon="el-icon-search"
            class="ele-btn-icon"
            @click="search"
          >
            查询
          </el-button>
          <el-button @click="reset">重置</el-button>
        </div>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
import AppTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';

  export default {
    components: {AppTypeSelect },
    props: {
       value: null,
    },
    data() {
      // 默认表单数据
      const defaultWhere = {
        appType:this.value,
        title: '',
        path: '',
        authority: ''
      };
      return {
        // 表单数据
        where: { ...defaultWhere }
      };
    },
    computed: {
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    methods: {
      /* 搜索 */
      search() {
        this.$emit('search', this.where);
      },
      /* 搜索 */
      inputsearch(val) {
        this.where.appType = val;
        this.$emit('search', this.where);
      },
      /*  重置 */
      reset() {
        this.where.title='';
        this.where.path='';
        this.where.authority='';
        this.$emit('search', this.where);
      }
    }
  };
</script>
