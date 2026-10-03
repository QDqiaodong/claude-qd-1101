<script setup>
import { ref, onMounted, defineEmits, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const emit = defineEmits(['changed'])

const list = ref([])
const materials = ref([])
const dialog = ref(false)
const editing = ref(null)
const form = ref({ name: '', price: 0, items: [] })

const materialMap = computed(() => {
  const m = {}
  materials.value.forEach((x) => { m[x.id] = x })
  return m
})
const activeMaterials = computed(() => materials.value.filter((x) => x.status === '在售'))

async function load() {
  const [bq, fm] = await Promise.all([api.listBouquets(), api.listFlowerMaterials()])
  list.value = bq
  materials.value = fm
}
onMounted(load)

function openCreate() {
  editing.value = null
  form.value = { name: '', price: 0, items: [{ flowerMaterialId: null, qty: 1 }] }
  dialog.value = true
}
function openEdit(b) {
  editing.value = b.id
  form.value = { name: b.name, price: b.price, items: [] }
  dialog.value = true
}
function addItem() { form.value.items.push({ flowerMaterialId: null, qty: 1 }) }
function removeItem(i) { form.value.items.splice(i, 1) }

async function save() {
  try {
    if (editing.value) {
      await api.updateBouquet(editing.value, { name: form.value.name, price: Number(form.value.price) })
    } else {
      const items = form.value.items.map((it) => ({ flowerMaterialId: it.flowerMaterialId, qty: Number(it.qty) }))
      await api.createBouquet({ name: form.value.name, price: Number(form.value.price), items })
    }
    ElMessage.success('已保存')
    dialog.value = false
    await load()
    emit('changed')
  } catch (e) { ElMessage.error(e.message) }
}
</script>

<template>
  <div>
    <div class="bar">
      <h2 class="title">花束配方</h2>
      <el-button type="primary" @click="openCreate">新增花束</el-button>
    </div>

    <div class="grid">
      <el-card v-for="b in list" :key="b.id" class="card" shadow="hover">
        <div class="card-body">
          <div class="card-head">
            <span class="name">{{ b.name }}</span>
            <span class="price">¥{{ b.price }}</span>
          </div>
          <div class="recipe">
            <div v-for="it in b.items" :key="it.id" class="recipe-row">
              <span>{{ materialMap[it.flowerMaterialId] ? materialMap[it.flowerMaterialId].name : '花材#' + it.flowerMaterialId }}</span>
              <span class="qty">×{{ it.qty }}</span>
            </div>
            <div v-if="!b.items || b.items.length === 0" class="empty">暂无配方</div>
          </div>
        </div>
        <div class="overlay">
          <el-button size="small" @click="openEdit(b)">编辑</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog :title="editing ? '编辑花束' : '新增花束'" v-model="dialog" width="480px">
      <el-form label-width="80px">
        <el-form-item label="花束名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="售价"><el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="配方" v-if="!editing">
          <div class="items">
            <div v-for="(it, i) in form.items" :key="i" class="item-row">
              <el-select v-model="it.flowerMaterialId" placeholder="选择花材" style="flex:1">
                <el-option v-for="m in activeMaterials" :key="m.id" :label="m.name" :value="m.id" />
              </el-select>
              <el-input-number v-model="it.qty" :min="1" />
              <el-button text type="danger" @click="removeItem(i)">删</el-button>
            </div>
            <el-button text type="primary" @click="addItem">+ 添加花材</el-button>
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
.bar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.title { margin: 0; font-size: 20px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 16px; }
.card { position: relative; border-radius: 14px; }
.card-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.name { font-size: 16px; font-weight: 700; }
.price { color: var(--primary-deep); font-weight: 700; }
.recipe { display: flex; flex-direction: column; gap: 6px; }
.recipe-row { display: flex; justify-content: space-between; color: var(--muted); font-size: 13px; }
.qty { font-weight: 600; color: var(--ink); }
.empty { color: var(--muted); font-size: 13px; }
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
