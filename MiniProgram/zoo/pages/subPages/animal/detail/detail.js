// pages/subPages/animal/detail/detail.js
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userinfo: {},
    animalinfo: {},
    isStar: false,

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let that = this;
    let id = options.id;
    let user = wx.getStorageSync('userinfo');
    if (user !== null && Object.keys(user).length > 0) {
      that.setData({
        userinfo: user,
      })
      wx.request({
        url: app.globalData.url + '/look/add',
        header: {
          token: wx.getStorageSync('token')
        },
        data: {
          userId: user.id,
          animalId: id
        },
        method: 'POST',
        success: function (res) {
          if (res.data.code == 200) {}
        }
      })
      wx.request({
        url: app.globalData.url + '/favor/selectAll',
        header: {
          token: wx.getStorageSync('token')
        },
        data: {
          userId: user.id,
          animalId: id
        },
        method: 'GET',
        success: function (res) {
          if (res.data.code == 200) {
            if (res.data.data.length !== 0) {
              that.setData({
                isStar: true,
              })
            }
          }
        }
      })
    } else {
      this.setData({
        isStar: false,
      })
    }

    if (id !== null) {
      wx.request({
        url: app.globalData.url + '/animal/selectById/' + id,
        method: 'GET',
        success: function (res) {
          if (res.data.code == 200) {
            that.setData({
              animalinfo: res.data.data,
            })
          }
        }
      })

    }
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

  },

  toggleStar() {
    if (this.data.userinfo.id == null) {
      return;
    }
    this.setData({
      isStar: !this.data.isStar
    })

    wx.request({
      url: app.globalData.url + '/favor/favorToggle',
      header: {
        token: wx.getStorageSync('token')
      },
      data: {
        userId: this.data.userinfo.id,
        animalId: this.data.animalinfo.id
      },
      method: 'POST',
      success: function (res) {
        if (res.data.code == 200) {

        }
      }
    })

  }




})