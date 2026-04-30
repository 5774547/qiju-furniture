/**
 * 我的询价单页
 * 卡片式列表展示所有询价单
 */
const api = require('../../utils/api');

Page({
  data: {
    inquiries: [],
    loading: false,
    statusText: {
      0: '待报价',
      1: '已报价',
      2: '已确认',
      3: '已关闭',
    },
  },

  onShow() {
    this.loadInquiries();
  },

  /**
   * 加载我的询价单列表
   */
  async loadInquiries() {
    if (this.data.loading) return;

    this.setData({ loading: true });
    try {
      const inquiries = await api.getMyInquiries();
      this.setData({
        inquiries: Array.isArray(inquiries) ? inquiries : [],
        loading: false,
      });
    } catch (err) {
      console.error('加载询价单失败:', err);
      this.setData({ loading: false });
    }
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.loadInquiries().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  /**
   * 点击卡片查看详情
   */
  onCardTap(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/inquiry-detail/inquiry-detail?id=${id}`,
    });
  },

  /**
   * 去浏览产品
   */
  onBrowseProducts() {
    wx.switchTab({ url: '/pages/products/products' });
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
