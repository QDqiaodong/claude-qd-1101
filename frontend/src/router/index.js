import { createRouter, createWebHashHistory } from 'vue-router'

// 注意：main.js 里没有 app.use(router)，路由实例由 App.vue 直接驱动，
// 所以这里全部用静态 import —— <component :is> 拿到的是组件对象本身。
import FlowerMaterialView from '../views/FlowerMaterialView.vue'
import BouquetView from '../views/BouquetView.vue'
import CustomerOrderView from '../views/CustomerOrderView.vue'
import DeliveryView from '../views/DeliveryView.vue'

const routes = [
  { path: '/', redirect: '/flower-materials' },
  { path: '/flower-materials', name: 'flower-materials', component: FlowerMaterialView },
  { path: '/bouquets', name: 'bouquets', component: BouquetView },
  { path: '/customer-orders', name: 'customer-orders', component: CustomerOrderView },
  { path: '/deliveries', name: 'deliveries', component: DeliveryView },
  { path: '/:pathMatch(.*)*', redirect: '/flower-materials' }
]

export default createRouter({
  history: createWebHashHistory(),
  routes
})
