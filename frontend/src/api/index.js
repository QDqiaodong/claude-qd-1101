import axios from 'axios'

const http = axios.create({ baseURL: '/api', timeout: 20000 })

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error?.response?.data?.message || error?.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

async function call(promise) {
  const response = await promise
  const body = response.data
  if (!body || body.ok === false) {
    throw new Error((body && body.message) || '请求失败')
  }
  return body.data
}

const params = (o) => ({ params: o })

export default {
  // ---------------- 花材台账 ----------------
  listFlowerMaterials: (status) => call(http.get('/flower-materials', params(status ? { status } : {}))),
  createFlowerMaterial: (d) => call(http.post('/flower-materials', d)),
  updateFlowerMaterial: (id, d) => call(http.put(`/flower-materials/${id}`, d)),
  disableFlowerMaterial: (id) => call(http.post(`/flower-materials/${id}/disable`)),
  enableFlowerMaterial: (id) => call(http.post(`/flower-materials/${id}/enable`)),

  // ---------------- 花束配方 ----------------
  listBouquets: () => call(http.get('/bouquets')),
  createBouquet: (d) => call(http.post('/bouquets', d)),
  updateBouquet: (id, d) => call(http.put(`/bouquets/${id}`, d)),

  // ---------------- 客户订单 ----------------
  listCustomerOrders: (status) => call(http.get('/customer-orders', params(status ? { status } : {}))),
  createCustomerOrder: (d) => call(http.post('/customer-orders', d)),
  updateCustomerOrder: (id, d) => call(http.put(`/customer-orders/${id}`, d)),
  cancelCustomerOrder: (id) => call(http.post(`/customer-orders/${id}/cancel`)),

  // ---------------- 今日配送 ----------------
  listDeliveries: (status) => call(http.get('/deliveries', params(status ? { status } : {}))),
  createDelivery: (d) => call(http.post('/deliveries', d)),
  updateDelivery: (id, d) => call(http.put(`/deliveries/${id}`, d)),
  finishDelivery: (id) => call(http.post(`/deliveries/${id}/finish`)),
  cancelDelivery: (id) => call(http.post(`/deliveries/${id}/cancel`))
}
