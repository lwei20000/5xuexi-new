<!-- 租户编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="860px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改租户' : '添加租户'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="上级租户:">
            <ele-tree-select
              clearable
              v-model="form.parentId"
              :data="tenantList"
              label-key="tenantName"
              value-key="tenantId"
              default-expand-all
              placeholder="请选择上级租户"
            />
          </el-form-item>
          <el-form-item label="租户类型:" prop="tenantType">
            <tenant-type-select :params="{dictCode: 'tenant_type'}" v-model="form.tenantType" />
          </el-form-item>
          <el-form-item label="租户代码:" prop="tenantCode">
            <el-input
              clearable
              disabled
              :maxlength="100"
              v-model="form.tenantCode"
              placeholder="请输入租户代码"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="租户名称:" prop="tenantName">
            <el-input
              clearable
              :maxlength="100"
              v-model="form.tenantName"
              placeholder="请输入租户名称"
            />
          </el-form-item>
          <el-form-item label="租户全称:" prop="tenantFullName">
            <el-input
              clearable
              disabled
              :maxlength="100"
              v-model="form.tenantFullName"
              placeholder="请输入租户全称"
            />
          </el-form-item>

          <el-form-item label="租户LOGO:" prop="picture">
            <com-image-upload v-model="form.picture" :limit="1"/>
          </el-form-item>
        </el-col>
      </el-row>





      <el-form-item label="租户简介:">
        <!-- 编辑器 -->
        <tinymce-editor
          ref="editor"
          v-model="form.comments"
          :init="{ height: 520 }"
        />
      </el-form-item>
    </el-form>
    <template v-slot:footer>
      <el-button @click="updateVisible(false)">取消</el-button>
      <el-button type="primary" @click="save" :loading="loading">
        保存
      </el-button>
    </template>
  </ele-modal>
</template>

<script>
import {addTenant, getTenant, updateTenant} from '@/api/system/tenant';
  import {clearDeep} from "@/api/common";
  import TenantTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  import ComImageUpload from '@/views/system/file/components/com-image-upload.vue';
  import TinymceEditor from '@/components/TinymceEditor/index.vue';
  export default {
    components: {TenantTypeSelect,ComImageUpload,TinymceEditor},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,
      // 上级id
      parentId: null,
      // 租户数据
      tenantList: Array,
    },
    data() {
      const defaultForm = {
        tenantId: null,
        parentId:'',
        picture:[],
        tenantLogo:'',
        tenantFullName:'',
        tenantCode:'',
        tenantType:'',
        tenantName: '',
        comments: ''
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          tenantName: [
            {
              required: true,
              message: '请输入租户名称',
              trigger: 'blur'
            }
          ],
          tenantType: [
            {
              required: true,
              message: '请选择租户类型',
              trigger: 'blur'
            }
          ]
        },
        // 提交状态
        loading: false,
        // 是否是修改
        isUpdate: false
      };
    },
    computed: {
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    methods: {
      /* 保存编辑 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }
          this.loading = true;

          if(this.form.picture.length>0 && this.form.picture[0].url){
            this.form.tenantLogo = this.form.picture[0].url;
          }else{
            this.form.tenantLogo='';
          }

          const saveOrUpdate = this.isUpdate ? updateTenant : addTenant;
          const data = {
            ...this.form,
            parentId: this.form.parentId || 0,
          };
          clearDeep(data);
          saveOrUpdate(data)
            .then((msg) => {
              this.loading = false;
              this.$message.success(msg);
              this.updateVisible(false);
              this.$emit('done');
            })
            .catch((e) => {
              this.loading = false;
              this.$message.error(e.message);
            });
        });
      },
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      }
    },
    watch: {
      visible(visible) {
        if (visible) {
          if (this.data) {
            this.loading = true;
            getTenant(this.data.tenantId).then((_data) => {
              this.loading = false;

              this.$util.assignObject(this.form, {
                ..._data,
                parentId: _data.parentId || ''}
              );

              if(_data.tenantLogo){
                var f={uid:1,status:'done',progress:100};
                f.url=_data.tenantLogo;
                this.form.picture=[f];
              }else{
                this.form.picture=[];
              }
            }).catch((e) => {
                this.loading = false;
                this.$message.error(e.message);
            });
            this.isUpdate = true;
          } else {
            this.form.picture=[];
            this.form.parentId = this.parentId || '';
            this.isUpdate = false;
          }
        } else {
          this.$refs.form.clearValidate();
          this.form = { ...this.defaultForm };
        }
      }
    }
  };
</script>
