<!-- 记录编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="960px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改记录' : '添加记录'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="102px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="年级:" prop="year">
            <el-date-picker
              type="year"
              class="ele-fluid"
              v-model="form.year"
              value-format="yyyy"
              placeholder="请选择年级"
            />
          </el-form-item>
          <el-form-item label="是否付费:" prop="payFlag">
            <el-radio-group v-model="form.payFlag">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="站点:" prop="orgId">
            <org-tree
              v-model="form.orgId"
              :multiple="false"
              :checkStrictly="true"
              :expandOnClickNode="false"
              placeholder="请选择站点"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }" style="margin-top: 20px">
          <el-form-item label="记录内容:" prop="content">
            <!-- 编辑器 -->
            <tinymce-editor
              ref="editor"
              v-model="form.remark"
              :init="{ height: 520 }"
            />
          </el-form-item>
        </el-col>
      </el-row>
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
  import { addRemark, updateRemark } from '@/api/report/remark';
  import {clearDeep} from "@/api/common";
  import OrgTree from '@/views/system/org/components/org-tree.vue';
  import TinymceEditor from '@/components/TinymceEditor/index.vue';
  export default {
    components: {OrgTree,TinymceEditor},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        id: null,
        year:'',
        orgId:'',
        payFlag:0,
        remark:''
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          year: [
            {
              required: true,
              message: '请选择年份',
              trigger: 'blur'
            }
          ],
          orgId: [
            {
              required: true,
              message: '请选择站点',
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
          const saveOrUpdate = this.isUpdate ? updateRemark : addRemark;
          const data = {
            ...this.form
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
            this.$util.assignObject(this.form, {...this.data});
            this.isUpdate = true;
          } else {
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
