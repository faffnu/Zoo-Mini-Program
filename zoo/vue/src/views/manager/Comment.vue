<template>
  <div>
		<div class="card" style="margin-bottom: 5px">
			<el-input v-model="data.search.score" prefix-icon="Search" style="width: 240px; margin-right: 10px; margin-bottom: 3px" placeholder="请输入评分"></el-input>
			<el-button type="info" plain @click="load">查询</el-button>
			<el-button type="warning" plain style="margin-right: 10px" @click="reset">重置</el-button>
		</div>
    <div class="card" style="margin-bottom: 5px">
			<el-button type="primary" plain @click="handleAdd">新增</el-button>
			<el-button  type="danger" plain @click="delBatch">批量删除</el-button>
		</div>

    <div class="card" style="margin-bottom: 5px;">
      <el-table :data="data.tableData" strip @selection-change="handleSelectionChange">
				<el-table-column  type="selection" width="55" align="center"></el-table-column>
				<el-table-column label="用户" prop="user"></el-table-column>
				<el-table-column label="评分" prop="score"></el-table-column>
				<el-table-column label="评论内容" prop="content"></el-table-column>
				<el-table-column prop="img" label="图片">
					<template v-slot="scope">
						<el-image style="width: 40px; height: 40px; border-radius: 50%; display: block" v-if="scope.row.img" :src="scope.row.img" :preview-src-list="[scope.row.img]" preview-teleported></el-image>
					</template>
				</el-table-column>
				<el-table-column label="体验视频">
					<template v-slot="scope">
						<span v-if="!scope.row.video">暂无</span>
						<el-button v-else size="small" type="warning"><a :href="scope.row.video" style="color: white">下载</a></el-button>
					</template>
				</el-table-column>
				<el-table-column label="点赞数" prop="approve"></el-table-column>
				<el-table-column label="评论时间" prop="time"></el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template v-slot="scope">
						<el-button type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
						<el-button type="danger" plain @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="card" v-if="data.total">
      <el-pagination @current-change="load" background layout="total, prev, pager, next" :page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
    </div>

    <el-dialog title="请填写信息" v-model="data.formVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="80px"  style="padding: 20px 30px">
				<el-form-item label="用户" prop="user">
					<el-select style="width: 100%" v-model="data.form.userId">
						<el-option v-for="item in data.userData" :value="item.id" :label="item.name" :key="item.id"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="评分" prop="score">
					<el-input v-model="data.form.score" placeholder="请输入评分"></el-input>
				</el-form-item>
				<el-form-item label="评论内容" prop="content">
					<el-input type="textarea" v-model="data.form.content" placeholder="请输入评论内容"></el-input>
				</el-form-item>
				<el-form-item label="图片" prop="img">
					<el-upload class="avatar-uploader" :action="'http://localhost:9090/files/upload'" :on-success="imgSuccessUpload" list-type="picture">
						<el-button type="primary">点击上传</el-button>
					</el-upload>
				</el-form-item>
				<el-form-item label="体验视频" prop="video">
					<el-upload action="http://localhost:9090/files/upload" ref="video" :on-success="videoSuccessUpload">
						<el-button type="success">点击上传</el-button>
					</el-upload>
				</el-form-item>
				<el-form-item label="点赞数" prop="approve">
					<el-input v-model="data.form.approve" placeholder="请输入点赞数"></el-input>
				</el-form-item>
				<el-form-item label="评论时间" prop="time">
					<el-date-picker v-model="data.form.time" type="datetime" clearable value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择日期时间" style="width: 100%"></el-date-picker>
				</el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive } from "vue"
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";
const baseApi = 'comment'

const data = reactive({
  token: JSON.parse(localStorage.getItem('xm-token') || '{}'),
  tableData: [],
  total: 0,
  pageNum: 1,  // 当前的页码
  pageSize: 5,  // 每页的个数
  formVisible: false,
  form: {},
  search: {},
	ids: [],
	userData: [],
})



// 加载表格数据
const load = () => {


  data.search.pageNum = data.pageNum
  data.search.pageSize = data.pageSize
  request.get(baseApi + '/selectPage', {
    params: data.search
  }).then(res => {
    data.tableData = res.data?.list || []
    data.total = res.data?.total
  })
}

// 打开新增弹窗
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}

// 打开编辑弹窗
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

// 新增
const add = () => {
  request.post(baseApi + '/add', data.form).then(res => {
    if (res.code === '200') {
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 更新
const update = () => {
  request.put(baseApi + '/update', data.form).then(res => {
    if (res.code === '200') {
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 删除
const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', { type: 'warning' }).then(res => {
    request.delete(baseApi + '/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.error(err)
  })
}

const save = () => {
	let score = data.form.score
  if (score && score !== '') {
    let num = Number(score); // 转换为数字
    if (isNaN(num)) { // 检查转换后的结果是否为 NaN（不是一个数字）
      ElMessage.error("请输入有效的评分");
      return false;
    }
  } else {
    // 如果为空或未定义
    ElMessage.error("评分不能为空");
    return false;
  }

	let approve = data.form.approve
	let regNum = /^[0-9]*$/
	if (approve && approve !== '') {
		if (!regNum.test(approve)) {
			ElMessage.error("请输入正确的数字")
			return false
		}
	}

  data.form.id ? update() : add()
}

const reset = () => {
  data.search = {}
  load()
}


// 批量删除表格数据
const delBatch = () => {
	if (data.ids.length === 0) {
		ElMessage.warning('请选择数据');
		return;
	}
	ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', { type: 'warning' }).then(res => {
		request.delete(baseApi + "/delete/batch", {data: data.ids}).then(res => {
			if (res.code === '200') {
				ElMessage.success('批量删除成功');
				load();
			} else {
				ElMessage.error(res.msg);
			}
		})
	}).catch(err => console.log(err))
}
const handleSelectionChange = (rows) => {
	data.ids = rows.map(v => v.id)
}
// 导出
const exp = () => {
	location.href = 'http://localhost:9090/comment/export?token=' + data.token
}
const imgSuccessUpload = (res) => {
	data.form.img = res.data;
}
const videoSuccessUpload = (res) => {
	data.form.video = res.data;
}
// 加载用户
const loadUser = () => {
	request.get("/user/selectAll").then(res => {
		if (res.code === '200') {
			data.userData = res.data;
		} else {
			ElMessage.error(res.msg);
		}
	})
}

// 加载模块数据
load()
// 加载用户
loadUser();

</script>
