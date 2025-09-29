// pages/subPages/voucher/voucher.js
const app = getApp()
Page({
  data: {
    activeTab: '0', // 0-可领取, 1-我的优惠券
    filterIndex: '0', // 0-未使用, 1-已使用, 2-已过期
    availableVouchers: [], // 可领取优惠券列表
    userVouchers: [], // 用户所有优惠券
    filteredUserVouchers: [], // 筛选后的用户优惠券
    now: new Date().getTime(), // 当前时间戳
  },

  onLoad: function (options) {
    this.loadAvailableVouchers();
    this.loadUserVouchers();
    this.setData({
      now: new Date().getTime()
    });
  },

  onPullDownRefresh: function () {
    this.loadAvailableVouchers();
    this.loadUserVouchers(() => {
      wx.stopPullDownRefresh();
    });
  },

  // 切换选项卡
  switchTab: function (e) {
    const index = e.currentTarget.dataset.index;
    this.setData({
      activeTab: index
    });
    // 切换到我的优惠券时刷新数据
    if (index === "1") {
      this.loadUserVouchers();
    }
  },

  // 切换筛选条件
  switchFilter: function (e) {
    const index = e.currentTarget.dataset.index;
    this.setData({
      filterIndex: index
    });
    this.filterUserVouchers();
  },

  // 加载可领取优惠券
  loadAvailableVouchers: function () {
    const that = this;
    wx.request({
      url: app.globalData.url + '/voucher/selectAvailable',
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: function (res) {
        if (res.data.code === "200") {
          that.setData({
            availableVouchers: res.data.data
          });
        } else {
          wx.showToast({
            title: '加载失败',
            icon: 'none'
          });
        }
      }
    });
  },

  // 加载用户优惠券
  loadUserVouchers: function (callback) {
    const that = this;
    const userInfo = wx.getStorageSync('userinfo');
    if (!userInfo || !userInfo.id) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    wx.request({
      url: app.globalData.url + '/userVoucher/all/' + userInfo.id,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: function (res) {
        if (res.data.code === "200") {
          that.setData({
            userVouchers: res.data.data
          });
          that.filterUserVouchers();
        } else {
          wx.showToast({
            title: '加载失败',
            icon: 'none'
          });
        }
        callback && callback();
      }
    });
  },

  // 筛选用户优惠券
  filterUserVouchers: function () {
    const {
      userVouchers,
      filterIndex
    } = this.data;
    const filtered = userVouchers.filter(item => {
      return item.state == filterIndex;
    });
    this.setData({
      filteredUserVouchers: filtered
    });
  },

  // 领取优惠券
  receiveVoucher: function (e) {
    const voucherId = e.currentTarget.dataset.id;
    const that = this;

    wx.showLoading({
      title: '领取中...',
    });

    wx.request({
      url: app.globalData.url + '/userVoucher/receive/' + voucherId,
      method: 'POST',
      header: {
        'token': wx.getStorageSync('token'),
        'content-type': 'application/json'
      },
      success: function (res) {
        wx.hideLoading();
        if (res.data.code === "200") {
          wx.showToast({
            title: '领取成功',
          });
          // 刷新可领取列表和用户优惠券列表
          that.loadAvailableVouchers();
          that.loadUserVouchers();
        } else {
          wx.showToast({
            title: res.data.msg || '领取失败',
            icon: 'none'
          });
        }
      },
      fail: function () {
        wx.hideLoading();
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
      }
    });
  },

});