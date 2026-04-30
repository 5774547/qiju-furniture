/**
 * 提交询价页
 * 表单：联系人、电话、公司名称、地址、备注
 * 展示即将提交的产品清单（只读）
 */
const api = require('../../utils/api');

Page({
  data: {
    customerName: '',
    customerPhone: '',
    customerCompany: '',
    address: '',
    remark: '',
    items: [],
    submitting: false,
  },

  onShow() {
    this.loadInquiryItems();
  },

  /**
   * 加载询价清单中的产品
   */
  async loadInquiryItems() {
    try {
      const items = await api.getInquiryList();
      this.setData({ items: Array.isArray(items) ? items : [] });
    } catch (err) {
      console.error('加载询价清单失败:', err);
      this.setData({ items: [] });
    }
  },

  /**
   * 表单输入
   */
  onInputChange(e) {
    const { field } = e.currentTarget.dataset;
    const value = e.detail.value;
    this.setData({ [field]: value });
  },

  /**
   * 提交询价
   */
  async onSubmit() {
    const { customerName, customerPhone, items } = this.data;

    // 表单校验
    if (!customerName.trim()) {
      wx.showToast({ title: '请输入联系人', icon: 'none' });
      return;
    }
    if (!customerPhone.trim()) {
      wx.showToast({ title: '请输入联系电话', icon: 'none' });
      return;
    }
    if (!/^1\d{10}$/.test(customerPhone.trim())) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }
    if (items.length === 0) {
      wx.showToast({ title: '询价清单为空，请先添加产品', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });

    try {
      const params = {
        customerName: this.data.customerName.trim(),
        customerPhone: this.data.customerPhone.trim(),
        customerCompany: this.data.customerCompany.trim(),
        address: this.data.address.trim(),
        remark: this.data.remark.trim(),
        itemIds: items.map(item => item.id),
      };

      await api.createInquiry(params);

      wx.showToast({ title: '提交成功', icon: 'success' });

      // 刷新全局询价清单数量
      const app = getApp();
      if (app && app.refreshInquiryCount) {
        app.refreshInquiryCount();
      }

      // 跳转到我的询价单
      setTimeout(() => {
        wx.navigateTo({ url: '/pages/inquiries/inquiries' });
      }, 1500);
    } catch (err) {
      console.error('提交询价失败:', err);
      this.setData({ submitting: false });
    }
  },
});
