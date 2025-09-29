<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button  type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px;">
      <el-table :data="data.tableData" strip @selection-change="handleSelectionChange">
        <el-table-column  type="selection" width="55" align="center"></el-table-column>
        <el-table-column label="优惠券名称" prop="title"></el-table-column>
        <el-table-column label="优惠券类型" prop="type">
          <template #default="scope">
            <span v-if="scope.row.type === 1">折扣券</span>
            <span v-else-if="scope.row.type === 2">满减券</span>
          </template>
        </el-table-column>
        <el-table-column label="优惠值" prop="value">
          <template #default="scope">
            <span v-if="scope.row.type === 1">{{ scope.row.value *10}}折</span>
            <span v-else-if="scope.row.type === 2">￥{{ scope.row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最低消费金额" prop="minAmount"></el-table-column>
        <el-table-column label="有效期开始时间" prop="beginTime"></el-table-column>
        <el-table-column label="有效期结束时间" prop="endTime"></el-table-column>
        <el-table-column label="库存" prop="stock"></el-table-column>
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
        <el-form-item label="名称" prop="title">
          <el-input v-model="data.form.title" placeholder="请输入优惠券名称"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="data.form.type" placeholder="请选择优惠券类型">
            <el-option label="折扣券" :value="1"></el-option>
            <el-option label="满减券" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="优惠值" prop="value">
          <el-input v-model="data.form.value" placeholder="请输入优惠值"></el-input>
        </el-form-item>
        <el-form-item label="最低金额" prop="minAmount">
          <el-input v-model="data.form.minAmount" placeholder="请输入最低消费金额"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="beginTime">
          <el-date-picker v-model="data.form.beginTime" type="datetime" clearable value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择日期时间" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="data.form.endTime" type="datetime" clearable value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择日期时间" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input v-model="data.form.stock" placeholder="请输入库存"></el-input>
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
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { reactive } from "vue"
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";
const baseApi = 'voucher'

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


const editorConfig = { MENU_CONF: {} }


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
  // 验证时间
  if (new Date(data.form.beginTime) >= new Date(data.form.endTime)) {
    ElMessage.error('结束时间必须晚于开始时间')
    return
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

// 加载模块数据
load()

</script>
