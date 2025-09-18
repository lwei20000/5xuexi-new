<!-- 开发文档 -->
<template>
  <div class="ele-body" ref="eleBody" v-loading="loading">
    <ele-split-layout style="min-height: calc(100vh - 90px);" class="ele-bg-white"
      width="316px"
      :reverse="true"
      allow-collapse
      :leftStyle="{'border-left':'1px solid #ededed'}"
      :right-style="{ overflow: 'hidden','padding': '20px 20px 75px','word-wrap': 'break-word'}"
      :collapseBtnStyle="{width:'20px'}"
    >
      <div>
        <div style="padding-left: 2%;padding-right: 2%; margin-top: 10px;">
          <el-alert
            title="* 提交意见反馈后，请耐心等待管理员回复。如紧急问题，请电话联系。"
            type="success"
            :closable="false">
          </el-alert>
          <el-form ref="form" :model="form" :rules="rules" class="noBorderInput">
            <el-row :gutter="15">
              <el-col>
                <el-form-item label="标题:" prop="title">
                  <el-input
                    clearable
                    :maxlength="200"
                    v-model.trim="form.title"
                    placeholder="请输入标题"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item label="详细描述:" prop="comments">
                  <el-input
                    :rows="8"
                    clearable
                    type="textarea"
                    :maxlength="20000"
                    v-model.trim="form.comments"
                    placeholder="请输入详细描述"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item label="手机号:" prop="phone">
                  <el-input
                    clearable
                    :maxlength="20"
                    v-model.trim="form.phone"
                    placeholder="请输入手机号"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                  <label class="el-form-item__label" style="float:none">上传图片:</label>
                  <div style="text-align: center">
                    <com-image-upload v-model="form.picture" :limit="4"/>
                  </div>
              </el-col>
              <el-col>
                <div style="text-align: center;margin:20px 0;">
                  <el-button type="primary" :loading="loading" @click="save">
                    保存
                  </el-button>
                </div>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </div>
      <template v-slot:content>
        <div class="infinite-list-wrapper">
          <div class="top-search-group noBorderInput" style="display: flex;">
            <el-input style="max-width: 260px" placeholder="请输入标题" v-model="keyword" />
            <el-button style="margin-left: 20px" type="primary" @click="queryKeyword" >搜索</el-button>
          </div>
          <div class="article-list-item" v-for="(item, index) in data" :key="index">
            <h4 style="padding-top: 15px;overflow:hidden;white-space: nowrap;text-overflow: ellipsis;-o-text-overflow:ellipsis;">
              <b>{{ item.title }}</b>
            </h4>
            <div class="article-list-wrapper">
              <div class="article-list-left">
                <div class="article-list-content" style="margin: 10px;">{{ item.comments }}</div>
                <div v-if="item.pictures" style="cursor: pointer">
                   <img style="width: 100px;height: 100px;margin: 0 10px;" v-for="(_item,index) in JSON.parse(item.pictures)" :src="_item.url" :key="index" @click="previewItemImage(item,_item)">
                </div>
                <div class="article-list-user-group" style="margin: 10px;text-align: end;">
                  <el-tag style="margin-right: 10px" type="warning" size="mini" v-if="item.status == 0">待回复</el-tag>
                  <div style="display: inline-block" v-if="item.status == 1">
                    <a style="cursor: pointer" @click="updateStatus(item,2)" >满意</a><a style="cursor: pointer;margin:0 10px;color: red" @click="updateStatus(item,0)">不满意</a>
                  </div>
                  <el-tag style="margin-right: 10px" type="success" size="mini" v-if="item.status == 2">已解决</el-tag>
                  <span>{{ item.time }}</span>
                </div>
              </div>
            </div>
            <div v-if="item.replyComments && item.status !=0" style="background: #ededed;padding: 10px;margin-bottom: 20px">
              <h5 style="color: var(--color-primary);">管理员回复：</h5>
              <div>
                <div class="article-list-content" style="margin: 10px;">{{ item.replyComments }}</div>
                <div v-if="item.replyPictures" style="cursor: pointer">
                  <img style="width: 100px;height: 100px;margin: 0 10px;" v-for="(_item,index) in JSON.parse(item.replyPictures)" :src="_item.url" :key="index" @click="previewReplyItemImage(item,_item)">
                </div>
                <div class="article-list-user-group" style="margin: 10px;text-align: end;">
                 <span>{{ item.replyTime }}</span>
                </div>
              </div>
            </div>
            <el-divider/>
          </div>
          <el-empty v-if="!data.length" description="暂无数据" ></el-empty>
          <div style="padding:15px 0; text-align: center" v-else>
            <el-button size="small" :loading="loading" v-if="hasMore" @click="query">
              加载更多
            </el-button>
            <span v-else>已全部加载完</span>
          </div>
        </div>
      </template>
    </ele-split-layout>
    <!-- 用于图片预览 -->
    <div style="display: none">
      <el-image
        ref="previewRef"
        v-if="previewOption.visible"
        :src="previewOption.current"
        :preview-src-list="previewImages"
      />
    </div>
  </div>
