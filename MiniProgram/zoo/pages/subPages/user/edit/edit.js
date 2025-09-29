const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    member: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      member: wx.getStorageSync('userinfo'),
    })
  },

  uploadAvatar() {
    var that = this;
    wx.chooseImage({
      sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
      sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
      success: function (res) {
        // 返回选定照片的本地文件路径列表，tempFilePath可以作为img标签的src属性显示图片
        wx.uploadFile({
          url: app.globalData.url + '/files/upload',
          filePath: res.tempFilePaths[0],
          name: 'file',
          formData: {
            imageType: "goodsImage"
          },
          success: function (res) {
            var data = JSON.parse(res.data);
            if (data.code == 200) {
              wx.showToast({
                title: '上传成功',
                icon: 'success',
                duration: 2000
              })
              var member = that.data.member;
              member.avatar = data.data;
              that.setData({
                member: member
              })
            }
          },
          fail: function (res) {
            wx.showToast({
              title: '上传失败',
              duration: 2000
            })
          }
        })
      }
    })

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },



  bindSave: function (e) {
    var name = e.detail.value.name;
    var idCard = e.detail.value.idCard;
    var phone = e.detail.value.phone;
    if (name == "") {
      wx.showModal({
        title: '提示',
        content: '请填写昵称',
        showCancel: false
      })
      return
    }
    if (phone == "") {
      wx.showModal({
        title: '提示',
        content: '请填写联系电话',
        showCancel: false
      })
      return
    }
    if (idCard == "") {
      wx.showModal({
        title: '提示',
        content: '请填写身份证',
        showCancel: false
      })
      return
    }

    var data = e.detail.value;
    data.id = this.data.member.id
    data.avatar = this.data.member.avatar

    wx.request({
      url: app.globalData.url + '/user/update',
      header: {
        token: wx.getStorageSync('token')
      },
      method: 'PUT',
      data: data,
      success: function (res) {
        if (res.data.code != 200) {
          // 错误 
          wx.hideLoading();
          wx.showModal({
            title: '错误',
            content: res.data.msg,
            showCancel: false
          })
          return;
        }
        wx.setStorageSync('userinfo', data)
        // 跳转到结算页面
        wx.navigateBack({})
      }
    })
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})