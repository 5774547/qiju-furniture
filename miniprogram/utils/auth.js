/**
 * 登录态管理模块
 * 检查登录状态、保存/读取/清除 token、跳转登录页
 */

const TOKEN_KEY = 'token';
const USER_INFO_KEY = 'userInfo';

/**
 * 检查本地是否有 token 存在
 * @returns {boolean}
 */
function hasToken() {
  try {
    const token = wx.getStorageSync(TOKEN_KEY);
    return !!token;
  } catch (e) {
    return false;
  }
}

/**
 * 保存 token 到本地存储
 * @param {string} token
 */
function saveToken(token) {
  try {
    wx.setStorageSync(TOKEN_KEY, token);
  } catch (e) {
    console.error('保存 token 失败:', e);
  }
}

/**
 * 获取本地存储的 token
 * @returns {string|null}
 */
function getToken() {
  try {
    return wx.getStorageSync(TOKEN_KEY) || null;
  } catch (e) {
    return null;
  }
}

/**
 * 清除本地存储的 token
 */
function clearToken() {
  try {
    wx.removeStorageSync(TOKEN_KEY);
  } catch (e) {
    console.error('清除 token 失败:', e);
  }
}

/**
 * 保存用户信息到本地存储
 * @param {object} userInfo
 */
function saveUserInfo(userInfo) {
  try {
    wx.setStorageSync(USER_INFO_KEY, userInfo);
  } catch (e) {
    console.error('保存用户信息失败:', e);
  }
}

/**
 * 获取本地存储的用户信息
 * @returns {object|null}
 */
function getUserInfo() {
  try {
    return wx.getStorageSync(USER_INFO_KEY) || null;
  } catch (e) {
    return null;
  }
}

/**
 * 清除本地存储的用户信息
 */
function clearUserInfo() {
  try {
    wx.removeStorageSync(USER_INFO_KEY);
  } catch (e) {
    console.error('清除用户信息失败:', e);
  }
}

/**
 * 检查当前是否已登录（同步）
 * @returns {boolean}
 */
function isLoggedIn() {
  return hasToken();
}

/**
 * 确保已登录，否则跳转登录页
 * @param {string} [redirectUrl] - 登录后要跳转回的页面路径
 * @returns {boolean} 是否已登录
 */
function ensureLoggedIn(redirectUrl) {
  if (isLoggedIn()) {
    return true;
  }

  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1];
  const currentUrl = redirectUrl || (currentPage ? `/${currentPage.route}` : '');

  wx.showToast({ title: '请先登录', icon: 'none' });

  setTimeout(() => {
    wx.navigateTo({
      url: `/pages/login/login${currentUrl ? `?redirect=${encodeURIComponent(currentUrl)}` : ''}`,
    });
  }, 1000);

  return false;
}

/**
 * 登录成功后的处理：保存 token 和用户信息
 * @param {string} token
 * @param {object} userInfo
 */
function handleLoginSuccess(token, userInfo) {
  saveToken(token);
  saveUserInfo(userInfo);
}

/**
 * 退出登录：清除所有登录态信息
 */
function logout() {
  clearToken();
  clearUserInfo();

  // 跳转回首页
  wx.switchTab({
    url: '/pages/index/index',
  });
}

module.exports = {
  hasToken,
  saveToken,
  getToken,
  clearToken,
  saveUserInfo,
  getUserInfo,
  clearUserInfo,
  isLoggedIn,
  ensureLoggedIn,
  handleLoginSuccess,
  logout,
};
