/**
 * 登录页
 * 微信一键登录为主，账号密码登录为备用
 */
const api = require('../../utils/api');
const auth = require('../../utils/auth');

Page({
  data: {
    username: '',
    password: '',
    showPassword: false,
    isLogging: false,
    wechatLogging: false,
  },

  onLoad(options) {
    if (options.redirect) {
      this.redirectUrl = decodeURIComponent(options.redirect);
    }
    // 不自动微信登录，让用户手动点击或使用账号密码
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value });
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  onTogglePassword() {
    this.setData({ showPassword: !this.data.showPassword });
  },

  /** 微信一键登录 */
  async wechatLogin() {
    if (this.data.wechatLogging) return;
    this.setData({ wechatLogging: true });

    try {
      // 1. 获取微信临时 code
      const loginRes = await new Promise((resolve, reject) => {
        wx.login({
          success: resolve,
          fail: reject,
        });
      });

      if (!loginRes.code) {
        throw new Error('获取微信登录凭证失败');
      }

      // 2. 获取用户信息（头像、昵称）
      let nickName = '';
      let avatarUrl = '';
      try {
        const userInfo = await new Promise((resolve, reject) => {
          wx.getUserProfile({
            desc: '用于完善个人资料',
            success: resolve,
            fail: () => resolve({ userInfo: {} }),
          });
        });
        nickName = userInfo.userInfo?.nickName || '';
        avatarUrl = userInfo.userInfo?.avatarUrl || '';
      } catch (e) {
        // 用户拒绝授权也可以继续
      }

      // 3. 调后端接口登录
      const result = await api.miniAppLogin({
        code: loginRes.code,
        nickName,
        avatarUrl,
      });

      // 4. 保存登录态
      auth.handleLoginSuccess(result.token, result.user);
      this.navigateAfterLogin();
    } catch (err) {
      console.error('微信一键登录失败:', err);
      this.setData({ wechatLogging: false });
      // 不弹 toast，让用户使用账号密码登录
    }
  },

  /** 账号密码登录 */
  async onLogin() {
    const { username, password, isLogging } = this.data;
    if (isLogging) return;
    if (!username || !password) {
      wx.showToast({ title: '请输入用户名和密码', icon: 'none' });
      return;
    }

    this.setData({ isLogging: true });
    try {
      const result = await api.login(username, password);
      auth.handleLoginSuccess(result.token, result.user);
      this.navigateAfterLogin();
    } catch (err) {
      wx.showToast({ title: err.msg || '登录失败', icon: 'none' });
    } finally {
      this.setData({ isLogging: false });
    }
  },

  /** 登录成功后跳转 */
  navigateAfterLogin() {
    if (this.redirectUrl) {
      wx.redirectTo({ url: this.redirectUrl });
    } else {
      wx.switchTab({ url: '/pages/index/index' });
    }
  },

  /** 跳转注册 */
  onRegister() {
    wx.navigateTo({ url: '/pages/register/register' });
  },

  /** 忽略微信登录，使用账号密码 */
  onUsePassword() {
    this.setData({ wechatLogging: false });
  },
});
