// pages/subPages/comment/detail/comment.js
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    comments: [],
    currentSort: "default", // 当前排序方式
    sliderPosition: 0, // 滑块的偏移量
    averageRating: 0, // 平均评分
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      user: wx.getStorageSync('userinfo'),
    })
    this.getComments();
    this.calculateAverageRating();
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

  getComments() {
    var that = this;
    let url = '';
    // 根据currentSort动态设置请求地址
    if (that.data.currentSort === 'default') {
      url = app.globalData.url + '/comment/selectByApprove';
    } else if (that.data.currentSort === 'latest') {
      url = app.globalData.url + '/comment/selectByDate';
    } else {
      console.error('Invalid type:', type);
    }
    wx.request({
      url: url,
      method: 'GET',
      success: function (res) {
        if (res.data.code == 200) {
          let comments = res.data.data;
          if (that.data.user != null && Object.keys(that.data.user).length > 0) {
            let promises = comments.map((comment, i) => {
              return new Promise((resolve, reject) => {
                wx.request({
                  url: app.globalData.url + '/commentApprove/selectAll',
                  header: {
                    token: wx.getStorageSync('token')
                  },
                  data: {
                    userId: that.data.user.id,
                    commentId: comment.id
                  },
                  success: function (res) {
                    if (res.data.code == 200) {
                      comment.isApprove = res.data.data.length !== 0;
                      resolve();
                    } else {
                      comment.isApprove = false;
                    }
                  },
                  fail: reject
                });
              });
            });
            Promise.all(promises).then(() => {
              that.setData({
                comments: comments
              });
            }).catch(error => {
              console.error("Error processing comments:", error);
            });
          }
          that.setData({
            comments: comments,
          })
        }
      }
    })

  },

  toggleApprove(e) {
    const that = this;
    if (that.data.user != null && Object.keys(that.data.user).length > 0) {
      wx.request({
        url: app.globalData.url + '/commentApprove/approveToggle',
        header: {
          token: wx.getStorageSync('token')
        },
        method: 'POST',
        data: {
          userId: that.data.user.id,
          commentId: e.currentTarget.dataset.id
        },
        success: (res) => {
          let comments = [...that.data.comments];
          for (let i = 0; i < comments.length; i++) {
            if (comments[i].id == e.currentTarget.dataset.id) {
              if (comments[i].isApprove) {
                comments[i].approve = comments[i].approve - 1;
                comments[i].isApprove = false;
              } else {
                comments[i].approve = comments[i].approve + 1;
                comments[i].isApprove = true;
              }
              break;
            }
          }
          // 更新数据
          that.setData({
            comments: comments
          });
          if (res.data.code != 200) {}
        },
        fail: (error) => {
          console.error("请求失败", error);
        }
      });
    }
  },

  // 计算平均评分
  calculateAverageRating() {
    let that = this;
    wx.request({
      url: app.globalData.url + '/comment/getScore',
      method: 'GET',
      success: function (res) {
        if (res.data.code == 200) {
          that.setData({
            averageRating: res.data.data,
          });
        }
      }
    })

  },

  // 切换排序方式
  changeSort(e) {
    const sortType = e.currentTarget.dataset.sort;
    // 更新当前选中的排序方式
    this.setData({
      currentSort: sortType
    }, () => {
      // 更新滑块位置
      this.updateSliderPosition();
      this.getComments();
    });

  },
  // 更新滑块位置
  updateSliderPosition() {
    const query = wx.createSelectorQuery();
    query.select('.sort-item.active').boundingClientRect((rect) => {
      if (rect) {
        const sliderPosition = rect.left;
        this.setData({
          sliderPosition
        });
      }
    }).exec();
  },

  // 获取排序后的评价列表
  get sortedReviews() {
    const {
      reviews,
      currentSort
    } = this.data;
    if (currentSort === "latest") {
      // 按日期倒序排列（最新评论在前）
      return [...reviews].sort((a, b) => new Date(b.date) - new Date(a.date));
    }
    // 默认排序（按原始顺序）
    return reviews;
  },



})