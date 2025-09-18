<!-- 富文本编辑器 -->
<template>
  <component v-if="inlineEditor" :is="tagName" :id="elementId" />
  <textarea v-else :id="elementId"></textarea>
</template>

<script>
  import tinymce from 'tinymce/tinymce';
  import 'tinymce/themes/silver';
  import 'tinymce/icons/default';
  import 'tinymce/plugins/code';
  import 'tinymce/plugins/preview';
  import 'tinymce/plugins/fullscreen';
  import 'tinymce/plugins/paste';
  import 'tinymce/plugins/searchreplace';
  import 'tinymce/plugins/save';
  import 'tinymce/plugins/autosave';
  import 'tinymce/plugins/link';
  import 'tinymce/plugins/autolink';
  import 'tinymce/plugins/image';
  import 'tinymce/plugins/media';
  import 'tinymce/plugins/table';
  import 'tinymce/plugins/codesample';
  import 'tinymce/plugins/lists';
  import 'tinymce/plugins/advlist';
  import 'tinymce/plugins/hr';
  import 'tinymce/plugins/charmap';
  import 'tinymce/plugins/emoticons';
  import 'tinymce/plugins/anchor';
  import 'tinymce/plugins/directionality';
  import 'tinymce/plugins/pagebreak';
  import 'tinymce/plugins/quickbars';
  import 'tinymce/plugins/nonbreaking';
  import 'tinymce/plugins/visualblocks';
  import 'tinymce/plugins/visualchars';
  import 'tinymce/plugins/wordcount';
  import 'tinymce/plugins/emoticons/js/emojis';
  import '../../../public/tinymce/plugins/kityformula-editor/plugin';
  import '../../../public/tinymce/plugins/indent2em/plugin';
  import {
    DEFAULT_CONFIG,
    DARK_CONFIG,
    uuid,
    bindHandlers,
    openAlert
  } from './util';

  export default {
    name: 'TinymceEditor',
    props: {
      // 编辑器唯一 id
      id: String,
      // v-model
      value: String,
      // 编辑器配置
      init: Object,
      // 是否内联模式
      inline: {
        type: Boolean,
        default: false
      },
      // model events
      modelEvents: {
        type: String,
        default: 'change input undo redo'
      },
      // 内联模式标签名
      tagName: {
        type: String,
        default: 'div'
      },
      // 是否禁用
      disabled: Boolean,
      // 自动跟随框架主题
      autoTheme: {
        type: Boolean,
        default: true
      },
      // 是否使用暗黑主题
      darkTheme: Boolean
    },
    data() {
      return {
        // 编辑器唯一 id
        elementId: this.id || uuid('tiny-vue'),
        // 编辑器实例
        editorIns: null,
        // 是否内联模式
        inlineEditor: this.init?.inline || this.inline
      };
    },
    computed: {
      // 是否是暗黑模式
      darkMode() {
        return this.$store?.state?.theme?.darkMode;
      }
    },
    methods: {
      /* 更新 value */
      updateValue(value) {
        this.$emit('input', value);
      },
      /* 修改内容 */
      setContent(value) {
        if (
          this.editorIns &&
          typeof value === 'string' &&
          value !== this.editorIns.getContent()
        ) {
          this.editorIns.setContent(value);
        }
      },
      /* 渲染编辑器 */
      render() {
        const isDark = this.autoTheme ? this.darkMode : this.darkTheme;
        tinymce.init({
          ...DEFAULT_CONFIG,
          ...(isDark ? DARK_CONFIG : {}),
          ...this.init,
          selector: `#${this.elementId}`,
          readonly: this.disabled,
          inline: this.inlineEditor,
          setup: (editor) => {
            this.editorIns = editor;
            editor.on('init', (e) => {
              // 回显初始值
              if (this.value) {
                this.setContent(this.value);
              }
              // v-model
              editor.on(this.modelEvents, () => {
                this.updateValue(editor.getContent());
              });
              // valid events
              bindHandlers(e, this.$attrs, editor);
            });
            if (typeof this.init?.setup === 'function') {
              this.init.setup(editor);
            }
          }
        });
      },
      /* 销毁编辑器 */
      destory() {
        if (tinymce != null && this.editorIns != null) {
          tinymce.remove(this.editorIns);
          this.editorIns = null;
        }
      },
      /* 弹出提示框 */
      alert(option) {
        openAlert(this.editorIns, option);
      }
    },
    watch: {
      value(val, prevVal) {
        if (val !== prevVal) {
          this.setContent(val);
        }
      },
      disabled(disable) {
        if (this.editorIns !== null) {
          if (typeof this.editorIns.mode?.set === 'function') {
            this.editorIns.mode.set(disable ? 'readonly' : 'design');
          } else {
            this.editorIns.setMode(disable ? 'readonly' : 'design');
          }
        }
      },
      tagName() {
        this.destory();
        this.$nextTick(() => {
          this.render();
        });
      },
      darkMode() {
        if (this.autoTheme) {
          this.destory();
          this.$nextTick(() => {
            this.render();
          });
        }
      }
    },
    mounted() {
      this.render();
    },
    beforeDestroy() {
      this.destory();
    },
    activated() {
      this.render();
    },
    deactivated() {
      this.destory();
    }
  };
</script>

<style>
  body .tox-tinymce-aux ,.tox-fullscreen .tox.tox-tinymce-aux, .tox-fullscreen~.tox.tox-tinymce-aux{
    z-index: 19990000!important;
  }

  textarea[id^='tiny-vue'] {
    width: 0;
    height: 0;
    margin: 0;
    padding: 0;
    opacity: 0;
    box-sizing: border-box;
  }
</style>
