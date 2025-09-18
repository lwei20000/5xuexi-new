<template>
  <ele-image-upload :value="images" :disabled="disabled" :limit="limit" @upload="onUpload" :before-upload="onBeforeUpload" :before-remove="onBeforeRemove" />
</template>

<script>
import EleImageUpload from 'ele-admin/es/ele-image-upload';
import request from '@/utils/request';

  export default {
    components: {
      EleImageUpload
    },
    props: {

      value:{type:[Array,String,Number]},

      limit: {default: 1},
      // 禁用
      disabled: {default: false},
    },
    data() {
      return {
        images:this.value
      };
    },
    created() {
    },
    methods: {
      updateValue() {
        this.$emit('input', this.images);
      },
      /* beforeUpload 返回 false 则阻止上传, 也可以返回 Promise , resolve 继续上传, reject 阻止上传 */
      onBeforeUpload(file) {
        // file 即选择后的文件
        if (!file.type.startsWith('image')) {
          this.$message.error('只能选择图片');
          return false;
        }
      },
      onBeforeRemove(item){
        for(var i=0;i<this.images.length;i++){
          if(item.name == this.images[i].name){
            this.images.splice(i, 1);
            break;
          }
        }
        this.updateValue();
      },
      onUpload(item) {
        // item 包含的字段参考前面说明
        item.status = 'uploading';
        this.images.push(item);
        const formData = new FormData();
        formData.append('file', item.file);
        request({
          url: '/file/upload',
          method: 'post',
          data: formData,
          onUploadProgress: (e) => {  // 文件上传进度回调
            if (e.lengthComputable) {
              item.progress = e.loaded / e.total * 100;
            }
          }
        }).then((res) => {
          if(res.data.code === 0) {
            item.status = 'done';
            item.url = res.data.data.path;
            // 如果你上传的不是图片格式, 建议将 url 字段作为缩略图, 再添加其它字段作为最后提交数据
            item.fileUrl = res.data.data.path;
            this.updateValue();
          }
        }).catch((e) => {
          this.$message.error(e);
          item.status = 'exception';
          this.images.pop();
          this.updateValue();
        });
      },
    },
    watch: {
      // 监听机构id变化
      value(value) {
        this.images = value;
      }
    }
  };
</script>
