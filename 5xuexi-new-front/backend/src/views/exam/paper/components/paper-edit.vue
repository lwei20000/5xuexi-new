<!-- 试卷编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="680px"
    :visible="visible"
    append-to-body
    :close-on-click-modal="true"
    custom-class="ele-dialog-form"
    :title="isUpdate ? '修改试卷' : '添加试卷'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="102px">
      <el-row :gutter="15">
        <el-form-item label="试卷名称:" prop="paperName">
          <el-input
            clearable
            v-model="form.paperName"
            placeholder="请输入试卷名称"
          />
        </el-form-item>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="试卷类型:" prop="paperType">
            <el-radio-group v-model="form.paperType"  disabled>
              <el-radio :label="1">文件试卷</el-radio>
              <el-radio :label="2">题目试卷</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="是否默认:" prop="paperUsage">
            <el-radio-group v-model="form.paperUsage" >
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>

        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
        <el-form-item label="文件试卷:" prop="files" v-if="form.paperType==1">
          <com-upload v-model="form.files" :limit="1" />
        </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template v-slot:footer>
      <el-button @click="updateVisible(false)">取消</el-button>
      <el-button type="primary" :loading="loading" @click="save">
        保存
      </el-button>
    </template>
  </ele-modal>
</template>

<script>
import ComUpload from '@/views/system/file/components/com-upload.vue';
  import {
    addPaper,
    updatePaper
  } from '@/api/exam/paper';
  import {clearDeep} from "@/api/common";

  export default {
    components: { ComUpload },
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object,

      courseId:Number,
    },
    data() {
      const defaultForm = {
        paperId: null,
        paperName:null,
        paperType: 1,
        paperFile: null,
        paperUsage:null,
        files:[]
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          paperName: [
            {
              required: true,
              message: '请输入试卷名称',
              trigger: 'blur'
            }
          ],
          paperType: [
            {
              required: true,
              message: '请选择试卷类型',
              trigger: 'blur'
            }
          ],
          paperUsage: [
            {
              required: true,
              message: '请选择是否默认试卷',
              trigger: 'blur'
            }
          ],
          files: [
            {
              required: true,
              message: '请上传文件',
              trigger: 'blur'
            }
          ],
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
          if(this.form.files && this.form.files.length>0){
            var list = [];
            for (var n = this.form.files.length - 1; n >= 0; n--) {
              if(this.form.files[n].status =='success') {
                var url = this.form.files[n].response?.data?.path || this.form.files[n].url;
                list.push({name:this.form.files[n].name,url: url,status:'success'});
              }
            }
            this.form.paperFile =JSON.stringify(list);
          }else{
            this.form.paperFile='';
          }

          this.loading = true;
          const data = {...this.form};
          const saveOrUpdate = this.isUpdate ? updatePaper : addPaper;
          clearDeep(data);



          data.courseId =this.courseId;
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

            if(this.data.paperFile){
              this.form.files=JSON.parse(this.data.paperFile);
            }else{
              this.form.files=[];
            }

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
