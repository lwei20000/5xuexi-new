<!-- 批量发布考试发布 -->
<template>
  <ele-modal :lock-scroll="false"
    width="560px"
    :visible="visible"
    append-to-body
    :close-on-click-modal="true"
    title= "批量发布考试"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-form-item label="考试时间:" prop="dateRange">
        <el-date-picker
          v-model="form.dateRange"
          type="datetimerange"
          unlink-panels
          range-separator="-"
          start-placeholder="考试开始日期"
          end-placeholder="考试结束日期"
          value-format="yyyy-MM-dd HH:mm:ss"
          class="ele-fluid"
        />
      </el-form-item>
      <el-alert
        title="* 按照查询条件批量发布考试。只会发布课程有默认试卷的，并且该专业课程没有进行中或未开始的考试"
        type="success"
        :closable="false">
      </el-alert>
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
  import { batchSave }  from "@/api/exam/publish";
  import {clearDeep} from "@/api/common";
  export default {
    components: { },
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        dateRange:null
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          dateRange: [
            {
              required: true,
              message: '请选择考试时间',
              trigger: 'blur'
            }
          ],
        },
        // 提交状态
        loading: false
      };
    },
    methods: {
      /* 保存编辑 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }
          this.loading = true;
          const data = {
            ...this.data
          };

          data.startTime =  this.form.dateRange[0];
          data.endTime =  this.form.dateRange[1];

          clearDeep(data);
          batchSave(data).then((msg) => {
            this.loading = false;
            this.updateVisible(false);
            this.$message.success(msg);
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
  };
</script>
