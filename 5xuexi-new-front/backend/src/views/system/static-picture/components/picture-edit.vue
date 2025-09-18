<!-- 静态图片编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="460px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改静态图片' : '添加静态图片'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="82px">
      <el-form-item label="图片KEY:" prop="url">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.url"
          placeholder="请输入图片KEY"
        />
      </el-form-item>
      <el-form-item label="图片名称:" prop="name">
        <el-input
          clearable
          :maxlength="100"
          v-model="form.name"
          placeholder="请输入图片名称"
        />
      </el-form-item>
      <el-form-item label="图片:" prop="pictures">
        <com-image-upload v-model="form.pictures" :limit="1"/>
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
  import { addStaticPicture, updateStaticPicture } from '@/api/system/static-picture';
  import { clearDeep } from '@/api/common';
  import ComImageUpload from '@/views/system/file/components/com-image-upload.vue';

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
        url: '',
        picture: '',
        pictures:[],
        name: ''
      };
      return {
        defaultForm,
        // 表单数据
        form: { ...defaultForm },
        // 表单验证规则
        rules: {
          url: [
            {
              required: true,
              message: '请输入图片KEY',
              trigger: 'blur'
            }
          ],
          name: [
            {
              required: true,
              message: '请输入图片名称',
              trigger: 'blur'
            }
          ],
          pictures: [
            {
              required: true,
              message: '请上传图片',
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
    methods: {
      /* 保存编辑 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }

          if(this.form.pictures.length>0 && this.form.pictures[0].url){
            this.form.picture=this.form.pictures[0].url;
          }else{
            this.form.picture='';
          }
          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateStaticPicture : addStaticPicture;
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
          if (this.data) {
            this.$util.assignObject(this.form, this.data);
            if(this.data.picture){
              var f={uid:1,status:'done',progress:100};
              f.url=this.data.picture;
              this.form.pictures=[f];
            }else{
              this.form.pictures=[];
            }
            this.isUpdate = true;
          } else {
            this.form.pictures=[];
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
