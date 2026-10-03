<script setup>
import { ref, onMounted, defineEmits, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const emit = defineEmits(['changed'])

const list = ref([])
const bouquets = ref([])
const dialog = ref(false)
const editing = ref(null)
const filter = ref('')
const form = ref({ customer: '', phone: '', items: [] })

const bouquetMap = computed(() => {
  const m = {}
  bouquets.value.forEach((b) => { m[b.id] = b })
  return m
})

async function load() {
  const [co, bq] = await Promise.all([
    api.listCustomerOrders(filter.value || null),
    api.listBouquets()
  ])
  list.value = co
  bouquets.value = bq
}
onMounted(load)

function openCreate() {
  editing.value = null
  form.value = { customer: '', phone: '', items: [{ bouquetId: null, qty: 1 }] }
  dialog.value = true
}
function openEdit(o) {
  editing.value = o.id
  form.value = { customer: o.customer, phone: o.phone, items: [] }
  dialog.value = true
}
function addItem() { form.value.items.push({ bouquetId: null, qty: 1 }) }
function removeItem(i) { form.value.items.splice(i, 1) }

async function save() {
  try {
    if (editing.value) {
      await api.updateCustomerOrder(editing.value, { customer: form.value.customer, phone: form.value.phone })
    } else {
      const items = form.value.items.map((it) => ({ bouquetId: it.bouquetId, qty: Number(it.qty) }))
      await api.createCustomerOrder({ customer: form.value.customer, phone: form.value.phone, items })
    }
    ElMessage.success('已保存')
    dialog.value = false
    await load()
    emit('changed')
  } catch (e) { ElMessage.error(e.message) }
}
async function cancel(o) {
  try { await api.cancelCustomerOrder(o.id); ElMessage.success('订单已取消'); await load(); emit('changed') }
  catch (e) { ElMessage.error(e.message) }
}
function statusType(s) {
  if (s === '已付款') return 'success'
  if (s === '已取消') return 'info'
  return 'warning'
}
</script>

<template>
  <div>
    <div class="bar">
      <h2 class="title">客户订单</h2>
      <el-button type="primary" @click="openCreate">新增订单</el-button>
    </div>

    <el-radio-group v-model="filter" @change="load" style="margin-bottom:14px">
      <el-radio-button label="">全部</el-radio-button>
      <el-radio-button label="待付款">待付款</el-radio-button>
      <el-radio-button label="已付款">已付款</el-radio-button>
      <el-radio-button label="已取消">已取消</el-radio-button>
    </el-radio-group>

    <div class="grid">
      <el-card v-for="o in list" :key="o.id" class="card" shadow="hover">
        <div class="card-body">
          <div class="card-head">
            <span class="name">{{ o.customer }}</span>
            <el-tag size="small" :type="statusType(o.status)">{{ o.status }}</el-tag>
          </div>
          <div class="phone">📞 {{ o.phone }}</div>
          <div class="recipe">
            <div v-for="it in o.items" :key="it.id" class="recipe-row">
              <span>{{ bouquetMap[it.bouquetId] ? bouquetMap[it.bouquetId].name : '花束#' + it.bouquetId }}</span>
              <span class="qty">×{{ it.qty }}</span>
            </div>
          </div>
          <div class="amount">合计 ¥{{ o.totalAmount }}</div>
        </div>
        <div class="overlay">
          <el-button size="small" @click="openEdit(o)">编辑</el-button>
          <el-button v-if="o.status === '待付款'" size="small" type="danger" @click="cancel(o)">取消订单</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog :title="editing ? '编辑订单' : '新增订单'" v-model="dialog" width="480px">
      <el-form label-width="80px">
        <el-form-item label="客户名"><el-input v-model="form.customer" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" maxlength="11" /></el-form-item>
        <el-form-item label="花束" v-if="!editing">
          <div class="items">
            <div v-for="(it, i) in form.items" :key="i" class="item-row">
              <el-select v-model="it.bouquetId" placeholder="选择花束" style="flex:1">
                <el-option v-for="b in bouquets" :key="b.id" :label="b.name" :value="b.id" />
              </el-select>
              <el-input-number v-model="it.qty" :min="1" />
              <el-button text type="danger" @click="removeItem(i)">删</el-button>
            </div>
            <el-button text type="primary" @click="addItem">+ 添加花束</el-button>
          </div>
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
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 16px; }
.card { position: relative; border-radius: 14px; }
.card-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.name { font-size: 16px; font-weight: 700; }
.phone { color: var(--muted); font-size: 13px; margin-bottom: 10px; }
.recipe { display: flex; flex-direction: column; gap: 6px; margin-bottom: 10px; }
.recipe-row { display: flex; justify-content: space-between; color: var(--muted); font-size: 13px; }
.qty { font-weight: 600; color: var(--ink); }
.amount { font-weight: 700; color: var(--primary-deep); }
.items { display: flex; flex-direction: column; gap: 10px; }
.item-row { display: flex; gap: 8px; align-items: center; }
.overlay {
  position: absolute; left: 0; right: 0; bottom: 0;
  display: flex; gap: 8px; justify-content: center;
  padding: 10px; background: rgba(252,237,239,0.96);
  border-radius: 0 0 14px 14px;
  opacity: 0; transform: translateY(8px); transition: all .18s ease;
}
.card:hover .overlay { opacity: 1; transform: translateY(0); }
</style>
