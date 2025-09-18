<template>
  <ele-modal :lock-scroll="false"
    width="460px"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    custom-class="ele-dialog-form"
    title="移动到文件夹"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="102px">
      <el-form-item label="文件夹名称:" prop="parentId">
      <ele-tree-select
        clearable
        v-model="form.parentId"
        :data="directoryList"
        label-key="name"
        value-key="id"
        default-expand-all
        placeholder="请选择文件夹"
      />
      </el-form-item>
    </el-form>
    <template v-slot:footer>
      <el-button @click="updateVisible(false)">取消</el-button>
      <el-button type="primary" :loading="loading" @click="save">
        保存
      </el-button>
    </template>
  </ele-modal>
</template>

<script>
  import { updateUserFile,listUserFiles } from '@/api/system/user-file';

  export default {
    props: {
      // 弹窗是否打开
      visible: Boolean,
      // 文件数据
      data: Object
    },
    data() {
      return {
        directoryList:[],
        // 表单数据
        form: {
          parentId: null
        },
        // 表单验证规则
        rules: {
          parentId: [
            {
              required: true,
              message: '请输选择文件夹',
              trigger: 'blur'
            }
          ]
        },
        // 提交状态
        loading: false
      };
    },
    // created() {
    //   listUserFiles({
    //     isDirectory: 1
    //   }).then((list) => {
    //       this.loading = false;
    //       list.push({id:0,name:"最上级"});
    //       this.directoryList = this.$util.toTreeData({
    //         data: list,
    //         idField: 'id',
    //         parentIdField: 'parentId'
    //       });
    //
    //     }).catch((e) => {
    //       this.loading = false;
    //       this.$message.error(e.message);
    //     });
    // },
    methods: {
      /* 保存编辑 */
      save() {
        this.$refs.form.validate((valid) => {
          if (!valid) {
            return false;
          }
          this.loading = true;
          updateUserFile({ ...this.form, id: this.data?.id })
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
      /* 更新 visible */
      updateVisible(value) {
        this.$emit('update:visible', value);
      }
    },
    watch: {
      visible(visible) {
        if (!visible) {
          this.$refs.form.clearValidate();
          this.form.parentId = null;
        }else{
          listUserFiles({
            isDirectory: 1
          }).then((list) => {
            this.loading = false;
            list.push({id:0,name:"最上级"});
            this.directoryList = this.$util.toTreeData({
              data: list,
              idField: 'id',
              parentIdField: 'parentId'
            });

          }).catch((e) => {
            this.loading = false;
            this.$message.error(e.message);
          });
        }
      }
    }
  };
</script>
