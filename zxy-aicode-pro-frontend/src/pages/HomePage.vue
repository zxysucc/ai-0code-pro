<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { health } from '@/api/healthController'

type ServiceState = 'checking' | 'online' | 'offline'

const serviceState = ref<ServiceState>('checking')

const statusText = computed(() => {
  if (serviceState.value === 'online') return '服务正常'
  if (serviceState.value === 'offline') return '连接失败'
  return '检测中'
})

const statusColor = computed(() => {
  if (serviceState.value === 'online') return 'success'
  if (serviceState.value === 'offline') return 'error'
  return 'processing'
})

const checkHealth = async () => {
  serviceState.value = 'checking'

  try {
    const response = await health()
    const result = response.data
    serviceState.value = result.code === 200 && result.data === 'ok' ? 'online' : 'offline'
  } catch {
    serviceState.value = 'offline'
  }
}

onMounted(() => {
  void checkHealth()
})
</script>

<template>
  <main id="home-page" aria-labelledby="page-title">
    <header class="page-heading">
      <p class="product-label">应用工作台</p>
      <h1 id="page-title">AI 零代码应用生成平台</h1>
      <p class="page-summary">项目基础环境已就绪，可以开始接入应用生成能力。</p>
    </header>

    <section class="workspace" aria-label="开发环境状态">
      <a-card class="status-card" title="服务状态" :bordered="false">
        <div class="status-row">
          <div>
            <p class="status-name">后端 API</p>
            <p class="status-address">127.0.0.1:8123/api</p>
          </div>
          <a-tag :color="statusColor">{{ statusText }}</a-tag>
        </div>

        <div class="status-actions">
          <a-button :loading="serviceState === 'checking'" @click="checkHealth">
            重新检测
          </a-button>
          <a-button href="http://127.0.0.1:8123/api/doc.html" target="_blank"> 接口文档 </a-button>
        </div>
      </a-card>

      <a-card class="status-card" title="工程能力" :bordered="false">
        <a-descriptions :column="1" size="small">
          <a-descriptions-item label="界面">Vue 3 + Ant Design Vue</a-descriptions-item>
          <a-descriptions-item label="状态">Pinia</a-descriptions-item>
          <a-descriptions-item label="接口">OpenAPI 自动生成</a-descriptions-item>
          <a-descriptions-item label="请求">Axios + Vite Proxy</a-descriptions-item>
        </a-descriptions>
      </a-card>
    </section>
  </main>
</template>

<style scoped>
.page-heading {
  max-width: 760px;
  padding: 36px 0 32px;
}

.product-label {
  margin: 0 0 8px;
  color: #b24f3f;
  font-size: 14px;
  font-weight: 700;
}

h1 {
  margin: 0;
  color: #173a35;
  font-size: clamp(32px, 5vw, 52px);
  line-height: 1.16;
}

.page-summary {
  margin: 16px 0 0;
  color: #52635f;
  font-size: 17px;
  line-height: 1.7;
}

.workspace {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(280px, 0.9fr);
  gap: 20px;
}

.status-card {
  min-height: 230px;
  border: 1px solid #dce3e1;
  border-radius: 8px;
  box-shadow: none;
}

.status-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  padding: 12px 0 24px;
}

.status-name {
  margin: 0 0 6px;
  color: #1f2a27;
  font-size: 17px;
  font-weight: 650;
}

.status-address {
  margin: 0;
  color: #6a7874;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 13px;
  overflow-wrap: anywhere;
}

.status-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 760px) {
  .page-heading {
    padding-top: 20px;
  }

  .workspace {
    grid-template-columns: 1fr;
  }

  .status-card {
    min-height: 0;
  }
}
</style>
