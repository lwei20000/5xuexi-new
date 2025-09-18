/* 菜单路由 */
export default {
  login: { _name: 'Login' },
  forget: { _name: 'Forget' },
  dashboard: {
    _name: 'Dashboard',
    workplace: { _name: 'Workplace' },
    analysis: { _name: 'Analysis' },
    monitor: { _name: 'Monitor' }
  },
  system: {
    _name: 'System',
    user: {
      _name: 'User',
      details: { _name: '' }
    },
    role: { _name: 'Role' },
    menu: { _name: 'Menu' },
    dictionary: { _name: 'Dictionary' },
    org: { _name: 'Organization' },
    tenant: { _name: 'Tenant' },
    loginRecord: { _name: 'LoginRecord' },
    operationRecord: { _name: 'OperationRecord' },
    file: { _name: 'File' },
    userFile: { _name: 'Personal File' },
    defaultMenu: { _name: 'Default Menu' },
    defaultRole: { _name: 'Default Role' }
  },
  base: {
    _name: 'Basic Information',
    course: {_name: 'Course'},
    major: { _name: 'Major' },
    majorCourse: { _name: 'Major Course' },
    userMajor: { _name: 'User Major' }
  },
  form: {
    _name: 'Form',
    basic: { _name: 'Basic Form' },
    advanced: { _name: 'Advanced Form' },
    step: { _name: 'Step Form' }
  },
  list: {
    _name: 'List',
    basic: {
      _name: 'Basic List',
      add: { _name: 'UserAdd' },
      edit: { _name: 'UserEdit' },
      details: {
        ':id': { _name: '' }
      }
    },
    advanced: { _name: 'Advanced List' },
    card: {
      _name: 'Card List',
      project: { _name: 'Project' },
      application: { _name: 'Application' },
      article: { _name: 'Article' }
    },
    userInfo: {
      ':id': { _name: '' }
    }
  },
  result: {
    _name: 'Result',
    success: { _name: 'Success' },
    fail: { _name: 'Fail' }
  },
  exception: {
    _name: 'Exception',
    403: { _name: '403' },
    404: { _name: '404' },
    500: { _name: '500' }
  },
  user: {
    _name: 'User',
    profile: { _name: 'Profile' },
    message: { _name: 'Message' }
  },
  extension: {
    _name: 'Extension',
    icon: { _name: 'Icon' },
    tag: { _name: 'Tags' },
    dialog: { _name: 'DragDialog' },
    file: { _name: 'FileList' },
    upload: { _name: 'ImageUpload' },
    dragsort: { _name: 'DragSort' },
    message: { _name: 'Message' },
    regions: { _name: 'CitySelect' },
    printer: { _name: 'Printer' },
    excel: { _name: 'Excel' },
    countUp: { _name: 'CountUp' },
    empty: { _name: 'Empty' },
    steps: { _name: 'Steps' },
    menu: { _name: 'Menu' },
    treeSelect: { _name: 'TreeSelect' },
    tableSelect: { _name: 'TableSelect' },
    player: { _name: 'Player' },
    map: { _name: 'Map' },
    qrCode: { _name: 'QRCode' },
    barCode: { _name: 'BarCode' },
    editor: { _name: 'Editor' },
    markdown: { _name: 'Markdown' }
  },
  example: {
    _name: 'Example',
    table: { _name: 'ProTable' },
    menuBadge: { _name: 'MenuBadge' },
    eleadmin: { _name: 'IFrame' },
    eleadminDoc: { _name: 'IFrame2' },
    choose: { _name: 'Choose' },
    document: { _name: 'Document' }
  },
  'https://eleadminCom/goods/8': { _name: 'Authorization' }
};
