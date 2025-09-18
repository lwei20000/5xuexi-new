<!-- 意见反馈编辑 -->
<template>
  <ele-modal :lock-scroll="false"
             width="960px"
             :visible="visible"
             :close-on-click-modal="true"
             :title="isUpdate ? '回复意见反馈' : '添加意见反馈'"
             @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
          <el-form-item label="标题:" prop="title">
            <el-input disabled
              clearable
              :maxlength="200"
              v-model="form.title"
              placeholder="请输入标题"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
          <el-form-item label="详细描述:" prop="comments">
            <el-input disabled
              :rows="6"
              clearable
              type="textarea"
              :maxlength="20000"
              v-model="form.comments"
              placeholder="请输入详细描述"
            />
          </el-form-item>
        </el-col>
        <el-col>
          <el-form-item label="手机号:" prop="phone">
            <el-input disabled
              clearable
              :maxlength="20"
              v-model="form.phone"
              placeholder="请输入手机号"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
          <el-form-item label="上传图片:" prop="picture">
            <com-image-upload key="picture"  disabled v-model="form.picture" :limit="4"/>
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
          <el-form-item label="回复描述:" prop="replyComments">
            <el-input
                      :rows="6"
                      clearable
                      type="textarea"
                      :maxlength="20000"
                      v-model="form.replyComments"
                      placeholder="请输入回复描述"
            />
          </el-form-item>
        </el-col>
        <el-col  style="margin-top: 15px"  v-bind="styleResponsive ? { sm: 24 } : { span: 24 }">
          <el-form-item label="回复图片:" prop="replyPicture">
            <com-image-upload key="replyPicture" v-model="form.replyPicture" :limit="4"/>
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
  import {
    updateFeedback
  } from '@/api/system/feedback';
  import {clearDeep} from "@/api/common";
import ComImageUpload from "@/views/system/file/components/com-image-upload.vue";

  export default {
    components: {ComImageUpload },
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        id: null,
        title: '',
        phone:'',
        picture:[],
        pictures: '',
        comments: '',
        replyComments: '',
        replyPicture:[],
        replyPictures: '',
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          title: [
            {
              required: true,
              message: '请输入标题',
              trigger: 'blur'
            }
          ],
          comments: [
            {
              required: true,
              message: '请输入详细描述',
              trigger: 'blur'
            }
          ],
          replyComments: [
            {
              required: true,
              message: '请输入回复描述',
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
          const data = {
            ...this.form,
          };
          if(this.form.picture.length>0 && this.form.picture[0].url){
            data.pictures=JSON.stringify(this.form.picture);
          }else{
            data.pictures="";
          }
          if(this.form.replyPicture.length>0 && this.form.replyPicture[0].url){
            data.replyPictures=JSON.stringify(this.form.replyPicture);
          }else{
            data.replyPictures="";
          }
          delete data.replyPicture;
          delete data.picture;
          const saveOrUpdate = this.isUpdate ? updateFeedback : function () {};
          clearDeep(data);
          saveOrUpdate(data)
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
      }
    },
    watch: {
      visible(visible) {
        if (visible) {
          if (this.data) {
            this.$util.assignObject(this.form, {
              ...this.data,
            });
            if(this.data.pictures){
              this.form.picture = JSON.parse(this.data.pictures);
            }else{
              this.form.picture =[];
            }
            if(this.data.replyPictures){
              this.form.replyPicture = JSON.parse(this.data.replyPictures);
            }else{
              this.form.replyPicture =[];
            }
            this.isUpdate = true;
          } else {
            this.form.picture =[];
            this.form.replyPicture =[];
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
