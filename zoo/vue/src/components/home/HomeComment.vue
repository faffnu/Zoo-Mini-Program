<template>
  <div class="home-comment">
    <div class="comment-list">
      <div v-for="(item,index) in datalist" v-bind:key="index" class="comment-item">
        <div class="user-content">
          <el-image
              class="avatar"
              :src="item.avatar"
          ></el-image>
          <span class="user">{{ item.user}}</span>
        </div>
        <div>
          <span class="comment">{{ item.content }}</span>
          <span class="approve">点赞数：{{item.approve}}</span>
        </div>
        <div class="create-time">{{ item.time }}</div>
      </div>
    </div>
  </div>
</template>
<script>
import request from "@/utils/request.js";

export default {
  async mounted() {
    await this.getComment();
  },

  data() {
    return {
      datalist: []
    };
  },

  methods: {
    async getComment(){
      await request.get('comment/selectByApprove').then(res=>{
        this.datalist=res.data;
      })
    },


  },
}
</script>
<style lang="scss" scoped>

.home-comment {
  background: #ffffff;
  .title {
    font-size: 18px;
    color: #666;
    font-weight: bold;
    padding: 10px;
    border-bottom: 1px solid #eeeeee;
  }

  .comment-list {
    padding: 5px;
    height: 500px; /* 设定一个固定高度，或者根据实际需求使用百分比等 */
    overflow-y: auto; /* 启用垂直滚动条 */
    border: 1px solid #ddd; /* 可选：增加一个边框以更清晰地展示滚动区域 */
    margin: auto;

    .comment-item {
      padding: 5px;
      border-bottom: 1px solid #eeeeee;

      .user-content {
        display: flex;
        align-items: center;
        .user {
          font-size: 10px;
          color: #666;
          font-weight: bold;
          line-height: 50px;
          margin-left: 10px;
        }
        .avatar {
          width: 40px;
          height: 40px;
          border-radius: 50%;
        }
      }

      .comment {
        display: flex;
        font-size: 13px;
        color: #888888;
        margin-left: 50px;
      }
      .approve{
        display: flex;
        font-size: 10px;
        color: #666;
        margin-left: 90%;
      }

      .create-time {
        margin-top: 0;
        font-size: 10px;
        color: #888888;
        text-align: left;
        margin-left: 50px;
      }
    }
  }
}
</style>
