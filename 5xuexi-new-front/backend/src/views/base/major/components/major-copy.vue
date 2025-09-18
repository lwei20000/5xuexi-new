<!-- 专业COPY弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="760px"
    :visible="visible"
    :close-on-click-modal="true"
    title="复制专业"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">

          <el-form-item label="来源年份:" prop="majorYear">
            <el-date-picker
              type="year"
              class="ele-fluid"
              v-model="form.majorYear"
              value-format="yyyy"
              placeholder="请选择来源年份"
            />
          </el-form-item>
          <el-form-item label="来源专业:" prop="majors">
            <major-select :multiple="true" v-model="form.majorIds" :initValue="form.initMajorIdValue"></major-select>
          </el-form-item>
          <el-form-item label="目标年份:" prop="targetMajorYear">
            <el-date-picker
              type="year"
              class="ele-fluid"
              v-model="form.targetMajorYear"
              value-format="yyyy"
              placeholder="请选择目标年份"
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
  import {copyMajor } from '@/api/base/major';
  import {clearDeep} from "@/api/common";
  import MajorSelect from "@/views/base/major/components/major-select";

  export default {
    components: {MajorSelect},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        majorIds: [],
        initMajorIdValue:[],
        majorYear:'',
        targetMajorYear:'',
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          targetMajorYear: [
            {
              required: true,
              message: '请选择目标年份',
              trigger: 'blur'
            }
          ]
        },
        // 提交状态
        loading: false
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
          if(!this.form.majorYear && this.form.majorIds.length == 0){
            this.$message.error("来源年份和来源专业至少填一个");
            return false;
          }
          this.loading = true;
          const data = {
            ...this.form
          };
          clearDeep(data);
          copyMajor(data)
            .then((msg) => {
              this.loading = false;
              this.$message.success(msg);
              this.updateVisible(false);
              this.$emit('done');
            }).catch((e) => {
              this.loading = false;
              this.$message.error(e.message);
            });
        });
      },
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      },
    },
    watch: {
      visible(visible) {
        if (visible) {
          this.form.majorIds=[];
          this.form.initMajorIdValue=[];
        } else {
          this.$refs.form.clearValidate();
          this.form = { ...this.defaultForm };
        }
      }
    }
  };
</script>
