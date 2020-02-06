<template>
  <div class="app-container">
    <div class="filter-container">
      <h1>{{ name }}
        <el-button v-if="checkPermission('user:add')" type="primary" style="float:right;">添加</el-button>
      </h1>
    </div>
    <el-table
      v-loading.body="listLoading"
      :data="list"
      element-loading-text="拼命加载中"
      border
      fit
      highlight-current-row
    >
      <el-table-column align="center" label="序号" width="60">
        <template slot-scope="scope">
          <span v-text="scope.$index"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="昵称" prop="nickname" />
      <el-table-column align="center" label="用户名" prop="account" />
      <el-table-column align="center" label="角色" >
        <template slot-scope="scope">
          <el-tag type="success" v-text="scope.row.roles" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createtime" />
      <el-table-column align="center" label="最近修改时间" prop="updatetime" />
      <el-table-column v-if="checkPermission('user:update')||checkPermission('user:delete')" align="center" label="操作">
        <template>
          <el-button v-if="checkPermission('user:update')" type="primary">修改</el-button>
          <el-button v-if="checkPermission('user:delete')" type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import checkPermission from '@/utils/permission' // 权限判断函数
import { fetchList } from '@/api/user'

export default {
  data() {
    return {
      totalCount: 0, // 分页组件--数据总条数
      list: [], // 表格的数据
      listLoading: false // 数据加载等待动画
    }
  },
  computed: {
    ...mapGetters([
      'name'
    ])
  },
  created() {
    this.getList()
  },
  methods: {
    checkPermission(value) {
      return checkPermission(value)
    },
    getList() {
      // 查询列表
      this.listLoading = true
      fetchList().then(response => {
        this.list = response.data
        setTimeout(() => {
          this.listLoading = false
        }, 500)
      })
    }
  }
}
</script>
