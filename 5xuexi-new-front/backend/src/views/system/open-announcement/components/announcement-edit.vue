<!-- 公告编辑弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="960px"
    :visible="visible"
    :close-on-click-modal="true"
    :title="isUpdate ? '修改公告' : '添加公告'"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="102px">
      <el-row :gutter="15">
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="公告标题:" prop="title">
            <el-input
              clearable
              v-model="form.title"
              placeholder="请输入公告标题"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }">
          <el-form-item label="公告类型:" prop="openAnnouncementType">
            <announcement-type-select :params="{dictCode: 'open_announcement_type'}" v-model="form.openAnnouncementType" />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 24 } : { span: 24 }" style="margin-top: 20px">
          <el-form-item label="公告内容:" prop="content">
            <!-- 编辑器 -->
            <tinymce-editor
              ref="editor"
              v-model="form.content"
              :init="{ height: 520 }"
            />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }" style="margin-top:20px" >
          <el-form-item label="公告图片:" prop="picture">
            <com-image-upload v-model="form.picture" :limit="1" />
          </el-form-item>
        </el-col>
        <el-col v-bind="styleResponsive ? { sm: 12 } : { span: 12 }" style="margin-top:20px" >
          <el-form-item label="公告附件:" prop="attachment">
            <com-upload v-model="form.attachment" :limit="3"/>
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
import {addOpenAnnouncement, getOpenAnnouncement, updateOpenAnnouncement} from '@/api/system/open-announcement';
  import {clearDeep,htmlToString} from "@/api/common";
  import AnnouncementTypeSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  import ComImageUpload from '@/views/system/file/components/com-image-upload.vue';
  import TinymceEditor from '@/components/TinymceEditor/index.vue';
  import ComUpload from '@/views/system/file/components/com-upload.vue';

  export default {
    components: {AnnouncementTypeSelect ,TinymceEditor,ComImageUpload,ComUpload},
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 修改回显的数据
      data: Object
    },
    data() {
      const defaultForm = {
        openAnnouncementId: null,
        title: '',
        openAnnouncementType:'',
        openAnnouncementPicture:'',
        picture: [],
        openAnnouncementAttachment:'',
        attachment:[],
        content:'',
        contentText:''
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
              message: '请输入公告标题',
              trigger: 'blur'
            }
          ],
          openAnnouncementType: [
            {
              required: true,
              message: '请选择公告类型',
              trigger: 'blur'
            }
          ],
          content: [
            {
              required: true,
              message: '请输入公告内容',
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

          if (this.form.picture.length > 0 && this.form.picture[0].url) {
            this.form.openAnnouncementPicture = this.form.picture[0].url;
          } else {
            this.form.openAnnouncementPicture = '';
          }

          if(this.form.attachment && this.form.attachment.length>0){
            var list = [];
            for (var n = this.form.attachment.length - 1; n >= 0; n--) {
              if(this.form.attachment[n].status =='success') {
                var url = this.form.attachment[n].response?.data?.path || this.form.attachment[n].url;
                list.push({name:this.form.attachment[n].name,url: url,status:'success'});
              }
            }
            this.form.openAnnouncementAttachment =JSON.stringify(list);
          }else{
            this.form.openAnnouncementAttachment='';
          }

          var text = htmlToString(this.form.content);
          if(text && text.length>200){
            text = text.substring(0,200);
          }
          this.form.contentText = text;

          this.loading = true;
          const saveOrUpdate = this.isUpdate ? updateOpenAnnouncement : addOpenAnnouncement;
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
            this.loading = true;
            getOpenAnnouncement(this.data.openAnnouncementId).then((_data) => {
              this.loading = false;
              this.$util.assignObject(this.form, {..._data});

              if (_data.openAnnouncementPicture) {
                var f = { uid: 1, status: 'done', progress: 100 };
                f.url = _data.openAnnouncementPicture;
                this.form.picture = [f];
              } else {
                this.form.picture = [];
              }

              if(_data.openAnnouncementAttachment){
                this.form.attachment=JSON.parse(_data.openAnnouncementAttachment);
              }else{
                this.form.attachment=[];
              }

            }).catch((e) => {
              this.loading = false;
              this.$message.error(e.message);
            });

            this.isUpdate = true;
          } else {
            this.form.picture=[];
            this.form.attachment=[];
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
