
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    memberInfo: {},
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    this.setData({
      memberInfo: wx.getStorageSync('userinfo'),
    })
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  },
  logout() {
    wx.removeStorage({
      key: 'token',
    });
    wx.removeStorage({
      key: 'userinfo',
    })
    this.setData({
      memberInfo: null,
    })
    wx.navigateBack({
      delta: 1, // 表示返回上一级页面
    });
  },

  edit: function () {
    wx.navigateTo({
      url: '/pages/subPages/user/edit/edit',
    })
  }

})