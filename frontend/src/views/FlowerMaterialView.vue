<script setup>
import { ref, onMounted, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const emit = defineEmits(['changed'])

const list = ref([])
const dialog = ref(false)
const editing = ref(null)
const form = ref({ name: '', category: '切花', unitPrice: 0, stock: 0, status: '在售' })

const categories = ['切花', '叶材', '辅材']

async function load() {
  list.value = await api.listFlowerMaterials()
}
onMounted(load)

function openCreate() {
  editing.value = null
  form.value = { name: '', category: '切花', unitPrice: 0, stock: 0, status: '在售' }
  dialog.value = true
}
function openEdit(m) {
  editing.value = m.id
  form.value = { name: m.name, category: m.category, unitPrice: m.unitPrice, stock: m.stock, status: m.status }
  dialog.value = true
}
async function save() {
  try {
    if (editing.value) {
      await api.updateFlowerMaterial(editing.value, {
        name: form.value.name, category: form.value.category,
        unitPrice: Number(form.value.unitPrice), stock: Number(form.value.stock), status: form.value.status
      })
    } else {
      await api.createFlowerMaterial({
        name: form.value.name, category: form.value.category,
        unitPrice: Number(form.value.unitPrice), stock: Number(form.value.stock)
      })
    }
    ElMessage.success('已保存')
    dialog.value = false
    await load()
    emit('changed')
  } catch (e) { ElMessage.error(e.message) }
}
async function disable(m) {
  try { await api.disableFlowerMaterial(m.id); ElMessage.success('已停用'); await load(); emit('changed') }
  catch (e) { ElMessage.error(e.message) }
}
async function enable(m) {
  try { await api.enableFlowerMaterial(m.id); ElMessage.success('已启用'); await load(); emit('changed') }
  catch (e) { ElMessage.error(e.message) }
}
</script>

<template>
  <div>
    <div class="bar">
      <h2 class="title">花材台账</h2>
      <el-button type="primary" @click="openCreate">新增花材</el-button>
    </div>

    <div class="grid">
      <el-card v-for="m in list" :key="m.id" class="card" shadow="hover">
        <div class="card-body">
          <div class="card-head">
            <span class="name">{{ m.name }}</span>
            <el-tag size="small" :type="m.status === '在售' ? 'success' : 'info'">{{ m.status }}</el-tag>
          </div>
          <div class="meta">
            <el-tag size="small" effect="plain">{{ m.category }}</el-tag>
            <span>单价 ¥{{ m.unitPrice }}</span>
            <span>库存 {{ m.stock }}</span>
          </div>
        </div>
        <div class="overlay">
          <el-button size="small" @click="openEdit(m)">编辑</el-button>
          <el-button v-if="m.status === '在售'" size="small" type="warning" @click="disable(m)">停用</el-button>
          <el-button v-else size="small" type="success" @click="enable(m)">启用</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog :title="editing ? '编辑花材' : '新增花材'" v-model="dialog" width="420px">
      <el-form label-width="80px">
        <el-form-item label="花材名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类别">
          <el-select v-model="form.category" style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价"><el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" style="width:100%" /></el-form-item>
        <el-form-item v-if="editing" label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="在售" value="在售" />
            <el-option label="停用" value="停用" />
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
.bar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.title { margin: 0; font-size: 20px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
.card { position: relative; border-radius: 14px; }
.card-body { padding: 4px; }
.card-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px; }
.name { font-size: 16px; font-weight: 700; }
.meta { display: flex; flex-wrap: wrap; gap: 10px; color: var(--muted); font-size: 13px; align-items: center; }
.overlay {
  position: absolute; left: 0; right: 0; bottom: 0;
  display: flex; gap: 8px; justify-content: center;
  padding: 10px; background: rgba(252,237,239,0.96);
  border-radius: 0 0 14px 14px;
  opacity: 0; transform: translateY(8px); transition: all .18s ease;
}
.card:hover .overlay { opacity: 1; transform: translateY(0); }
</style>
