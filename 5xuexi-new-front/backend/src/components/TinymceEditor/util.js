import request from "@/utils/request";

const BASE_URL = process.env.BASE_URL;

// 默认加载插件
const PLUGINS = [
  'code',
  'preview',
  'fullscreen',
  'paste',
  'searchreplace',
  'save',
  'autosave',
  'link',
  'autolink',
  'image',
  'media',
  'table',
  'codesample',
  'lists',
  'advlist',
  'hr',
  'charmap',
  'emoticons',
  'anchor',
  'directionality',
  'pagebreak',
  'quickbars',
  'nonbreaking',
  'visualblocks',
  'visualchars',
  'wordcount',
  'kityformula-editor',
  'indent2em'
].join(' ');

// 默认工具栏布局
const TOOLBAR = [
  'fullscreen',
  'preview',
  'code',
  '|',
  'undo',
  'redo',
  '|',
  'forecolor',
  'backcolor',
  '|',
  'bold',
  'italic',
  'underline',
  'strikethrough',
  '|',
  'alignleft',
  'aligncenter',
  'alignright',
  'alignjustify',
  '|',
  'outdent',
  'indent',
  '|',
  'numlist',
  'bullist',
  '|',
  'formatselect',
  'fontselect',
  'fontsizeselect',
  '|',
  'link',
  'image',
  'media',
  'emoticons',
  'charmap',
  'anchor',
  'pagebreak',
  'codesample',
  '|',
  'ltr',
  'rtl',
  'kityformula-editor',
  'indent2em'
].join(' ');

// 默认配置
export const DEFAULT_CONFIG = {
  height: 300,
  branding: false,
  content_style:'* {margin: 0;padding: 0;outline: none;-webkit-tap-highlight-color: rgba(0, 0, 0, 0);scrollbar-width: thin;} ul,ol{padding-inline-start: 40px;} ' +
    '  a {text-decoration: none;} h1, h2, h3, h4, h5, h6 {font-weight: normal;}',
  skin_url: BASE_URL + 'tinymce/skins/ui/oxide',
  content_css: BASE_URL + 'tinymce/skins/content/default/content.min.css',
  language_url: BASE_URL + 'tinymce/langs/zh_CN.js',
  language: 'zh_CN',
  plugins: PLUGINS,
  toolbar: TOOLBAR,
  draggable_modal: true,
  toolbar_mode: 'sliding',
  quickbars_insert_toolbar: '',
  images_upload_handler: (blobInfo, success, error) => {
    var file = blobInfo.blob();
    if(file instanceof File){
      if (blobInfo.blob().size / 1024 > 4000) {
        error('大小不能超过 4000KB');
        return;
      }
      const formData = new FormData();
      formData.append('file', file);
      request({
        url: '/file/upload',
        method: 'post',
        data: formData
      }).then((res) => {
        if(res.data.code === 0) {
          success(res.data.data.path);
        }
      }).catch((e) => {
        this.$message.error(e);
      });
    }
  },
  file_picker_types: 'file image media',
  // 自定义文件上传(这里使用把选择的文件转成 blob 演示)
  file_picker_callback: (callback, _value, meta) => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    // 设定文件可选类型
    if (meta.filetype === 'image') {
      input.setAttribute('accept', 'image/*');
    } else if (meta.filetype === 'media') {
      input.setAttribute('accept', 'video/*');
    }
    input.onchange = () => {
      const file = input.files?.[0];
      if (!file) {
        return;
      }
      if (meta.filetype === 'media') {
        if (!file.type.startsWith('video/')) {
          this.$refs.editor.alert({ content: '只能选择视频文件' });
          return;
        }
      }
      if (file.size / 1024 / 1024 > 20) {
        this.$refs.editor.alert({ content: '大小不能超过 20MB' });
        return;
      }

      const formData = new FormData();
      formData.append('file', file);
      request({
        url: '/file/upload',
        method: 'post',
        data: formData
      }).then((res) => {
        if(res.data.code === 0) {
          callback(res.data.data.path, { title: res.data.data.name });
        }
      }).catch((e) => {
        this.$message.error(e);
      });
    };
    input.click();
  }
};

// 暗黑主题配置
export const DARK_CONFIG = {
  skin_url: BASE_URL + 'tinymce/skins/ui/oxide-dark',
  content_css: BASE_URL + 'tinymce/skins/content/dark/content.min.css'
};

// 支持监听的事件
export const VALID_EVENTS = [
  'onActivate',
  'onAddUndo',
  'onBeforeAddUndo',
  'onBeforeExecCommand',
  'onBeforeGetContent',
  'onBeforeRenderUI',
  'onBeforeSetContent',
  'onBeforePaste',
  'onBlur',
  'onChange',
  'onClearUndos',
  'onClick',
  'onContextMenu',
  'onCopy',
  'onCut',
  'onDblclick',
  'onDeactivate',
  'onDirty',
  'onDrag',
  'onDragDrop',
  'onDragEnd',
  'onDragGesture',
  'onDragOver',
  'onDrop',
  'onExecCommand',
  'onFocus',
  'onFocusIn',
  'onFocusOut',
  'onGetContent',
  'onHide',
  'onInit',
  'onKeyDown',
  'onKeyPress',
  'onKeyUp',
  'onLoadContent',
  'onMouseDown',
  'onMouseEnter',
  'onMouseLeave',
  'onMouseMove',
  'onMouseOut',
  'onMouseOver',
  'onMouseUp',
  'onNodeChange',
  'onObjectResizeStart',
  'onObjectResized',
  'onObjectSelected',
  'onPaste',
  'onPostProcess',
  'onPostRender',
  'onPreProcess',
  'onProgressState',
  'onRedo',
  'onRemove',
  'onReset',
  'onSaveContent',
  'onSelectionChange',
  'onSetAttrib',
  'onSetContent',
  'onShow',
  'onSubmit',
  'onUndo',
  'onVisualAid'
];

let unique = 0;

/**
 * 生成编辑器 id
 */
export function uuid(prefix) {
  const time = Date.now();
  const random = Math.floor(Math.random() * 1000000000);
  unique++;
  return prefix + '_' + random + unique + String(time);
}

/**
 * 绑定事件
 */
export function bindHandlers(initEvent, listeners, editor) {
  const validEvents = VALID_EVENTS.map((event) => event.toLowerCase());
  Object.keys(listeners)
    .filter((key) => validEvents.includes(key.toLowerCase()))
    .forEach((key) => {
      const handler = listeners[key];
      if (typeof handler === 'function') {
        if (key === 'onInit') {
          handler(initEvent, editor);
        } else {
          editor.on(key.substring(2), (e) => handler(e, editor));
        }
      }
    });
}

/**
 * 弹出提示框
 */
export function openAlert(editor, option = {}) {
  editor?.windowManager?.open({
    title: option.title ?? '提示',
    body: {
      type: 'panel',
      items: [
        {
          type: 'htmlpanel',
          html: `<p>${option.content ?? ''}</p>`
        }
      ]
    },
    buttons: [
      {
        type: 'cancel',
        name: 'closeButton',
        text: '确定',
        primary: true
      }
    ]
  });
}
