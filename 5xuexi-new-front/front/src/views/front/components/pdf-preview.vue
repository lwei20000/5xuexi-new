<template>
  <div class="global">
    <div class="pdfContainer">
      <div class="button">
        <el-button icon="el-icon-zoom-in" @click="scaleD()" size="mini">放大</el-button>
        <el-button icon="el-icon-zoom-out" @click="scaleX()" size="mini">缩小</el-button>
      </div>
      <div v-if="!numPages" style="text-align: center"><i class="el-icon-loading"></i>加载中。。。</div>
      <div class="pdf">
        <pdf class="scroll_container_pdf" v-for="i in numPages" :key="i" :src="src" :page="i" style="display: block; width: 100%"></pdf>
      </div>
    </div>
  </div>
</template>
<script>
import Pdf from "pdfvuer";
export default {
  name: 'pdfPreview',
  props: {
    url:undefined
  },
  data() {
    return {
      scale: 100,  //  开始的时候默认和容器一样大即宽高都是100%
      numPages:0,
      src: "",
    };
  },
  components: { Pdf },
  mounted() {

  },
  methods: {
    //放大
    scaleD() {
      if(this.scale >= 200) return false;
      this.scale += 10;
      var pdfs = document.getElementsByClassName("scroll_container_pdf");
      for(var i=0;i<pdfs.length;i++){
        pdfs[i].style.width =  parseInt(this.scale)+ "%";
      }
    },
    //缩小
    scaleX() {
      if(this.scale <= 100) return false;
      this.scale += -10;
      var pdfs = document.getElementsByClassName("scroll_container_pdf");
      for(var i=0;i<pdfs.length;i++){
        pdfs[i].style.width =  parseInt(this.scale)+ "%";
      }
    },
  },
  watch: {
    url() {
      this.src = Pdf.createLoadingTask(this.url);//解决中文乱码问题
      this.src.then(Pdf => {
        this.numPages = Pdf.numPages;
      });
    }
  }
};
</script>
<style scoped>

.scroll_container_pdf :deep(canvas){
  box-sizing: border-box;
  border-top: 1px solid var(--border-color-base);
}
.scroll_container_pdf>:deep(div),.scroll_container_pdf :deep(.page),.scroll_container_pdf :deep(.textLayer),.scroll_container_pdf :deep(canvas),.scroll_container_pdf :deep(.canvasWrapper) {
  width: 100% !important;
  height: 100% !important;
}


.global {
  width: 100%;
  height: 100%;
}

.pdfContainer {
  width: 100%;
  height: calc(100% - 50px);
}
.pdfContainer .button {
  width: 100%;
  height: 40px;
  text-align: center;
}
.pdfContainer .pdf {
  width: 100%;
  height: 100%;
  overflow: auto;
}
</style>
