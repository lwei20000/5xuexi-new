<!-- 用户考试成绩导入弹窗 -->
<template>
  <ele-modal :lock-scroll="false"
    width="520px"
    title="导入用户考试成绩"
    :visible="visible"
    @update:visible="updateVisible"
  >
    <el-upload
      drag
      action=""
      class="ele-block"
      v-loading="loading"
      accept=".xls,.xlsx"
      :show-file-list="false"
      :before-upload="doUpload"
    >
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处, 或 <em>点击上传</em></div>
      <template v-slot:tip>
        <div class="el-upload__tip ele-text-center">
          <span>只能上传xlsx文件, </span>
          <el-link @click="doDownload"
            type="primary"
            :underline="false"
            style="vertical-align: baseline"
          >
            下载模板
          </el-link>
        </div>
      </template>
    </el-upload>
  </ele-modal>
</template>

<script>
  import { scoreImport, scoreTemplateExport} from '@/api/base/userCourse';
import {download, downloadUrl} from "@/api/common";

  export default {
    props: {
      // 是否打开弹窗
      visible: Boolean
    },
    data() {
      return {
        // 导入请求状态
        loading: false
      };
    },
    methods: {
      doDownload(){
        this.loading = true;
        scoreTemplateExport().then((data) => {
          this.loading = false;
          download(data,"用户考试成绩导入模板.xlsx");
        }).catch((e) => {
            this.loading = false;
            this.$message.error(e.message);
          });
      },
      /* 上传 */
      doUpload(file) {
        if (
          ![
            'application/vnd.ms-excel',
            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
          ].includes(file.type)
        ) {
          this.$message.error('只能选择 excel 文件');
          return false;
        }
        if (file.size / 1024 / 1024 > 10) {
          this.$message.error('大小不能超过 10MB');
          return false;
        }
        this.loading = true;
        scoreImport(file)
          .then((res) => {
            this.loading = false;
            this.$message.success(res.message);
            if(res.code ==999){
              downloadUrl(res.data,"用户考试成绩导入_错误数据.xlsx");
            }
            this.updateVisible(false);
            this.$emit('done');
          })
          .catch((e) => {
            this.loading = false;
            this.$message.error(e.message);
          });
        return false;
      },
      /* 更新visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      }
    }
  };
</script>

<style lang="scss" scoped>
  .ele-block {
    :deep(.el-upload),
    :deep(.el-upload-dragger) {
      width: 100%;
    }
  }
</style>
