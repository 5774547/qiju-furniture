import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getInquiryList,
  addToInquiryList,
  updateInquiryItem,
  removeFromInquiryList,
  clearInquiryList as apiClearInquiryList
} from '@/api/inquiryList'
import { getSessionId } from '@/utils/session'

export const useInquiryListStore = defineStore('inquiryList', () => {
  const items = ref([])
  const loading = ref(false)

  const count = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  async function fetchInquiryList() {
    try {
      loading.value = true
      const res = await getInquiryList()
      items.value = res || []
    } catch (e) {
      items.value = []
    } finally {
      loading.value = false
    }
  }

  async function addItem(productId, quantity = 1) {
    const sessionId = getSessionId()
    const res = await addToInquiryList({ sessionId, productId, quantity })
    await fetchInquiryList()
    return res
  }

  async function updateQty(id, quantity) {
    await updateInquiryItem(id, { quantity })
    await fetchInquiryList()
  }

  async function removeItem(id) {
    await removeFromInquiryList(id)
    await fetchInquiryList()
  }

  async function clearList() {
    await apiClearInquiryList()
    items.value = []
  }

  return {
    items,
    loading,
    count,
    fetchInquiryList,
    addItem,
    updateQty,
    removeItem,
    clearList
  }
})
