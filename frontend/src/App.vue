<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import router from './router'
import api from './api'

import FlowerMaterialView from './views/FlowerMaterialView.vue'
import BouquetView from './views/BouquetView.vue'
import CustomerOrderView from './views/CustomerOrderView.vue'
import DeliveryView from './views/DeliveryView.vue'

// ------------------------------------------------------------------
// 导航形态：左侧竖排导航（el-menu mode="vertical"）。
// 四个业务模块竖向排列，点击切换；导航项右侧显示各模块计数。
// ------------------------------------------------------------------

const modules = [
  { path: '/flower-materials', label: '花材台账', icon: '🌸', comp: FlowerMaterialView },
  { path: '/bouquets',         label: '花束配方', icon: '💐', comp: BouquetView },
  { path: '/customer-orders',  label: '客户订单', icon: '🧾', comp: CustomerOrderView },
  { path: '/deliveries',       label: '今日配送', icon: '🚚', comp: DeliveryView }
]

const currentPath = ref(router.currentRoute.value.path)
const counts = ref({})

const currentView = computed(() => {
  const m = modules.find((mm) => mm.path === currentPath.value)
  return m ? m.comp : FlowerMaterialView
})

async function loadCounts() {
  try {
    const [fm, bq, co, dv] = await Promise.all([
      api.listFlowerMaterials(), api.listBouquets(),
      api.listCustomerOrders(), api.listDeliveries()
    ])
    counts.value = {
      '/flower-materials': fm.length,
      '/bouquets': bq.length,
      '/customer-orders': co.length,
      '/deliveries': dv.length
    }
  } catch (e) { /* 计数失败不影响主流程 */ }
}

function go(path) {
  router.push(path)
}

watch(() => router.currentRoute.value.path, (p) => { currentPath.value = p })

onMounted(async () => {
  currentPath.value = router.currentRoute.value.path
  await loadCounts()
})
</script>

<template>
  <el-container class="app-shell">
    <el-aside width="220px" class="sidebar">
      <div class="brand">
        <span class="brand-mark">花</span>
        <div class="brand-text">
          <div class="brand-title">花店</div>
          <div class="brand-sub">花材与花束管理</div>
        </div>
      </div>

      <el-menu
        class="nav"
        mode="vertical"
        :default-active="currentPath"
        @select="go"
      >
        <el-menu-item
          v-for="m in modules"
          :key="m.path"
          :index="m.path"
        >
          <span class="nav-icon">{{ m.icon }}</span>
          <span class="nav-label">{{ m.label }}</span>
          <span
            v-if="counts[m.path] !== undefined"
            class="nav-count"
          >{{ counts[m.path] }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-main class="content">
      <component :is="currentView" @changed="loadCounts" />
    </el-main>
  </el-container>
</template>

<style scoped>
.app-shell { min-height: 100vh; }

.sidebar {
  background: #fff;
  border-right: 1px solid var(--line);
  box-shadow: 2px 0 12px rgba(224, 64, 138, 0.06);
  display: flex;
  flex-direction: column;
}

.brand {
  display: flex; align-items: center; gap: 12px;
  padding: 20px 18px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-deep) 100%);
  color: #fff;
}
.brand-mark {
  width: 40px; height: 40px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  background: rgba(255,255,255,0.2); font-weight: 800; font-size: 22px;
}
.brand-title { font-size: 18px; font-weight: 700; }
.brand-sub { font-size: 12px; opacity: 0.85; }

.nav { border-right: none; flex: 1; padding-top: 8px; }
.nav :deep(.el-menu-item) {
  height: 54px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.nav-icon { font-size: 18px; }
.nav-label { flex: 1; font-size: 15px; }
.nav-count {
  min-width: 22px; height: 22px; padding: 0 6px;
  border-radius: 11px;
  background: var(--primary-wash); color: var(--primary-deep);
  font-size: 12px; font-weight: 600;
  display: flex; align-items: center; justify-content: center;
}
.nav :deep(.el-menu-item.is-active) {
  background: var(--primary-wash);
  color: var(--primary-deep);
  font-weight: 600;
}

.content { padding: 22px 26px; background: var(--bg); }
</style>
