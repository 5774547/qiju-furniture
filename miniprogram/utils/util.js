/**
 * 工具函数模块
 * 价格格式化、时间格式化、图片URL处理
 */

/**
 * 格式化价格，保留两位小数
 * @param {number|string} price
 * @param {string} [prefix='¥'] - 前缀符号
 * @returns {string}
 */
function formatPrice(price, prefix = '¥') {
  if (price === null || price === undefined || price === '') {
    return `${prefix}0.00`;
  }
  const num = typeof price === 'string' ? parseFloat(price) : price;
  if (isNaN(num)) {
    return `${prefix}0.00`;
  }
  return `${prefix}${num.toFixed(2)}`;
}

/**
 * 格式化价格（不带前缀，纯数字格式化）
 * @param {number|string} price
 * @returns {string}
 */
function formatPriceNumber(price) {
  if (price === null || price === undefined || price === '') {
    return '0.00';
  }
  const num = typeof price === 'string' ? parseFloat(price) : price;
  if (isNaN(num)) {
    return '0.00';
  }
  return num.toFixed(2);
}

/**
 * 格式化时间戳为日期字符串
 * @param {number|string} timestamp - 时间戳（毫秒），也可以是 ISO 日期字符串
 * @param {string} [format='YYYY-MM-DD HH:mm'] - 输出格式
 * @returns {string}
 */
function formatTime(timestamp, format = 'YYYY-MM-DD HH:mm') {
  if (!timestamp) {
    return '';
  }

  let date;
  if (typeof timestamp === 'string' && isNaN(Number(timestamp))) {
    // ISO 字符串
    date = new Date(timestamp);
  } else {
    // 时间戳（秒转毫秒）
    const ts = typeof timestamp === 'string' ? parseInt(timestamp, 10) : timestamp;
    date = new Date(ts > 1e12 ? ts : ts * 1000);
  }

  if (isNaN(date.getTime())) {
    return '';
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 格式化日期（简洁版）
 * @param {number|string} timestamp
 * @returns {string} - 如 "2024-01-15"
 */
function formatDate(timestamp) {
  return formatTime(timestamp, 'YYYY-MM-DD');
}

/**
 * 相对时间格式化
 * @param {number|string} timestamp
 * @returns {string}
 */
function formatRelativeTime(timestamp) {
  if (!timestamp) {
    return '';
  }

  let date;
  if (typeof timestamp === 'string' && isNaN(Number(timestamp))) {
    date = new Date(timestamp);
  } else {
    const ts = typeof timestamp === 'string' ? parseInt(timestamp, 10) : timestamp;
    date = new Date(ts > 1e12 ? ts : ts * 1000);
  }

  if (isNaN(date.getTime())) {
    return '';
  }

  const now = Date.now();
  const diff = now - date.getTime();

  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const month = 30 * day;
  const year = 365 * day;

  if (diff < minute) {
    return '刚刚';
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`;
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`;
  } else if (diff < month) {
    return `${Math.floor(diff / day)}天前`;
  } else if (diff < year) {
    return `${Math.floor(diff / month)}个月前`;
  } else {
    return `${Math.floor(diff / year)}年前`;
  }
}

/**
 * 拼接完整图片 URL
 * 如果图片路径是相对路径，则拼接上 baseUrl
 * @param {string} imagePath - 图片路径
 * @param {string} [baseUrl='http://localhost:8080'] - 后端地址
 * @returns {string}
 */
function formatImageUrl(imagePath, baseUrl = 'http://localhost:8080') {
  if (!imagePath) {
    return '';
  }

  // 已经是完整 URL（http/https 开头）
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }

  // 云文件 ID
  if (imagePath.startsWith('cloud://')) {
    return imagePath;
  }

  // 相对路径，去掉开头的 / 或直接拼接
  const cleanPath = imagePath.startsWith('/') ? imagePath : `/${imagePath}`;
  return `${baseUrl}${cleanPath}`;
}

/**
 * 截断文本
 * @param {string} text
 * @param {number} maxLength - 最大长度
 * @param {string} [suffix='...']
 * @returns {string}
 */
function truncateText(text, maxLength, suffix = '...') {
  if (!text) {
    return '';
  }
  if (text.length <= maxLength) {
    return text;
  }
  return text.substring(0, maxLength) + suffix;
}

/**
 * 防抖函数
 * @param {Function} fn
 * @param {number} delay - 延迟毫秒数
 * @returns {Function}
 */
function debounce(fn, delay = 300) {
  let timer = null;
  return function (...args) {
    if (timer) {
      clearTimeout(timer);
    }
    timer = setTimeout(() => {
      fn.apply(this, args);
      timer = null;
    }, delay);
  };
}

/**
 * 节流函数
 * @param {Function} fn
 * @param {number} interval - 间隔毫秒数
 * @returns {Function}
 */
function throttle(fn, interval = 300) {
  let lastTime = 0;
  return function (...args) {
    const now = Date.now();
    if (now - lastTime >= interval) {
      lastTime = now;
      fn.apply(this, args);
    }
  };
}

module.exports = {
  formatPrice,
  formatPriceNumber,
  formatTime,
  formatDate,
  formatRelativeTime,
  formatImageUrl,
  truncateText,
  debounce,
  throttle,
};
