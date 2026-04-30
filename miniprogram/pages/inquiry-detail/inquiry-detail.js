/**
 * 询价单详情页
 * 展示询价单状态、报价、联系信息、商品明细
 */
const api = require('../../utils/api');

Page({
  data: {
    detail: {},
    loading: true,
    statusText: {
      0: '待报价',
      1: '已报价',
      2: '已确认',
      3: '已关闭',
    },
  },

  onLoad(options) {
    const { id } = options;
    if (id) {
      this.loadDetail(id);
    } else {
      wx.showToast({ title: '参数错误', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
    }
  },

  /**
   * 加载询价单详情
   */
  async loadDetail(id) {
    this.setData({ loading: true });
    try {
      const detail = await api.getInquiryDetail(id);
      // Pre-compute subtotals (WXML doesn't support .toFixed())
      if (detail && detail.items) {
        detail.items = detail.items.map(item => ({
          ...item,
          subtotal: ((item.wholesalePrice || 0) * (item.quantity || 0)).toFixed(2)
        }));
      }
      this.setData({ detail: detail || {}, loading: false });
    } catch (err) {
      console.error('加载询价单详情失败:', err);
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  /**
   * 格式化时间
   */
  formatTime(timeStr) {
    if (!timeStr) return '';
    try {
      const date = new Date(timeStr);
      const y = date.getFullYear();
      const m = String(date.getMonth() + 1).padStart(2, '0');
      const d = String(date.getDate()).padStart(2, '0');
      const h = String(date.getHours()).padStart(2, '0');
      const min = String(date.getMinutes()).padStart(2, '0');
      return `${y}-${m}-${d} ${h}:${min}`;
    } catch (e) {
      return timeStr;
    }
  },
});
