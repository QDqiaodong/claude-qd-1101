<script setup>
import { ref, onMounted, defineEmits, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const emit = defineEmits(['changed'])

const list = ref([])
const orders = ref([])
const dialog = ref(false)
const filter = ref('')
const form = ref({ orderId: null, address: '', expectSlot: '' })

const orderMap = computed(() => {
  const m = {}
  orders.value.forEach((o) => { m[o.id] = o })
  return m
})

async function load() {
  const [dv, co] = await Promise.all([
    api.listDeliveries(filter.value || null),
    api.listCustomerOrders()
  ])
  list.value = dv
  orders.value = co
}
onMounted(load)

function openCreate() {
  form.value = { orderId: null, address: '', expectSlot: '' }
  dialog.value = true
}
async function save() {
  try {
    await api.createDelivery({
      orderId: form.value.orderId,
      address: form.value.address,
      expectSlot: form.value.expectSlot
    })
    ElMessage.success('已创建配送单')
    dialog.value = false
    await load()
    emit('changed')
  } catch (e) { ElMessage.error(e.message) }
}
async function finish(d) {
  try { await api.finishDelivery(d.id); ElMessage.success('已标记完成'); await load(); emit('changed') }
  catch (e) { ElMessage.error(e.message) }
}
async function cancel(d) {
  try { await api.cancelDelivery(d.id); ElMessage.success('配送单已取消'); await load(); emit('changed') }
  catch (e) { ElMessage.error(e.message) }
}
function statusType(s) {
  if (s === '已完成') return 'success'
  if (s === '已取消') return 'info'
  if (s === '配送中') return 'warning'
  return 'primary'
}
</script>

<template>
  <div>
    <div class="bar">
      <h2 class="title">今日配送</h2>
      <el-button type="primary" @click="openCreate">新建配送</el-button>
    </div>

    <el-radio-group v-model="filter" @change="load" style="margin-bottom:14px">
      <el-radio-button label="">全部</el-radio-button>
      <el-radio-button label="待配送">待配送</el-radio-button>
      <el-radio-button label="配送中">配送中</el-radio-button>
      <el-radio-button label="已完成">已完成</el-radio-button>
    </el-radio-group>

    <div class="grid">
      <el-card v-for="d in list" :key="d.id" class="card" shadow="hover">
        <div class="card-body">
          <div class="card-head">
            <span class="name">订单 #{{ d.orderId }}</span>
            <el-tag size="small" :type="statusType(d.status)">{{ d.status }}</el-tag>
          </div>
          <div class="cust">{{ orderMap[d.orderId] ? orderMap[d.orderId].customer : '客户' }}</div>
          <div class="addr">📍 {{ d.address }}</div>
          <div class="slot">🕑 {{ d.expectSlot }}</div>
        </div>
        <div class="overlay">
          <el-button v-if="d.status === '配送中'" size="small" type="success" @click="finish(d)">标记完成</el-button>
          <el-button v-if="d.status === '待配送'" size="small" type="warning" @click="cancel(d)">取消配送</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog title="新建配送" v-model="dialog" width="460px">
      <el-form label-width="80px">
        <el-form-item label="订单">
          <el-select v-model="form.orderId" placeholder="选择订单" style="width:100%">
            <el-option v-for="o in orders" :key="o.id" :label="`${o.customer} · 订单#${o.id}`" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="期望时段">
          <el-select v-model="form.expectSlot" placeholder="选择时段" style="width:100%">
            <el-option label="09:00-11:00" value="09:00-11:00" />
            <el-option label="11:00-13:00" value="11:00-13:00" />
            <el-option label="13:00-15:00" value="13:00-15:00" />
            <el-option label="14:00-16:00" value="14:00-16:00" />
            <el-option label="16:00-18:00" value="16:00-18:00" />
            <el-option label="18:00-20:00" value="18:00-20:00" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.bar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
.title { margin: 0; font-size: 20px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 16px; }
.card { position: relative; border-radius: 14px; }
.card-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px; }
.name { font-size: 16px; font-weight: 700; }
.cust { font-weight: 600; margin-bottom: 6px; }
.addr { color: var(--muted); font-size: 13px; margin-bottom: 4px; }
.slot { color: var(--muted); font-size: 13px; }
.overlay {
  position: absolute; left: 0; right: 0; bottom: 0;
  display: flex; gap: 8px; justify-content: center;
  padding: 10px; background: rgba(252,237,239,0.96);
  border-radius: 0 0 14px 14px;
  opacity: 0; transform: translateY(8px); transition: all .18s ease;
}
.card:hover .overlay { opacity: 1; transform: translateY(0); }
</style>
