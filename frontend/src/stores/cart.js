import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCart, addToCart, updateCartItem, removeCartItem, clearCart as apiClearCart } from '@/api/cart'
import { getSessionId } from '@/utils/session'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const loading = ref(false)

  const count = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const total = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  async function fetchCart() {
    try {
      loading.value = true
      const res = await getCart()
      items.value = res || []
    } catch (e) {
      items.value = []
    } finally {
      loading.value = false
    }
  }

  async function addItem(productId, quantity = 1) {
    const sessionId = getSessionId()
    const res = await addToCart({ sessionId, productId, quantity })
    await fetchCart()
    return res
  }

  async function updateQty(id, quantity) {
    await updateCartItem(id, { quantity })
    await fetchCart()
  }

  async function removeItem(id) {
    await removeCartItem(id)
    await fetchCart()
  }

  async function clearCart() {
    await apiClearCart()
    items.value = []
  }

  return {
    items,
    loading,
    count,
    total,
    fetchCart,
    addItem,
    updateQty,
    removeItem,
    clearCart
  }
})
