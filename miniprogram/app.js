/**
 * 栖居家具 - 小程序入口
 */
const auth = require('./utils/auth');

App({
  /**
   * 全局数据
   */
  globalData: {
    userInfo: null,
    token: null,
    // 询价清单数量（跨页面共享）
    inquiryCount: 0,
    // 环境配置 'dev' | 'prod'
    env: 'dev',
  },

  /**
   * 小程序启动时
   */
  onLaunch() {
    // 获取系统信息
    const systemInfo = wx.getSystemInfoSync();
    this.globalData.systemInfo = systemInfo;

    // 检查登录态
    const token = auth.getToken();
    const userInfo = auth.getUserInfo();
    if (token && userInfo) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
    }

    // 获取询价清单数量
    this.refreshInquiryCount();

    // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || [];
    logs.unshift(Date.now());
    wx.setStorageSync('logs', logs);
  },

  /**
   * 小程序从后台进入前台
   */
  onShow() {
    // 刷新询价清单数量
    this.refreshInquiryCount();
  },

  /**
   * 小程序从前台进入后台
   */
  onHide() {},

  /**
   * 刷新询价清单数量
   */
  refreshInquiryCount() {
    const token = auth.getToken();
    if (!token) {
      this.globalData.inquiryCount = 0;
      return;
    }

    const api = require('./utils/api');
    api.getInquiryList().then((list) => {
      const count = Array.isArray(list) ? list.length : 0;
      this.globalData.inquiryCount = count;
    }).catch(() => {
      // 静默失败
    });
  },

  /**
   * 设置环境
   * @param {'dev'|'prod'} env
   */
  setEnv(env) {
    this.globalData.env = env;
    const api = require('./utils/api');
    api.setBaseUrl(env);
  },
});
