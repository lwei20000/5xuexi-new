<!-- 搜索表单 -->
<template>
  <el-form
    label-width="78px"
    class="ele-form-search"
    @keyup.enter.native="search"
    @submit.native.prevent
  >
    <el-row :gutter="15">
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="招生年份:" v-model="where.majorId">
          <el-date-picker
            type="year"
            class="ele-fluid"
            v-model="where.majorYear"
            value-format="yyyy"
            placeholder="请选择年份"
          />
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="学年:">
          <el-date-picker
            type="year"
            class="ele-fluid"
            v-model="where.academicYear"
            value-format="yyyy"
            placeholder="请选择学年"
          />
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="上/下学年:">
          <el-select class="ele-block" clearable v-model="where.academicSemester" placeholder="请选择上/下学年">
            <el-option label="上学年" value="1" />
            <el-option label="下学年" value="0" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="用户:">
          <user-select ref="userSelect" :multiple="false"  v-model="where.userId"></user-select>
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="专业:">
          <major-select ref="majorSelect" v-model="where.majorId"></major-select>
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="学期:">
          <dict-data-select :params="{dictCode: 'semester'}" v-model="where.semester" />
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="课程:">
          <course-select ref="courseSelect" v-model="where.courseId" ></course-select>
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="站点:">
          <org-tree
            v-model="where.orgId"
            :multiple="false"
            :checkStrictly="true"
            :expandOnClickNode="false"
            placeholder="请选择站点"
          />
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <el-form-item label="状态:">
          <el-select class="ele-block" clearable v-model="where.status" placeholder="请选择状态">
            <el-option label="提交已批改" value="2" />
            <el-option label="提交未批改" value="1" />
            <el-option label="答题进行中" value="0" />
            <el-option label="未考试" value="3" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col v-bind="styleResponsive ? { lg: 6, md: 12 } : { span: 6 }">
        <div class="ele-form-actions">
          <el-button
            type="primary"
            icon="el-icon-search"
            class="ele-btn-icon"
            @click="search"
          >
            查询
          </el-button>
          <el-button @click="reset">重置</el-button>
        </div>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
import UserSelect from "@/views/system/user/components/user-select";
import MajorSelect from '@/views/base/major/components/major-select.vue';
import CourseSelect from '@/views/base/course/components/course-select.vue';
import OrgTree from '@/views/system/org/components/org-tree.vue';
import DictDataSelect from '@/views/system/dictionary/components/dict-data-select.vue';
  export default {
    components: { UserSelect, MajorSelect ,CourseSelect,OrgTree,DictDataSelect},
    data() {
      // 默认表单数据
      const defaultWhere = {
        userId: undefined,
        majorId: undefined,
        courseId:undefined,
        semester:'',
        orgId:'',
        academicYear:'',
        academicSemester:'',
        status:''
      };
      return {
        // 表单数据
        where: { ...defaultWhere }
      };
    },
    computed: {
      // 是否开启响应式布局
      styleResponsive() {
        return this.$store.state.theme.styleResponsive;
      }
    },
    methods: {
      /* 搜索 */
      search() {
        this.$emit('search', this.where);
      },
      /*  重置 */
      reset() {
        this.where = { ...this.defaultWhere };
        this.search();
      }
    }
  };
</script>
