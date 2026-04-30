/**
 * 登录页
 * 用户名/邮箱 + 密码登录，支持密码显隐切换
 */
const api = require('../../utils/api');
const auth = require('../../utils/auth');

Page({
  data: {
    username: '',
    password: '',
    showPassword: false,
    isLogging: false,
  },

  onLoad(options) {
    // 支持 redirect 参数，登录后跳转回指定页面
    if (options.redirect) {
      this.redirectUrl = decodeURIComponent(options.redirect);
    }
  },

  /**
   * 用户名输入
   */
  onUsernameInput(e) {
    this.setData({ username: e.detail.value });
  },

  /**
   * 密码输入
   */
  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  /**
   * 切换密码显隐
   */
  onTogglePassword() {
    this.setData({ showPassword: !this.data.showPassword });
  },

  /**
   * 登录
   */
  async onLogin() {
    const { username, password, isLogging } = this.data;

    if (isLogging) return;

    // 表单校验
    if (!username.trim()) {
      wx.showToast({ title: '请输入用户名/邮箱', icon: 'none' });
      return;
    }
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' });
      return;
    }
    if (password.length < 6) {
      wx.showToast({ title: '密码长度不能少于6位', icon: 'none' });
      return;
    }

    this.setData({ isLogging: true });

    try {
      const result = await api.login(username.trim(), password);

      // 保存登录态
      const { token, user } = result;
      auth.handleLoginSuccess(token, user);

      wx.showToast({ title: '登录成功', icon: 'success' });

      // 延迟跳转，让 toast 显示
      setTimeout(() => {
        if (this.redirectUrl) {
          // 跳转到来源页
          wx.redirectTo({ url: this.redirectUrl });
        } else {
          // 获取页面栈，如果有上一页则返回，否则跳转首页
          const pages = getCurrentPages();
          if (pages.length > 1) {
            wx.navigateBack();
          } else {
            wx.switchTab({ url: '/pages/index/index' });
          }
        }
      }, 1000);
    } catch (err) {
      console.error('登录失败:', err);
      // API 内部已处理 toast 提示，这里仅补充
      if (!err.msg) {
        wx.showToast({ title: '登录失败，请稍后重试', icon: 'none' });
      }
    } finally {
      this.setData({ isLogging: false });
    }
  },

  /**
   * 跳转注册页
   */
  onGoRegister() {
    wx.navigateTo({ url: '/pages/register/register' });
  },
});
