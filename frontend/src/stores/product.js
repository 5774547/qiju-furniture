import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getProducts, getProduct, getCategories } from '@/api/product'

export const useProductStore = defineStore('product', () => {
  const products = ref([])
  const currentProduct = ref(null)
  const categories = ref([])
  const loading = ref(false)
  const pageInfo = ref({ page: 1, size: 20, total: 0, pages: 0, hasNext: false })

  async function fetchProducts(params = {}) {
    try {
      loading.value = true
      const res = await getProducts(params)
      // Handle both paginated (PageResult) and flat array responses
      if (res && res.records !== undefined) {
        pageInfo.value = { page: res.page, size: res.size, total: res.total, pages: res.pages, hasNext: res.hasNext }
        products.value = res.records || []
      } else {
        products.value = res || []
      }
      return products.value
    } catch (e) {
      products.value = []
      return []
    } finally {
      loading.value = false
    }
  }

  async function fetchProduct(id) {
    try {
      loading.value = true
      const res = await getProduct(id)
      currentProduct.value = res
      return res
    } catch (e) {
      currentProduct.value = null
      return null
    } finally {
      loading.value = false
    }
  }

  async function fetchCategories() {
    try {
      const res = await getCategories()
      categories.value = res || []
      return categories.value
    } catch (e) {
      categories.value = []
      return []
    }
  }

  return {
    products,
    currentProduct,
    categories,
    loading,
    pageInfo,
    fetchProducts,
    fetchProduct,
    fetchCategories
  }
})
