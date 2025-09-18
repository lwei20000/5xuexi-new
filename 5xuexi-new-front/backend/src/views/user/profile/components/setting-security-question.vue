//设置密保问题弹窗
<template>
  <ele-modal :lock-scroll="false"
    width="420px"
    :title="isUpdate ? '修改密保问题' : '设置密保问题'"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    @update:visible="updateVisible"
  >
   <el-form
      ref="form"
      :model="form"
      :rules="rules"
      class="noBorderInput"
      label-width="86px"
      @keyup.enter.native="save"
    >
        <template>
          <div v-for="(item,index) in form.defaultForm" :key='index' class="question">
            <el-form-item :label="'问题'+ (index+1)+':'" :prop="`defaultForm.${index}.problem`" :rules="rules.problem">
              <question-type-Select
                :params="{ dictCode: 'safety_problem' }"
                :multiple="false"
                :filterable="true"
                v-model="item.problem"
              />
            </el-form-item>
            <el-form-item :label="'答案'+ (index+1) +':'" :prop="`defaultForm.${index}.answer`" :rules="rules.answer">
              <el-input
                clearable
                :maxlength="200"
                v-model.trim="item.answer"
                placeholder="请输入答案"
              />
            </el-form-item>
          </div>
        </template>
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
import QuestionTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';
import {clearDeep} from "@/api/common";
import {settingQa} from "@/api/user/profile";
export default {
  components:{QuestionTypeSelect},
  props:{
    visible:Boolean,
    problemList:Array
  },
  data(){
    const p_defaultForm = {
      defaultForm:[
        {id:'',problem:'',answer:''},
        {id:'',problem:'',answer:''},
        {id:'',problem:'',answer:''}
      ],
    };
    return{
      p_defaultForm,
      form:{
        ...p_defaultForm
      },
      // 是否是修改
      isUpdate: false,
      loading:false,
      rules:{
        problem: [
          {
            required: true,
            message: '请选择问题',
            trigger: 'blur'
          }
        ],
        answer: [
          {
            required: true,
            message: '请输入问题答案',
            trigger: 'blur'
          }
        ],
      }
    }
  },
  methods:{
    /* 保存编辑 */
    save() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return false;
        }
        this.loading = true;
        clearDeep(this.form.defaultForm);
        settingQa(this.form.defaultForm).then((msg) => {
            this.loading = false;
            this.$message.success(msg);
            //更新机构
            this.updateVisible(false);
            this.$emit('done');
            this.$parent.problemList = JSON.parse(JSON.stringify(this.form.defaultForm));
          }).catch((e) => {
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
        if (this.problemList && this.problemList.length>0) {
          this.form.defaultForm = JSON.parse(JSON.stringify(this.problemList));
        }else{
          this.form.defaultForm = JSON.parse(JSON.stringify(this.p_defaultForm.defaultForm));
        }
      } else {
        this.$refs.form.clearValidate();
        this.form.defaultForm = JSON.parse(JSON.stringify(this.p_defaultForm.defaultForm));
      }
    }
  }
}
</script>
<style lang="scss" scoped>
  .question{
    margin-bottom: 30px;
  }
</style>
