<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message, type TableColumnsType } from 'ant-design-vue'
import type { TablePaginationConfig } from 'ant-design-vue'
import { deleteUser, listUserVoByPage } from '@/api/userController'
import type { UserQueryRequest, UserVO } from '@/types/user'
import { getRequestErrorMessage } from '@/utils/requestError'

const loading = ref(false)
const deletingId = ref<number | string>()
const users = ref<UserVO[]>([])
const total = ref(0)
const query = reactive<UserQueryRequest>({
  userAccount: '',
  userName: '',
  pageNum: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

const columns: TableColumnsType<UserVO> = [
  { title: 'ID', dataIndex: 'id', width: 80 },
  { title: '账号', dataIndex: 'userAccount', width: 150 },
  { title: '昵称', dataIndex: 'userName', width: 140 },
  { title: '头像', dataIndex: 'userAvatar', width: 70 },
  { title: '简介', dataIndex: 'userProfile', width: 220 },
  { title: '角色', dataIndex: 'userRole', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', width: 170 },
  { title: '操作', key: 'action', fixed: 'right', width: 80 },
]

const pagination = computed<TablePaginationConfig>(() => ({
  current: query.pageNum,
  pageSize: query.pageSize,
  total: total.value,
  showSizeChanger: true,
  showTotal: (count) => `共 ${count} 条`,
}))

function getRowKey(record: UserVO) {
  return String(record.id)
}

function formatDate(value?: string) {
  if (!value) return '-'

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value

  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  }).format(date)
}

async function loadUsers() {
  loading.value = true

  try {
    const response = await listUserVoByPage({ ...query })
    const result = response.data

    if (result.code !== 200 || !result.data) {
      users.value = []
      total.value = 0
      void message.error(result.message || '获取用户列表失败')
      return
    }

    users.value = result.data.records ?? []
    total.value = result.data.totalRow ?? result.data.total ?? 0
  } catch (error) {
    users.value = []
    total.value = 0
    void message.error(getRequestErrorMessage(error, '获取用户列表失败'))
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.pageNum = 1
  void loadUsers()
}

function handleReset() {
  query.userAccount = ''
  query.userName = ''
  query.pageNum = 1
  query.pageSize = 10
  void loadUsers()
}

function handleTableChange(page: TablePaginationConfig) {
  query.pageNum = page.current ?? 1
  query.pageSize = page.pageSize ?? 10
  void loadUsers()
}

async function handleDelete(id?: number | string) {
  if (id === undefined) return

  deletingId.value = id

  try {
    const response = await deleteUser({ id })
    const result = response.data

    if (result.code !== 200 || !result.data) {
      void message.error(result.message || '删除用户失败')
      return
    }

    if (users.value.length === 1 && (query.pageNum ?? 1) > 1) {
      query.pageNum = (query.pageNum ?? 1) - 1
    }

    void message.success('删除成功')
    await loadUsers()
  } catch (error) {
    void message.error(getRequestErrorMessage(error, '删除用户失败'))
  } finally {
    deletingId.value = undefined
  }
}

onMounted(() => {
  void loadUsers()
})
</script>

<template>
  <main class="user-manage-page" aria-labelledby="user-manage-title">
    <header class="page-heading">
      <p>系统管理</p>
      <h1 id="user-manage-title">用户管理</h1>
    </header>

    <section class="search-section" aria-label="搜索用户">
      <a-form class="search-form" layout="inline" :model="query" @finish="handleSearch">
        <a-form-item label="账号">
          <a-input v-model:value="query.userAccount" allow-clear placeholder="请输入账号" />
        </a-form-item>
        <a-form-item label="昵称">
          <a-input v-model:value="query.userName" allow-clear placeholder="请输入昵称" />
        </a-form-item>
        <a-form-item class="search-actions">
          <a-space>
            <a-button type="primary" html-type="submit">搜索</a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </section>

    <section class="table-section" aria-label="用户列表">
      <a-table
        :columns="columns"
        :data-source="users"
        :loading="loading"
        :pagination="pagination"
        :row-key="getRowKey"
        :scroll="{ x: 1010 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'userAccount'">
            <span class="primary-cell">{{ record.userAccount || '-' }}</span>
          </template>
          <template v-else-if="column.dataIndex === 'userName'">
            {{ record.userName || '-' }}
          </template>
          <template v-else-if="column.dataIndex === 'userAvatar'">
            <a-avatar :src="record.userAvatar">
              {{ (record.userName || record.userAccount || '用').slice(0, 1) }}
            </a-avatar>
          </template>
          <template v-else-if="column.dataIndex === 'userProfile'">
            <span class="profile-cell" :title="record.userProfile || ''">
              {{ record.userProfile || '-' }}
            </span>
          </template>
          <template v-else-if="column.dataIndex === 'userRole'">
            <a-tag :color="record.userRole === 'admin' ? 'red' : 'blue'">
              {{ record.userRole === 'admin' ? '管理员' : '普通用户' }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-popconfirm
              title="确定删除这个用户吗？"
              ok-text="删除"
              cancel-text="取消"
              @confirm="handleDelete(record.id)"
            >
              <a-button type="link" danger :loading="deletingId === record.id">删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </section>
  </main>
</template>

<style scoped>
.user-manage-page {
  padding-bottom: 20px;
}

.page-heading {
  padding: 12px 0 24px;
}

.page-heading p {
  margin: 0 0 6px;
  color: #b24f3f;
  font-size: 14px;
  font-weight: 700;
}

.page-heading h1 {
  margin: 0;
  color: #173a35;
  font-size: 30px;
}

.search-section {
  padding: 20px;
  border: 1px solid #dce3e1;
  border-bottom: 0;
  border-radius: 8px 8px 0 0;
  background: #ffffff;
}

.search-form {
  row-gap: 12px;
}

.search-form :deep(.ant-form-item) {
  margin-bottom: 0;
}

.search-actions :deep(.ant-btn-primary) {
  background: #0f7567;
  border-color: #0f7567;
}

.table-section {
  overflow: hidden;
  border: 1px solid #dce3e1;
  border-radius: 0 0 8px 8px;
  background: #ffffff;
}

.table-section :deep(.ant-table-wrapper),
.table-section :deep(.ant-table) {
  border-radius: 0;
}

.primary-cell {
  color: #173a35;
  font-weight: 600;
}

.profile-cell {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 640px) {
  .search-form {
    display: grid;
    grid-template-columns: 1fr;
  }

  .search-form :deep(.ant-form-item) {
    display: grid;
    grid-template-columns: 54px minmax(0, 1fr);
  }

  .search-actions :deep(.ant-form-item-control) {
    grid-column: 2;
  }
}
</style>
