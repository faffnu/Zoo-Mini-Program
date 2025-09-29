// pages/subPages/favor/favor.js
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    animalList: [],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    var that = this;
    var user = wx.getStorageSync('userinfo')
    wx.request({
      url: app.globalData.url + '/favor/selectByUserId/' + user.id,
      header: {
        token: wx.getStorageSync('token')
      },
      success: function (res) {
        if (res.statusCode === 401) {
          wx.showModal({
            title: '提示',
            content: 'token无效，请重新登录',
            showCancel: false,
            success(res) {
              wx.removeStorageSync('userinfo');
              wx.removeStorageSync('token');
              if (res.confirm) {
                wx.switchTab({
                  url: '/pages/me/me',
                })
              }
            }
          })
        }
        if (res.data.code === "200") {
          that.setData({
            animalList: res.data.data,
          });
        }

      },
    })

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

  }
})