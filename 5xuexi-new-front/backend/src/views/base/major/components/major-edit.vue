<!-- 专业编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="760px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改专业' : '添加专业'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">

      <el-form-item label="年份:" prop="majorYear">
        <el-date-picker
          type="year"
          class="ele-fluid"
          v-model="form.majorYear"
          value-format="yyyy"
          placeholder="请选择年份"
        />
      </el-form-item>
      <el-form-item label="专业代码:" prop="majorCode">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.majorCode"
          placeholder="请输入专业代码"
        />
      </el-form-item>
      <el-form-item label="专业名称:" prop="majorName">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.majorName"
          placeholder="请输入专业名称"
        />
      </el-form-item>
        <el-form-item label="教育类别:" prop="majorType">
          <dict-data-select :params="{dictCode: 'major_type'}" v-model="form.majorType" />
        </el-form-item>
          <el-form-item label="专业层次:" prop="majorGradation">
            <dict-data-select :params="{dictCode: 'major_gradation'}" v-model="form.majorGradation" />
          </el-form-item>
          <el-form-item label="学习形式:" prop="majorForms">
            <dict-data-select :params="{dictCode: 'major_forms'}" v-model="form.majorForms" />
          </el-form-item>
          <el-form-item label="学制:" prop="majorLength">
            <dict-data-select :params="{dictCode: 'major_length'}" v-model="form.majorLength" />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">

      <el-form-item label="专业图片:" prop="majorPicture">
        <com-image-upload v-model="form.picture" :limit="1"/>
      </el-form-item>
      <el-form-item label="专业简介:">
        <el-input
          :rows="4"
          type="textarea"
          :maxlength="200"
          v-model="form.comments"
          placeholder="请输入专业简介"
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
  import { addMajor, updateMajor } from '@/api/base/major';
  import DictDataSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  import {clearDeep} from "@/api/common";
  import ComImageUpload from '@/views/system/file/components/com-image-upload.vue';

  export default {
    components: {  ComImageUpload ,DictDataSelect},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        majorId: null,
        majorYear:'',
        majorCode: '',
        majorName: '',
        majorPicture: '',
        majorType:'',
        majorGradation:'',
        majorForms:'',
        majorLength:'',
        picture:[],
        comments: ''
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          majorYear: [
            {
              required: true,
              message: '请选择年份',
              trigger: 'blur'
            }
          ],
          majorCode: [
            {
              required: true,
              message: '请输入专业代码',
              trigger: 'blur'
            }
          ],
          majorName: [
            {
              required: true,
              message: '请输入专业名称',
              trigger: 'blur'
            }
          ],
          majorType: [
            {
              required: true,
              message: '请选择教育类别',
              trigger: 'blur'
            }
          ],
          majorGradation: [
            {
              required: true,
              message: '请选择专业层次',
              trigger: 'blur'
            }
          ],
          majorForms: [
            {
              required: true,
              message: '请选择学习形式',
              trigger: 'blur'
            }
          ],
          majorLength: [
            {
              required: true,
              message: '请选择专业学制',
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

          if(this.form.picture.length>0){
            this.form.majorPicture=this.form.picture[0].url;
          }else{
            this.form.majorPicture='';
          }
          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateMajor : addMajor;
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
      },
    },
    watch: {
      visible(visible) {
        if (visible) {
          if (this.data) {
            this.$util.assignObject(this.form, this.data);
            if(this.data.majorPicture){
              var f={uid:1,status:'done',progress:100};
              f.url=this.data.majorPicture;
              this.form.picture=[f];
            }else{
              this.form.picture=[];
            }
            this.isUpdate = true;
          } else {
            this.form.picture=[];
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
