/**
 * API 封装模块
 * 基于 wx.request 封装，支持 JWT 认证、自动解包、错误处理
 */

const DEV_BASE_URL = 'http://localhost:8080';
const PROD_BASE_URL = 'https://api.qijufurniture.com';

let _baseUrl = DEV_BASE_URL;

/**
 * 设置 baseUrl（可在 app.js 中切换环境）
 * @param {'dev'|'prod'} env
 */
function setBaseUrl(env) {
  _baseUrl = env === 'prod' ? PROD_BASE_URL : DEV_BASE_URL;
}

/**
 * 获取当前 baseUrl
 * @returns {string}
 */
function getBaseUrl() {
  return _baseUrl;
}

/**
 * 获取本地存储的 token
 * @returns {string|null}
 */
function getToken() {
  try {
    return wx.getStorageSync('token') || null;
  } catch (e) {
    return null;
  }
}

/**
 * 核心请求方法
 * @param {string} method - 请求方法 GET/POST/PUT/DELETE
 * @param {string} url - 接口路径，如 /api/products
 * @param {object} [data] - 请求参数
 * @param {object} [options] - 额外配置
 * @param {boolean} [options.noAuth] - 是否不需要认证
 * @param {boolean} [options.showLoading] - 是否显示加载提示
 * @param {string} [options.loadingText] - 加载提示文字
 * @returns {Promise<any>} - 返回解包后的 data 字段
 */
function request(method, url, data, options = {}) {
  const {
    noAuth = false,
    showLoading = false,
    loadingText = '加载中...',
  } = options;

  // 显示加载提示
  if (showLoading) {
    wx.showLoading({ title: loadingText, mask: true });
  }

  // 构建请求头
  const header = {
    'Content-Type': 'application/json',
  };

  // 添加认证 token
  if (!noAuth) {
    const token = getToken();
    if (token) {
      header['Authorization'] = `Bearer ${token}`;
    }
  }

  return new Promise((resolve, reject) => {
    wx.request({
      url: `${_baseUrl}${url}`,
      method: method,
      data: data,
      header: header,
      timeout: 15000,
      success: (res) => {
        // 隐藏加载提示
        if (showLoading) {
          wx.hideLoading();
        }

        const { statusCode, data: responseData } = res;

        // HTTP 状态码错误
        if (statusCode !== 200) {
          if (statusCode === 401 || statusCode === 403) {
            // 未认证，清除登录态
            wx.removeStorageSync('token');
            wx.removeStorageSync('userInfo');
          }
          const errMsg = `网络异常 (${statusCode})`;
          reject({ code: statusCode, msg: errMsg });
          return;
        }

        // 后端统一响应格式 {code, msg, data}
        const { code, msg, data: resultData } = responseData || {};

        if (code === 200) {
          resolve(resultData);
        } else if (code === 401) {
          // token 过期或无效，清除登录态并跳转登录页
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' });
          // 延迟跳转，让 toast 显示
          setTimeout(() => {
            wx.navigateTo({ url: '/pages/login/login' });
          }, 1500);
          reject({ code: 401, msg: msg || '登录已过期' });
        } else {
          // 业务错误
          wx.showToast({ title: msg || '请求失败', icon: 'none' });
          reject({ code, msg: msg || '请求失败' });
        }
      },
      fail: (err) => {
        if (showLoading) {
          wx.hideLoading();
        }
        const errMsg = err.errMsg || '网络请求失败';
        wx.showToast({ title: errMsg, icon: 'none' });
        reject({ code: -1, msg: errMsg });
      },
    });
  });
}

// ==================== 认证接口 ====================

/**
 * 用户名密码登录
 * @param {string} username
 * @param {string} password
 * @returns {Promise<{token: string, user: object}>}
 */
function login(username, password) {
  return request('POST', '/api/auth/login', { username, password }, { noAuth: true });
}

/**
 * 用户注册
 * @param {object} params
 * @param {string} params.username
 * @param {string} params.password
 * @param {string} params.email
 * @param {string} params.phone
 * @param {string} params.nickname
 * @param {string} params.companyName
 * @param {string} params.wechatId
 * @param {string} params.area
 * @returns {Promise<{token: string, user: object}>}
 */
function register(params) {
  return request('POST', '/api/auth/register', params, { noAuth: true });
}

