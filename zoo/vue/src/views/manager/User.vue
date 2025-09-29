<template>
  <div>
    
		<div class="card" style="margin-bottom: 5px">
			<el-input v-model="data.search.name" prefix-icon="Search" style="width: 240px; margin-right: 10px; margin-bottom: 3px" placeholder="请输入昵称"></el-input>
			<el-button type="info" plain @click="load">查询</el-button>
			<el-button type="warning" plain style="margin-right: 10px" @click="reset">重置</el-button>
		</div>
    <div class="card" style="margin-bottom: 5px">
			<el-button type="primary" plain @click="handleAdd">新增</el-button>
			<el-button  type="danger" plain @click="delBatch">批量禁用</el-button>
      <el-button  type="primary" plain @click="unBanBatch">批量解禁</el-button>
		</div>

    <div class="card" style="margin-bottom: 5px;">
      <el-table :data="data.tableData" strip @selection-change="handleSelectionChange">
				<el-table-column  type="selection" width="55" align="center"></el-table-column>
				<el-table-column label="昵称" prop="name"></el-table-column>
				<el-table-column prop="avatar" label="头像">
					<template v-slot="scope">
						<el-image style="width: 40px; height: 40px; border-radius: 50%; display: block" v-if="scope.row.avatar" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" preview-teleported></el-image>
					</template>
				</el-table-column>
				<el-table-column label="性别" prop="gender"></el-table-column>
				<el-table-column label="手机号" prop="phone"></el-table-column>
				<el-table-column label="身份证" prop="idCard"></el-table-column>
<!--				<el-table-column label="微信用户ID" prop="openid"></el-table-column>-->
				<el-table-column label="最近登录时间" prop="lastLogin"></el-table-column>
				<el-table-column label="是否禁用" prop="isBanned"></el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template v-slot="scope">
						<el-button type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
						<el-button v-if="scope.row.isBanned=='否'" type="danger" plain  @click="del(scope.row.id)">禁用</el-button>
            <el-button v-if="scope.row.isBanned=='是'" type="primary" plain  @click="ret(scope.row.id)">解禁</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="card" v-if="data.total">
      <el-pagination @current-change="load" background layout="total, prev, pager, next" :page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
    </div>

    <el-dialog title="请填写信息" v-model="data.formVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="98px"  style="padding: 20px 30px">
				<el-form-item label="昵称" prop="name">
					<el-input v-model="data.form.name" placeholder="请输入昵称"></el-input>
				</el-form-item>
				<el-form-item label="头像" prop="avatar">
					<el-upload class="avatar-uploader" :action="'http://localhost:9090/files/upload'" :on-success="avatarSuccessUpload" list-type="picture">
						<el-button type="primary">点击上传</el-button>
					</el-upload>
				</el-form-item>
				<el-form-item label="性别" prop="gender">
					<el-radio-group v-model="data.form.gender">
						<el-radio label="男"></el-radio>
						<el-radio label="女"></el-radio>
						<el-radio label="保密"></el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item label="手机号" prop="phone">
					<el-input v-model="data.form.phone" placeholder="请输入手机号"></el-input>
				</el-form-item>
				<el-form-item label="身份证" prop="idCard">
					<el-input v-model="data.form.idCard" placeholder="请输入身份证"></el-input>
				</el-form-item>
				<el-form-item label="微信用户ID" prop="openid">
					<el-input v-model="data.form.openid" placeholder="请输入微信用户ID"></el-input>
				</el-form-item>
				<el-form-item label="最近登录时间" prop="lastLogin">
					<el-date-picker v-model="data.form.lastLogin" type="datetime" clearable value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择日期时间" style="width: 100%"></el-date-picker>
				</el-form-item>
				<el-form-item label="是否禁用" prop="isBanned">
					<el-select style="width: 100%" v-model="data.form.isBanned">
						<el-option value="是"></el-option>
						<el-option value="否"></el-option>
					</el-select>
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
const baseApi = 'user'

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

// 禁用
const del = (id) => {
  ElMessageBox.confirm('您确定禁用该用户吗?', '确认', { type: 'warning' }).then(res => {
    request.put(baseApi + '/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('已禁用成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.error(err)
  })
}

// 解禁
const ret = (id) => {
  request.put(baseApi + '/delete/' + id).then(res => {
    if (res.code === '200') {
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const save = () => {
  data.form.id ? update() : add()
}

const reset = () => {
  data.search = {}
  load()
}


// 批量禁用
const delBatch = () => {
	if (data.ids.length === 0) {
		ElMessage.warning('请选择数据');
		return;
	}
	ElMessageBox.confirm('您确定禁用这些用户吗?', '确认', { type: 'warning' }).then(res => {
		request.delete(baseApi + "/delete/batch", {data: data.ids}).then(res => {
			if (res.code === '200') {
				ElMessage.success('批量禁用成功');
				load();
			} else {
				ElMessage.error(res.msg);
			}
		})
	}).catch(err => console.log(err))
}

// 批量解禁
const unBanBatch = () => {
  if (data.ids.length === 0) {
    ElMessage.warning('请选择数据');
    return;
  }
  ElMessageBox.confirm('您确定解禁这些用户吗?', '确认', { type: 'warning' }).then(res => {
    request.delete(baseApi + "/unban/batch", {data: data.ids}).then(res => {
      if (res.code === '200') {
        ElMessage.success('批量解禁成功');
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
	location.href = 'http://localhost:9090/user/export?token=' + data.token
}
const avatarSuccessUpload = (res) => {
	data.form.avatar = res.data;
}

// 加载模块数据
load()

</script>


