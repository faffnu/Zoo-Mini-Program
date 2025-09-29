<template>
  <div>
    
		<div class="card" style="margin-bottom: 5px">
			<el-input v-model="data.search.name" prefix-icon="Search" style="width: 240px; margin-right: 10px; margin-bottom: 3px" placeholder="请输入动物名称"></el-input>
			<el-input v-model="data.search.type" prefix-icon="Search" style="width: 240px; margin-right: 10px; margin-bottom: 3px" placeholder="请输入所属类别"></el-input>
			<el-button type="info" plain @click="load">查询</el-button>
			<el-button type="warning" plain style="margin-right: 10px" @click="reset">重置</el-button>

			<el-upload :action="'http://localhost:9090/animal/upload?token=' + data.token" style="display: inline-block" :show-file-list="false" :on-success="impSuccessUpload">
				<el-button style="margin-right: 10px" type="success">批量导入</el-button>
			</el-upload>
			<el-button style="margin-right: 10px" type="info" @click="download()">下载模板</el-button>

			<el-button style="margin-right: 10px" type="warning" @click="exp()">批量导出</el-button>
		</div>
    <div class="card" style="margin-bottom: 5px">
			<el-button type="primary" plain @click="handleAdd">新增</el-button>
			<el-button  type="danger" plain @click="delBatch">批量删除</el-button>
		</div>

    <div class="card" style="margin-bottom: 5px;">
      <el-table :data="data.tableData" strip @selection-change="handleSelectionChange">
				<el-table-column  type="selection" width="55" align="center"></el-table-column>
				<el-table-column label="动物名称" prop="name"></el-table-column>
        <el-table-column prop="picUrl" label="动物图片">
          <template v-slot="scope">
            <el-image style="width: 40px; height: 40px; border-radius: 50%; display: block" v-if="scope.row.picUrl" :src="scope.row.picUrl" :preview-src-list="[scope.row.picUrl]" preview-teleported></el-image>
          </template>
        </el-table-column>
        <el-table-column label="动物介绍" >
          <template v-slot="scope">
            <span v-if="!scope.row.content">暂无</span>
            <el-button v-else size="small" type="primary" @click="initEditorView(scope.row.content)">查看</el-button>
          </template>
        </el-table-column>
				<el-table-column label="所属类别" prop="type"></el-table-column>
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

    <el-dialog title="请填写信息" v-model="data.formVisible" width="50%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="80px"  style="padding: 20px 30px">
				<el-form-item label="动物名称" prop="name">
					<el-input v-model="data.form.name" placeholder="请输入动物名称"></el-input>
				</el-form-item>
        <el-form-item label="动物图片" prop="picUrl" >
          <el-upload class="avatar-uploader" :action="'http://localhost:9090/files/upload'" :on-success="imageSuccessUpload" list-type="picture">
            <el-button type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
				<el-form-item label="所属类别" prop="type">
					<el-select style="width: 100%" v-model="data.form.typeId">
						<el-option v-for="item in data.typeData" :value="item.id" :label="item.name" :key="item.id"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="动物介绍" prop="content">
					<el-input type="textarea" v-model="data.form.content" placeholder="请输入动物介绍"></el-input>
				</el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog title="信息查看" v-model="data.richTextDialogVisible" width="50%" :close-on-click-modal="false" destroy-on-close>
      <div class="w-e-text">
        <div v-html="data.content"></div>
      </div>
      <div slot="footer" class="dialog-footer" style="text-align: right">
        <el-button type="primary" @click="data.richTextDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive } from "vue"
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";
const baseApi = 'animal'

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
	typeData: [],
  richTextDialogVisible: false,
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
const imageSuccessUpload = (res) => {
  data.form.picUrl = res.data;
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
	location.href = 'http://localhost:9090/animal/export?token=' + data.token
}

const download = () => {
  location.href = 'http://localhost:9090/animal/download?token=' + data.token
}

const impSuccessUpload = () => {
	ElMessage.success('导入成功');
	load();
}
// 加载动物分类
const loadType = () => {
	request.get("/type/selectAll").then(res => {
		if (res.code === '200') {
			data.typeData = res.data;
		} else {
			ElMessage.error(res.msg);
		}
	})
}
// 查看内容
const initEditorView = (content) => {
  data.content = content
  data.richTextDialogVisible = true
}

// 加载模块数据
load()
// 加载动物分类
loadType();

</script>
