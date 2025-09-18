<template>
  <el-upload
    :disabled="disabled"
    class="upload"
    :action="`${API_BASE_URL}/file/upload`"
    :headers="token()"
    :on-preview="handlePreview"
    :on-remove="handleRemove"
    :on-change="handleChange"
    :before-remove="beforeRemove"
    :accept="accept"
    multiple
    :limit="limit"
    :on-exceed="handleExceed"
    :file-list="value">

    <!--<el-button size="mini" :disabled="disabled" type="primary">点击上传</el-button>-->

    <el-link type="primary" :underline="false" style="vertical-align: baseline">
      点击上传
    </el-link>

    <span slot="tip" class="el-upload__tip" style="margin-left: 10px">文件不超过100MB<span v-if="describe">，{{describe}}</span></span>
  </el-upload>
</template>

<script>
import { API_BASE_URL ,TOKEN_HEADER_NAME} from '@/config/setting';
import { getToken } from '@/utils/token-util';
  export default {
    props: {
      accept:{default: ''},

      describe:{default: ''},

      value: {type:[Array,String,Number]},

      limit: {default: 1},

      // 禁用
      disabled: {default: false},
    },
    data() {
      return {
        API_BASE_URL:API_BASE_URL,
        fileList1: this.value
      };
    },
    created() {
    },
    methods: {
      token(){
        const t = getToken();
        const m = {};
        m[TOKEN_HEADER_NAME]=t;
        return m;
      },
      handleChange(file, fileList) {
       this.$emit('input', fileList);
      },
      handleRemove(file, fileList) {
        this.$emit('input', fileList);
      },
      handlePreview(file) {
        window.open(file?.response?.data?.path || file?.url, '_blank')
      },
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 ${this.limit} 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      beforeRemove(file) {
        return this.$confirm(`确定移除 ${ file.name }？`,'提示',{ type: 'warning',lockScroll:false });
      }
    }
  };
</script>
