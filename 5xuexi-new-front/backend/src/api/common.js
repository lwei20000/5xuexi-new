//取页面显隐
export const getHiddenProp = () =>{
  var prefixes = ['webkit','moz','ms','o'];
  // 如果hidden 属性是原生支持的，我们就直接返回
  if ('hidden' in document) {
    return 'hidden';
  }

  // 其他的情况就循环现有的浏览器前缀，拼接我们所需要的属性
  for (var i = 0; i < prefixes.length; i++){
    // 如果当前的拼接的前缀在 document对象中存在 返回即可
    if ((prefixes[i] + 'Hidden') in document) {
      return prefixes[i] + 'Hidden';
    }
  }
  // 其他的情况 直接返回null
  return null;
}
//取页面显隐状态
export const getVisibilityState = () =>{
  var prefixes = ['webkit', 'moz', 'ms', 'o'];

  if ('visibilityState' in document) {
    return 'visibilityState';
  }

  for (var i = 0; i < prefixes.length; i++) {
    if ((prefixes[i] + 'VisibilityState') in document){
      return prefixes[i] + 'VisibilityState';
    }
  }
  // 找不到返回 null
  return null;
}

export const htmlToString = (htmlStr) =>{
  htmlStr = htmlStr.replace(/&mdash;*/g,"—");//替换破折号，"—" 不是中划线
  return htmlStr.replace(/<\/?.+?\/?>|&nbsp;|\r|\n|\s*/g, '')
}


export const getSystemDateYMD = () =>{
  let nowDate = new Date()
  let date = {
    year: nowDate.getFullYear(),
    month: nowDate.getMonth() + 1,
    date: nowDate.getDate()
  }
  if (date.month < 10) {
    date.month = '0' + date.month
  }
  if (date.date < 10) {
    date.date = '0' + date.date
  }
  let systemDate = date.year + '-' + date.month + '-' + date.date
  return systemDate
}
/**
 * 获取缩略图
 * @param path
 * @returns {null}
 */
export const getThumbnail = (path) =>{
  let thumbnail = null;
  if(path){
    const p1 = path.substring(0,path.lastIndexOf("."));
    const p = path.substring(path.lastIndexOf("."));
    thumbnail = p1+"_150x150"+p;
  }
  return thumbnail
}

/**
 * 过滤空数据
 * @param obj
 */
export const clearDeep = (obj) =>  {
  if (!obj || !typeof obj === 'object') return

  const keys = Object.keys(obj)
  for (var key of keys) {
    const val = obj[key]
    if (
      typeof val === 'undefined' ||
      ((typeof val === 'object' || typeof val === 'string') && !val)
    ) {
      // 如属性值为null或undefined或''，则将该属性删除
      delete obj[key]
    } else if (typeof val === 'object') {
      // 属性值为对象，递归调用
      clearDeep(obj[key])

      if (Object.keys(obj[key]).length === 0) {
        // 如某属性的值为不包含任何属性的独享，则将该属性删除
        delete obj[key]
      }
    }
  }
}

//文件流下载
export const download = function(content,fileName,fileType) {
  const blob = new Blob([content]) // 创建一个类文件对象：Blob对象表示一个不可变的、原始数据的类文件对象
  window.URL = window.URL || window.webkitURL;
  const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
  let dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
  dom.style.display = 'none'
  dom.href = url
  dom.download = fileName+(fileType?"."+fileType:'');
  document.body.appendChild(dom)
  dom.click()
  document.body.removeChild(dom)
}
//跨域地址下载
export const downloadUrl = function(url,fileName,fileType) {
  const x = new XMLHttpRequest();
  x.open('GET', url, true);
  x.responseType = 'blob';
  x.onload = () => {
    window.URL = window.URL || window.webkitURL;
    const _url = window.URL.createObjectURL(x.response);
    const a = document.createElement('a');
    a.style.display = 'none'
    a.href = _url;
    a.download = fileName+(fileType?"."+fileType:'');
    document.body.appendChild(a)
    a.click();
    document.body.removeChild(a)
  };
  x.send();
}


