<template>
  <div class="notice-management">
    <div class="page-header">
      <h2>公告管理</h2>
      <el-button type="primary" @click="showAddDialog">发布公告</el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入公告标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="已发布" :value="1"></el-option>
            <el-option label="已下线" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadNotices">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 公告列表 -->
    <el-table :data="notices" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="标题" min-width="200"></el-table-column>
      <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '已发布' : '已下线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button size="mini" @click="editNotice(scope.row)">编辑</el-button>
          <el-button 
            size="mini" 
            :type="scope.row.status === 1 ? 'warning' : 'success'"
            @click="toggleStatus(scope.row)"
          >
            {{ scope.row.status === 1 ? '下线' : '上线' }}
          </el-button>
          <el-button size="mini" type="danger" @click="deleteNotice(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      >
      </el-pagination>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="noticeForm" :rules="rules" ref="noticeForm" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            type="textarea"
            :rows="6"
            v-model="noticeForm.content"
            placeholder="请输入公告内容"
          ></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="noticeForm.status">
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveNotice" :loading="saving">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'NoticeManagement',
  data() {
    return {
      loading: false,
      saving: false,
      notices: [],
      searchForm: {
        title: '',
        status: ''
      },
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      isEdit: false,
      noticeForm: {
        id: null,
        title: '',
        content: '',
        status: 1
      },
      rules: {
        title: [
          { required: true, message: '请输入公告标题', trigger: 'blur' },
          { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入公告内容', trigger: 'blur' },
          { min: 1, max: 1000, message: '内容长度在 1 到 1000 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑公告' : '发布公告'
    }
  },
  mounted() {
    this.loadNotices()
  },
  methods: {
    async loadNotices() {
      this.loading = true
      try {
        // 模拟数据
        const mockData = {
          records: [
            {
              id: 1,
              title: '欢迎使用青春校园二手平台',
              content: '欢迎大家使用青春校园二手平台，让闲置物品重新焕发价值！',
              status: 1,
              createTime: '2024-01-15 10:30:00'
            },
            {
              id: 2,
              title: '平台使用规范',
              content: '为了维护良好的交易环境，请大家遵守平台使用规范...',
              status: 1,
              createTime: '2024-01-14 09:20:00'
            }
          ],
          total: 2,
          current: 1,
          size: 10
        }
        this.notices = mockData.records
        this.pagination.total = mockData.total
      } catch (error) {
        this.$message.error('加载公告列表失败')
      } finally {
        this.loading = false
      }
    },
    showAddDialog() {
      this.isEdit = false
      this.dialogVisible = true
    },
    editNotice(notice) {
      this.isEdit = true
      this.noticeForm = { ...notice }
      this.dialogVisible = true
    },
    async saveNotice() {
      this.$refs.noticeForm.validate(async (valid) => {
        if (valid) {
          this.saving = true
          try {
            // 这里应该调用API保存公告
            await new Promise(resolve => setTimeout(resolve, 1000))
            this.$message.success(this.isEdit ? '更新成功' : '发布成功')
            this.dialogVisible = false
            this.loadNotices()
          } catch (error) {
            this.$message.error('保存失败')
          } finally {
            this.saving = false
          }
        }
      })
    },
    async toggleStatus(notice) {
      try {
        // 这里应该调用API切换状态
        await new Promise(resolve => setTimeout(resolve, 500))
        notice.status = notice.status === 1 ? 0 : 1
        this.$message.success('状态更新成功')
      } catch (error) {
        this.$message.error('状态更新失败')
      }
    },
    deleteNotice(notice) {
      this.$confirm('确定要删除这条公告吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 这里应该调用API删除公告
          await new Promise(resolve => setTimeout(resolve, 500))
          this.$message.success('删除成功')
          this.loadNotices()
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    resetSearch() {
      this.searchForm = {
        title: '',
        status: ''
      }
      this.loadNotices()
    },
    resetForm() {
      this.noticeForm = {
        id: null,
        title: '',
        content: '',
        status: 1
      }
      this.$refs.noticeForm && this.$refs.noticeForm.resetFields()
    },
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadNotices()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadNotices()
    }
  }
}
</script>

<style scoped>
.notice-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-area {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}
</style>