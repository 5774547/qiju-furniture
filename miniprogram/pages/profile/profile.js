/**
 * 个人中心页
 * 用户信息展示、菜单导航、退出登录
 */
const auth = require('../../utils/auth');
const api = require('../../utils/api');

Page({
  data: {
    userInfo: {},
    isLoggedIn: false,
    inquiryCount: 0,
  },

  onShow() {
    this.loadUserInfo();
    this.loadInquiryCount();
  },

  /**
   * 加载用户信息
   */
  loadUserInfo() {
    const userInfo = auth.getUserInfo();
    const isLoggedIn = auth.isLoggedIn();
    this.setData({
      userInfo: userInfo || {},
      isLoggedIn,
    });
  },

  /**
   * 加载询价清单数量
   */
  async loadInquiryCount() {
    if (!auth.isLoggedIn()) {
      this.setData({ inquiryCount: 0 });
      return;
    }

    try {
      const list = await api.getInquiryList();
      const count = Array.isArray(list) ? list.length : 0;
      this.setData({ inquiryCount: count });
    } catch (err) {
      console.error('加载询价清单数量失败:', err);
    }
  },

  /**
   * 跳转登录页
   */
  onLoginTap() {
    wx.navigateTo({
      url: '/pages/login/login',
    });
  },

  /**
   * 编辑个人资料
   */
  onEditProfile() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  /**
   * 我的询价单
   */
  onMyInquiriesTap() {
    if (!auth.ensureLoggedIn()) return;
    wx.navigateTo({
      url: '/pages/inquiry-list/inquiry-list?tab=inquiries',
    });
  },

  /**
   * 询价清单
   */
  onInquiryListTap() {
    wx.switchTab({
      url: '/pages/inquiry-list/inquiry-list',
    });
  },

  /**
   * 关于我们
   */
  onAboutUsTap() {
    wx.showModal({
      title: '关于栖居家具',
      content: '栖居家具专注高品质实木家具设计与制造，致力于为每一位客户打造温馨舒适的家居空间。工厂直供，品质保障，让好家具触手可及。',
      showCancel: false,
      confirmText: '知道了',
      confirmColor: '#8B7355',
    });
  },

  /**
   * 退出登录
   */
  onLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      confirmText: '退出',
      confirmColor: '#F56C6C',
      success: (res) => {
        if (res.confirm) {
          auth.logout();
          this.setData({
            userInfo: {},
            isLoggedIn: false,
            inquiryCount: 0,
          });
          wx.showToast({ title: '已退出登录', icon: 'success' });
        }
      },
    });
  },
});
