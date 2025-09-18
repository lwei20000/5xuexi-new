<template>
  <div class="ele-body">
    <el-card shadow="never" header="基本信息">
      <el-form
        size="mini"
        v-loading="loading"
        label-width="100px"
        class="ele-form-detail"
      >
        <el-form-item label="账号:">
          <div class="ele-text-secondary">{{ user.username }}</div>
        </el-form-item>
        <el-form-item label="姓名:">
          <div class="ele-text-secondary">{{ user.realname }}</div>
        </el-form-item>
        <el-form-item label="性别:">
          <div class="ele-text-secondary">{{ user.sex }}</div>
        </el-form-item>
        <el-form-item label="手机号:">
          <div class="ele-text-secondary">{{ user.phone }}</div>
        </el-form-item>
        <el-form-item label="身份证号:">
          <div class="ele-text-secondary">{{ user.idCard }}</div>
        </el-form-item>
        <el-form-item label="民族:">
          <div class="ele-text-secondary">{{ user.nation }}</div>
        </el-form-item>
        <el-form-item label="政治面貌:">
          <div class="ele-text-secondary">{{ user.politics }}</div>
        </el-form-item>
        <el-form-item label="工作单位:">
          <div class="ele-text-secondary">{{ user.organization }}</div>
        </el-form-item>
        <el-form-item label="家庭地址:">
          <div class="ele-text-secondary">{{ user.address }}</div>
        </el-form-item>
        <el-form-item label="角色:">
          <el-tag
            v-for="item in user.roles"
            :key="item.roleId"
            size="mini"
            type="primary"
            :disable-transitions="true"
          >
            {{ item.roleName }}
          </el-tag>
        </el-form-item>
        <el-form-item label="站点:">
          <el-tag
            v-for="item in user.orgs"
            :key="item.orgId"
            size="mini"
            type="primary"
            :disable-transitions="true"
          >
            {{ item.orgName }}
          </el-tag>
        </el-form-item>
        <el-form-item label="分组:">
          <el-tag
            v-for="item in user.groups"
            :key="item.groupId"
            size="mini"
            type="primary"
            :disable-transitions="true"
          >
            {{ item.groupName }}
          </el-tag>
        </el-form-item>
        <el-form-item label="创建时间:">
          <div class="ele-text-secondary">{{ user.createTime }}</div>
        </el-form-item>
        <el-form-item label="状态:">
          <div class="ele-text-secondary">
            <ele-dot v-if="user.status === 0" text="正常" />
            <ele-dot v-else type="danger" :ripple="false" text="冻结" />
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
  import { setPageTabTitle } from '@/utils/page-tab-util';
  import { getUser } from '@/api/system/user';
  const ROUTE_PATH = '/system/user/details';

  export default {
    name: 'SystemUserDetails',
    data() {
      return {
        user: {
          nation:'',
          politics:'',
          organization:'',
          address:'',
          username: '',
          realname: '',
          idCard:'',
          sex: '',
          phone: '',
          roles: [],
          orgs: [],
          groups: [],
          createTime: '',
          status: 0
        },
        loading: true
      };
    },
    methods: {
      query() {
        const id = this.$route.query.id;
        if (!id || this.user.userId === Number(id)) {
          return;
        }
        this.loading = true;
        getUser(Number(id))
          .then((data) => {
            this.loading = false;
            if(data){
              this.$util.assignObject(this.user, {
                ...data,
                createTime: this.$util.toDateString(data.createTime)
              });
              // 修改页签标题
              if (this.$route.path === ROUTE_PATH) {
                setPageTabTitle(data.realname + '的信息');
              }
            }else{
              this.$util.assignObject(this.user, {})
            }
          })
          .catch((e) => {
            this.loading = false;
            this.$message.error(e.message);
          });
      }
    },
    watch: {
      $route: {
        handler(route) {
          const { path } = route;
          if (path !== ROUTE_PATH) {
            return;
          }
          this.query();
        },
        immediate: true
      }
    }
  };
</script>
