<template>
  <div>
    
		<div class="card" style="margin-bottom: 5px">
			<el-input v-model="data.search.phone" prefix-icon="Search" style="width: 240px; margin-right: 10px; margin-bottom: 3px" placeholder="请输入下单手机号"></el-input>
			<el-input v-model="data.search.state" prefix-icon="Search" style="width: 240px; margin-right: 10px; margin-bottom: 3px" placeholder="请输入订单状态"></el-input>
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
				<el-table-column label="订单号" prop="orderNumber"></el-table-column>
				<el-table-column label="所购门票" prop="ticket"></el-table-column>
				<el-table-column label="购买数量" prop="number"></el-table-column>
				<el-table-column label="订单总价" prop="totalPrice"></el-table-column>
				<el-table-column label="下单手机号" prop="phone"></el-table-column>
				<el-table-column label="下单时间" prop="createTime"></el-table-column>
				<el-table-column label="使用日期" prop="useDate"></el-table-column>
				<el-table-column label="订单状态" prop="state"></el-table-column>
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
      <el-form :model="data.form" label-width="85px"  style="padding: 20px 30px">
				<el-form-item label="订单号" prop="orderNumber">
					<el-input v-model="data.form.orderNumber" placeholder="请输入订单号"></el-input>
				</el-form-item>
				<el-form-item label="所购门票" prop="ticket">
					<el-select style="width: 100%" v-model="data.form.ticketId">
						<el-option v-for="item in data.ticketData" :value="item.id" :label="item.name" :key="item.id"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="购买数量" prop="number">
					<el-input v-model="data.form.number" placeholder="请输入购买数量"></el-input>
				</el-form-item>
				<el-form-item label="订单总价" prop="totalPrice">
					<el-input v-model="data.form.totalPrice" placeholder="请输入订单总价"></el-input>
				</el-form-item>
				<el-form-item label="下单手机号" prop="phone">
					<el-select style="width: 100%" v-model="data.form.userId">
						<el-option v-for="item in data.userData" :value="item.id" :label="item.phone" :key="item.id"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="下单时间" prop="createTime">
					<el-date-picker v-model="data.form.createTime" type="datetime" clearable value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择日期时间" style="width: 100%"></el-date-picker>
				</el-form-item>
				<el-form-item label="使用日期" prop="useDate">
					<el-date-picker v-model="data.form.useDate" type="date" clearable value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%"></el-date-picker>
				</el-form-item>
				<el-form-item label="订单状态" prop="state">
					<el-select style="width: 100%" v-model="data.form.state">
						<el-option value="待支付"></el-option>
						<el-option value="待使用"></el-option>
						<el-option value="已消费"></el-option>
						<el-option value="已退款"></el-option>
						<el-option value="已取消"></el-option>
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
const baseApi = 'order'

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
	ticketData: [],
})



// 加载表格数据
const load = () => {

  data.search.pageNum = data.pageNum
  data.search.pageSize = data.pageSize
  request.get(baseApi + '/selectPage', {
    params: data.search
  }).then(res => {
    data.tableData = res.data?.list || []
    console.log(res.data)
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

  let number = data.form.number
  if (number && number !== '') {
    let num = Number(number); // 将 number 转换为数字
    if (isNaN(num)) { // 检查转换后的结果是否为 NaN（不是一个数字）
      ElMessage.error("请输入有效的数字");
      return false;
    }
  } else {
    // 如果 number 为空或未定义
    ElMessage.error("购票数不能为空");
    return false;
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
	location.href = 'http://localhost:9090/order/export?token=' + data.token
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
}// 加载门票
const loadTicket = () => {
	request.get("/ticket/selectAll").then(res => {
		if (res.code === '200') {
			data.ticketData = res.data;
		} else {
			ElMessage.error(res.msg);
		}
	})
}

// 加载模块数据
load()
// 加载用户
loadUser();
// 加载门票
loadTicket();

</script>