// ==================== 产品接口 ====================

/**
 * 获取产品列表（分页）
 * @param {object} params
 * @param {number} [params.page=1]
 * @param {number} [params.size=20]
 * @param {string} [params.category]
 * @param {string} [params.keyword]
 * @returns {Promise<{records: Array, total: number, page: number, size: number}>}
 */
function getProducts(params = {}) {
  return request('GET', '/api/products', params, { showLoading: true });
}

/**
 * 获取产品详情
 * @param {number|string} id
 * @returns {Promise<object>}
 */
function getProduct(id) {
  return request('GET', `/api/products/${id}`, null, { showLoading: true });
}

/**
 * 获取产品分类列表
 * @returns {Promise<Array<{name: string, count: number}>>}
 */
function getCategories() {
  return request('GET', '/api/products/categories');
}

// ==================== 评价接口 ====================

/**
 * 获取产品评价
 * @param {number|string} productId
 * @returns {Promise<Array>}
 */
function getReviews(productId) {
  return request('GET', `/api/reviews/product/${productId}`);
}

/**
 * 提交评价
 * @param {object} params
 * @param {number|string} params.productId
 * @param {string} params.reviewerName
 * @param {number} params.rating
 * @param {string} params.content
 * @returns {Promise<object>}
 */
function submitReview(params) {
  return request('POST', '/api/reviews', params);
}

// ==================== 询价清单接口 ====================

/**
 * 获取我的询价清单
 * @returns {Promise<Array>}
 */
function getInquiryList() {
  return request('GET', '/api/inquiry-lists', null, { showLoading: true });
}

/**
 * 添加到询价清单
 * @param {number|string} productId
 * @param {number} quantity
 * @returns {Promise<object>}
 */
function addToInquiryList(productId, quantity = 1) {
  return request('POST', '/api/inquiry-lists', { productId, quantity });
}

/**
 * 更新询价清单项数量
 * @param {number|string} id
 * @param {number} quantity
 * @returns {Promise<void>}
 */
function updateInquiryItem(id, quantity) {
  return request('PUT', `/api/inquiry-lists/${id}`, { quantity });
}

/**
 * 从询价清单移除
 * @param {number|string} id
 * @returns {Promise<void>}
 */
function removeFromInquiryList(id) {
  return request('DELETE', `/api/inquiry-lists/${id}`);
}

/**
 * 清空询价清单
 * @returns {Promise<void>}
 */
function clearInquiryList() {
  return request('DELETE', '/api/inquiry-lists/clear');
}

// ==================== 询价单接口 ====================

/**
 * 创建询价单
 * @param {object} params
 * @param {string} params.customerName
 * @param {string} params.customerPhone
 * @param {string} params.customerCompany
 * @param {string} params.address
 * @param {string} params.remark
 * @param {Array<number|string>} params.itemIds
 * @returns {Promise<object>}
 */
function createInquiry(params) {
  return request('POST', '/api/inquiries', params, { showLoading: true, loadingText: '提交中...' });
}

/**
 * 获取我的询价单列表
 * @returns {Promise<Array>}
 */
function getMyInquiries() {
  return request('GET', '/api/inquiries/my', null, { showLoading: true });
}

/**
 * 获取询价单详情
 * @param {number|string} id
 * @returns {Promise<object>}
 */
function getInquiryDetail(id) {
  return request('GET', `/api/inquiries/${id}`, null, { showLoading: true });
}

/**
 * 系统健康检查
 * @returns {Promise<{status: string}>}
 */
function healthCheck() {
  return request('GET', '/api/system/health', null, { noAuth: true });
}

/**
 * 微信小程序一键登录
 * @param {object} params - {code, nickName, avatarUrl}
 * @returns {Promise<{token: string, user: object}>}
 */
function miniAppLogin(params) {
  return request('POST', '/api/auth/miniapp-login', params, { noAuth: true });
}

module.exports = {
  setBaseUrl,
  getBaseUrl,
  request,
  login,
  register,
  getProducts,
  getProduct,
  getCategories,
  getReviews,
  submitReview,
  getInquiryList,
  addToInquiryList,
  updateInquiryItem,
  removeFromInquiryList,
  clearInquiryList,
  createInquiry,
  getMyInquiries,
  getInquiryDetail,
  healthCheck,
  miniAppLogin,
};
