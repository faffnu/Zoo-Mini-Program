const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    stars: [{
        flag: 1,
        bgImg: "/images/none-star.png",
        bgfImg: "/images/star.png"
      },
      {
        flag: 1,
        bgImg: "/images/none-star.png",
        bgfImg: "/images/star.png"
      },
      {
        flag: 1,
        bgImg: "/images/none-star.png",
        bgfImg: "/images/star.png"
      },
      {
        flag: 1,
        bgImg: "/images/none-star.png",
        bgfImg: "/images/star.png"
      },
      {
        flag: 1,
        bgImg: "/images/none-star.png",
        bgfImg: "/images/star.png"
      }
    ],
    user: null,
    score: 0,
    content: '',
    img: null,
    video: null,
    orderId: null
  },
  // 评分处理
  score: function (e) {
    const index = e.currentTarget.dataset.index;
    const stars = this.data.stars.map((item, i) => ({
      ...item,
      flag: i <= index ? 2 : 1
    }));
    this.setData({
      stars,
      score: index + 1
    });
  },

  // 输入内容处理
  onContentInput: function (e) {
    this.setData({
      content: e.detail.value
    });
  },

  // 选择媒体文件
  chooseMedia: function () {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image', 'video'],
      sourceType: ['album', 'camera'],
      success: res => {
        const media = res.tempFiles[0];
        this.setData({
          img: media.fileType === 'image' ? media.tempFilePath : null,
          video: media.fileType === 'video' ? media.tempFilePath : null
        });
      }
    });
  },

  // 删除媒体文件
  removeMedia: function () {
    this.setData({
      img: null,
      video: null
    });
  },

  // 图片预览
  previewImage: function () {
    if (this.data.img) {
      wx.previewImage({
        urls: [this.data.img]
      });
    }
  },
  //更新订单
  orderUpdate() {
    let that = this;
    const id = that.data.orderId;
    wx.request({
      url: app.globalData.url + '/order/comment/' + id,
      method: "PUT",
      header: {
        token: wx.getStorageSync('token')
      },
      success(res) {
        if (res.data.code == 200) {
          wx.showToast({
            title: '发布成功'
          });
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);

        }
      }
    });


  },

  submit() {
    const that = this;
    // 校验必填项
    if (!this.data.score) {
      wx.showToast({
        title: '请先评分',
        icon: 'none'
      });
      return;
    }
    if (!this.data.content.trim()) {
      wx.showToast({
        title: '请输入评价内容',
        icon: 'none'
      });
      return;
    }
    // 获取用户信息
    const user = wx.getStorageSync('userinfo');
    if (!user || !user.id) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    // 上传文件Promise
    const uploadFile = () => {
      return new Promise((resolve, reject) => {
        if (!that.data.img && !that.data.video) {
          resolve({
            img: null,
            video: null
          });
          return;
        }
        const filePath = that.data.img || that.data.video;
        const formData = {
          userId: user.id
        };
        wx.uploadFile({
          url: app.globalData.url + '/files/upload',
          filePath: filePath,
          name: 'file',
          formData: formData,
          success(res) {
            const result = JSON.parse(res.data);
            if (result.code == '200') {
              resolve({
                [that.data.img ? 'img' : 'video']: result.data
              });
            } else {
              reject('文件上传失败');
            }
          },
          fail() {
            reject('文件上传失败');
          }
        });
      });
    };

    // 执行上传并提交
    wx.showLoading({
      title: '发布中...'
    });

    uploadFile()
      .then(media => {
        // 构建评论对象
        const comment = {
          score: Number(that.data.score),
          content: that.data.content,
          img: that.data.img ? media.img : null,
          video: that.data.video ? media.video : null,
          userId: user.id,
        };

        // 提交评论
        wx.request({
          url: app.globalData.url + '/comment/add',
          method: 'POST',
          header: {
            'content-type': 'application/json',
            token: wx.getStorageSync('token')
          },
          data: comment,
          success(res) {
            if (res.data.code == 200) {
              that.orderUpdate();
            } else {
              throw new Error(res.data.msg || '发布失败');
            }
          },
          fail() {
            throw new Error('网络请求失败');
          }
        });
      })
      .catch(err => {
        wx.showToast({
          title: err,
          icon: 'none'
        });
      })
      .finally(() => {
        wx.hideLoading();
      });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let user = wx.getStorageSync('userinfo');
    const orderId = options.id;
    this.setData({
      user: user,
      orderId: orderId
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