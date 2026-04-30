/**
 * 询价清单页
 * 展示已加入询价清单的产品，可修改数量、删除、清空、提交询价
 */
const api = require('../../utils/api');

Page({
  data: {
    items: [],
    totalQuantity: 0,
    loading: false,
  },

  onShow() {
    // 检查登录
    const token = wx.getStorageSync('token');
    if (!token) {
      this.setData({ items: [], totalQuantity: 0, loading: false });
      return;
    }
    this.loadInquiryList();
  },

  /**
   * 加载询价清单
   */
  async loadInquiryList() {
    if (this.data.loading) return;

    this.setData({ loading: true });
    try {
      const items = await api.getInquiryList();
      const list = Array.isArray(items) ? items : [];
      const totalQuantity = list.reduce((sum, item) => sum + (item.quantity || 1), 0);
      this.setData({ items: list, totalQuantity, loading: false });
    } catch (err) {
      console.error('加载询价清单失败:', err);
      this.setData({ loading: false });
    }
  },

  /**
   * 减少数量
   */
  onDecrease(e) {
    const { index } = e.currentTarget.dataset;
    const items = [...this.data.items];
    const item = { ...items[index] };
    if (item.quantity <= 1) return;
    item.quantity -= 1;
    this.updateItemQuantity(items, index, item);
  },

  /**
   * 增加数量
   */
  onIncrease(e) {
    const { index } = e.currentTarget.dataset;
    const items = [...this.data.items];
    const item = { ...items[index] };
    item.quantity += 1;
    this.updateItemQuantity(items, index, item);
  },

  /**
   * 数量输入
   */
  onQuantityInput(e) {
    const { index } = e.currentTarget.dataset;
    const value = parseInt(e.detail.value, 10) || 1;
    const items = [...this.data.items];
    if (value < 1) return;
    items[index] = { ...items[index], quantity: value };
    const totalQuantity = items.reduce((sum, item) => sum + (item.quantity || 1), 0);
    this.setData({ items, totalQuantity });
  },

  /**
   * 数量输入失焦时同步到后端
   */
  onQuantityBlur(e) {
    const { index } = e.currentTarget.dataset;
    const item = this.data.items[index];
    if (!item) return;
    const quantity = Math.max(1, parseInt(item.quantity, 10) || 1);
    this.updateQuantity(item.id, quantity, index);
  },

  /**
   * 更新单个项数量到后端
   */
  async updateItemQuantity(items, index, item) {
    items[index] = item;
    const totalQuantity = items.reduce((sum, it) => sum + (it.quantity || 1), 0);
    this.setData({ items, totalQuantity });
    await this.updateQuantity(item.id, item.quantity, index);
  },

  /**
   * 调用后端更新数量
   */
  async updateQuantity(id, quantity, index) {
    try {
      const apiUpdate = api.updateInquiryItem || api.updateInquiryItem;
      if (apiUpdate) {
        await api.updateInquiryItem(id, quantity);
      }
    } catch (err) {
      console.error('更新数量失败:', err);
    }
  },

  /**
   * 删除项
   */
  onDelete(e) {
    const { id, index } = e.currentTarget.dataset;
    wx.showModal({
      title: '提示',
      content: '确定要移除此产品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.removeFromInquiryList(id);
            const items = [...this.data.items];
            items.splice(index, 1);
            const totalQuantity = items.reduce((sum, item) => sum + (item.quantity || 1), 0);
            this.setData({ items, totalQuantity });

            // 刷新全局计数
            const app = getApp();
            if (app && app.refreshInquiryCount) {
              app.refreshInquiryCount();
            }
          } catch (err) {
            console.error('删除失败:', err);
            wx.showToast({ title: '删除失败', icon: 'none' });
          }
        }
      },
    });
  },

  /**
   * 清空全部
   */
  onClearAll() {
    wx.showModal({
      title: '提示',
      content: '确定要清空询价清单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.clearInquiryList();
            this.setData({ items: [], totalQuantity: 0 });

            // 刷新全局计数
            const app = getApp();
            if (app && app.refreshInquiryCount) {
              app.refreshInquiryCount();
            }
            wx.showToast({ title: '已清空', icon: 'success' });
          } catch (err) {
            console.error('清空失败:', err);
            wx.showToast({ title: '清空失败', icon: 'none' });
          }
        }
      },
    });
  },

  /**
   * 提交询价
   */
  onSubmitInquiry() {
    if (this.data.items.length === 0) {
      wx.showToast({ title: '请先添加产品', icon: 'none' });
      return;
    }
    wx.navigateTo({ url: '/pages/inquiry-create/inquiry-create' });
  },

  /**
   * 去浏览产品
   */
  onBrowseProducts() {
    wx.switchTab({ url: '/pages/products/products' });
  },
});