</template>

<script>
import {addUserFeedback, pageUserFeedbacks, updateUserStatus, updateUserFeedback} from "@/api/system/feedback";
import ComImageUpload from "@/views/system/file/components/com-image-upload.vue";
import {clearDeep} from "@/api/common";
export default {
  name: 'SystemUserFeedback',
  components: {ComImageUpload},
  data() {
    return {
      // 图片预览列表
      previewImages: [],
      // 图片预览配置
      previewOption: {
        visible: false,
        current: null
      },

      hasMore:false,
      page:0,
      limit:10,
      keyword: '',
      loading: false,
      data: [],

      form: {
        id: null,
        title: '',
        phone:'',
        picture:[],
        pictures: '',
        comments: ''
      },
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
        ]
      },
    }
  },
  mounted() {
    this.query();
  },
  methods: {
    queryKeyword() {
      this.page = 0
      this.data=[];
      //滚动到顶部
      document.getElementsByClassName("ele-admin-content")[0].scrollTop = 0;
      this.query()
    },
    query() {
      this.page++;
      var params = {};
      params.page = this.page;
      params.limit = this.limit;
      params.title = this.keyword
      pageUserFeedbacks(params).then((result) => {
        if (this.page * this.limit < result.count) {
          this.hasMore = true;
        }else{
          this.hasMore = false;
        }
        for(var i=0;i<result.list.length;i++){
          this.data.push(result.list[i]);
        }
      }).catch((e) => {
        this.$message.error(e.message);
      });
    },
    /* 保存编辑 */
    save() {
      this.$refs.form.validate((valid) => {
        if (!valid) {
          return false;
        }
        this.loading = true;
        const data = {
          ...this.form
        };
        if(this.form.picture.length>0 && this.form.picture[0].url){
          data.pictures=JSON.stringify(this.form.picture);
        }else{
          data.pictures="";
        }
        delete data.picture;
        const saveOrUpdate = this.isUpdate ? updateUserFeedback : addUserFeedback;
        clearDeep(data);
        saveOrUpdate(data)
          .then((msg) => {
            this.loading = false;
            this.form = {
              id: null,
              title: '',
              phone: '',
              picture:[],
              pictures: '',
              comments: ''
            };
            this.queryKeyword();
            this.$message.success(msg);
          }).catch((e) => {
            this.loading = false;
            this.$message.error(e.message);
          });
      });
    },
    /* 预览图片文件 */
    previewItemImage(item,_item) {
      var pictures = JSON.parse(item.pictures);
      this.previewImages = pictures
        .map((d) => d.url);
      this.previewOption.current = _item.url;
      this.previewOption.visible = true;
      this.$nextTick(() => {
        if (this.$refs.previewRef) {
          this.$refs.previewRef.showViewer = true;
        }
      });
    },
    previewReplyItemImage(item,_item){
      var pictures = JSON.parse(item.replyPictures);
      this.previewImages = pictures
        .map((d) => d.url);
      this.previewOption.current = _item.url;
      this.previewOption.visible = true;
      this.$nextTick(() => {
        if (this.$refs.previewRef) {
          this.$refs.previewRef.showViewer = true;
        }
      });
    },
    updateStatus(item,status){
      updateUserStatus(item.id,status).then(() => {
        this.queryKeyword();
        if(status == 2){
          this.$message.success("感谢您的反馈");
        }else{
          this.$message.success("请等待管理员重新回复");
        }
      }).catch((e) => {
        this.$message.error(e.message);
      });
    },
  },
  watch: {

  },
}
</script>

<style lang="scss" scoped>
.ele-border-lighter {
  min-height: calc(100vh - 264px);
  box-sizing: border-box;
  overflow: auto;
}

.ele-border-lighter :deep(.el-tree-node__content) {
  height: 40px;

  & > .el-tree-node__expand-icon {
    margin-left: 10px;
  }
}

.ele-table-tool-default{
  border: 0px;
  background-color: transparent;
}

.el-menu--horizontal > .el-menu-item,.el-menu--horizontal > .el-menu-item.is-active {
  border-bottom: none;
}
.wdms_html{
  padding-top: 10px;
}
.el-menu-demo{
  displey:-webkit-flex; display: flex; -webkit-flex-flow:row nowrap;
  flex-flow:row nowrap; overflow-x: auto; list-style: none;
}
/* 隐藏滚动条 */
.el-menu-demo::-webkit-scrollbar {
  display: none;
}

.el-menu-demo li {
  display: inline-block;
}
</style>


