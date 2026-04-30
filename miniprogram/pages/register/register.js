/**
 * 注册页
 * 用户名、密码、确认密码、昵称、公司名称、手机号注册
 * 注册成功后自动登录并跳转首页
 */
const api = require('../../utils/api');
const auth = require('../../utils/auth');

Page({
  data: {
    username: '',
    password: '',
    confirmPassword: '',
    nickname: '',
    companyName: '',
    phone: '',
    showPassword: false,
    showConfirmPassword: false,
    isRegistering: false,
  },

  /**
   * 用户名输入
   */
  onUsernameInput(e) {
    this.setData({ username: e.detail.value });
  },

  /**
   * 昵称输入
   */
  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value });
  },

  /**
   * 密码输入
   */
  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  /**
   * 确认密码输入
   */
  onConfirmPasswordInput(e) {
    this.setData({ confirmPassword: e.detail.value });
  },

  /**
   * 公司名称输入
   */
  onCompanyNameInput(e) {
    this.setData({ companyName: e.detail.value });
  },

  /**
   * 手机号输入
   */
  onPhoneInput(e) {
    this.setData({ phone: e.detail.value });
  },

  /**
   * 切换密码显隐
   */
  onTogglePassword() {
    this.setData({ showPassword: !this.data.showPassword });
  },

  /**
   * 切换确认密码显隐
   */
  onToggleConfirmPassword() {
    this.setData({ showConfirmPassword: !this.data.showConfirmPassword });
  },

  /**
   * 表单校验
   */
  validateForm() {
    const { username, password, confirmPassword, companyName, phone } = this.data;

    if (!username.trim()) {
      wx.showToast({ title: '请输入用户名', icon: 'none' });
      return false;
    }
    if (username.trim().length < 3) {
      wx.showToast({ title: '用户名不能少于3个字符', icon: 'none' });
      return false;
    }
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' });
      return false;
    }
    if (password.length < 6) {
      wx.showToast({ title: '密码长度不能少于6位', icon: 'none' });
      return false;
    }
    if (!confirmPassword) {
      wx.showToast({ title: '请确认密码', icon: 'none' });
      return false;
    }
    if (password !== confirmPassword) {
      wx.showToast({ title: '两次输入的密码不一致', icon: 'none' });
      return false;
    }
    if (!companyName.trim()) {
      wx.showToast({ title: '请输入公司名称', icon: 'none' });
      return false;
    }
    if (!phone.trim()) {
      wx.showToast({ title: '请输入手机号', icon: 'none' });
      return false;
    }
    if (!/^1\d{10}$/.test(phone.trim())) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return false;
    }
    return true;
  },

  /**
   * 注册
   */
  async onRegister() {
    const { isRegistering } = this.data;

    if (isRegistering) return;

    // 表单校验
    if (!this.validateForm()) return;

    this.setData({ isRegistering: true });

    try {
      const { username, password, nickname, companyName, phone } = this.data;

      const result = await api.register({
        username: username.trim(),
        password: password,
        nickname: nickname.trim() || undefined,
        companyName: companyName.trim(),
        phone: phone.trim(),
      });

      // 注册成功后自动保存登录态并跳转
      const { token, user } = result;
      auth.handleLoginSuccess(token, user);

      wx.showToast({ title: '注册成功', icon: 'success' });

      // 延迟跳转首页
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' });
      }, 1000);
    } catch (err) {
      console.error('注册失败:', err);
      if (!err.msg) {
        wx.showToast({ title: '注册失败，请稍后重试', icon: 'none' });
      }
    } finally {
      this.setData({ isRegistering: false });
    }
  },

  /**
   * 跳转登录页
   */
  onGoLogin() {
    wx.navigateBack();
  },
});
