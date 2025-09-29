const app = getApp();
Page({
  data: {
    notices: [
      '今日开园时间：08:30-17:30',
      '猛兽区临时维护通知',
      '新动物「小熊猫」已入驻'
    ],
    tickets: [],
    user: null,
    comments: [],
    activities: [],
    animals: [],
  },
  onLoad() {
    this.setData({
      user: wx.getStorageSync('userinfo'),
    })
  },
  onShow() {
    this.setData({
      user: wx.getStorageSync('userinfo'),
    });
    this.getTickets();
    this.getActivities();
    this.getComments();
    this.getAnimals();
  },

  getTickets() {
    var that = this;
    wx.request({
      url: app.globalData.url + '/ticket/selectAll',
      method: 'GET',
      success: function (res) {
        if (res.data.code == 200) {
          that.setData({
            tickets: res.data.data
          })
        }
      }
    })
  },

  ticket(e) {
    let id = e.currentTarget.dataset.ticketid;
    wx.navigateTo({
      url: '../subPages/ticket/detail/detail?id=' + id,
    });

  },

  getAnimals() {
    var that = this;
    let url = app.globalData.url + '/recommend/popular';
    wx.request({
      url: url,
      method: 'GET',
      success: function (res) {
        if (res.data.code == 200) {
          that.setData({
            animals: res.data.data,
          });
        }
      }
    })
  },
  getActivities() {
    var that = this;
    wx.request({
      url: app.globalData.url + '/activity/selectAll',
      method: 'GET',
      success: function (res) {
        if (res.data.code == 200) {
          let activities = [];
          for (let i = 0; i < 3; i++) {
            activities.push(res.data.data[i]);
          }
          that.setData({
            activities: activities
          });
        }
      }
    })
  },

  getComments() {
    var that = this;
    wx.request({
      url: app.globalData.url + '/comment/selectByApprove',
      method: 'GET',
      success: function (res) {
        if (res.data.code == 200) {
          let comments = [];
          for (let i = 0; i < 3; i++) {
            comments.push(res.data.data[i]);
          }
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

          if (res.data.code != 200) {
            wx.showToast({
              title: '点赞/取消点赞失败',
            })
          }
        },
        fail: (error) => {
          console.error("请求失败", error);
        }
      });
    }
  },
  viewAllComments() {
    wx.navigateTo({
      url: '/pages/subPages/comment/detail/comment'
    });
  },

  viewAnimals() {
    wx.switchTab({
      url: '/pages/animals/animals',
    })
  },

  viewActivities() {
    wx.navigateTo({
      url: '/pages/subPages/activity/list/list'
    });
  },

})