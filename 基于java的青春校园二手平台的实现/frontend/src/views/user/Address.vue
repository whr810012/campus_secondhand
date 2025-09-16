<template>
  <div class="address-page">
    <div class="page-header">
      <h2>收货地址管理</h2>
      <el-button type="primary" @click="showAddDialog">添加新地址</el-button>
    </div>

    <!-- 地址列表 -->
    <div class="address-list" v-loading="loading">
      <div 
        v-for="address in addresses" 
        :key="address.id" 
        class="address-item"
        :class="{ 'default-address': address.isDefault }"
      >
        <div class="address-info">
          <div class="recipient">
            <span class="name">{{ address.recipientName }}</span>
            <span class="phone">{{ address.phone }}</span>
            <el-tag v-if="address.isDefault" type="success" size="mini">默认</el-tag>
          </div>
          <div class="address-detail">
            {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}
          </div>
        </div>
        <div class="address-actions">
          <el-button size="mini" @click="editAddress(address)">编辑</el-button>
          <el-button 
            v-if="!address.isDefault" 
            size="mini" 
            type="primary" 
            @click="setDefault(address)"
          >
            设为默认
          </el-button>
          <el-button 
            size="mini" 
            type="danger" 
            @click="deleteAddress(address)"
            :disabled="address.isDefault"
          >
            删除
          </el-button>
        </div>
      </div>
      
      <div v-if="addresses.length === 0" class="empty-state">
        <i class="el-icon-location"></i>
        <p>暂无收货地址</p>
        <el-button type="primary" @click="showAddDialog">添加第一个地址</el-button>
      </div>
    </div>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="addressForm" :rules="rules" ref="addressForm" label-width="100px">
        <el-form-item label="收货人" prop="recipientName">
          <el-input v-model="addressForm.recipientName" placeholder="请输入收货人姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-select v-model="addressForm.province" placeholder="请选择省份" @change="onProvinceChange">
            <el-option 
              v-for="province in provinces" 
              :key="province.value" 
              :label="province.label" 
              :value="province.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-select v-model="addressForm.city" placeholder="请选择城市" @change="onCityChange">
            <el-option 
              v-for="city in cities" 
              :key="city.value" 
              :label="city.label" 
              :value="city.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-select v-model="addressForm.district" placeholder="请选择区县">
            <el-option 
              v-for="district in districts" 
              :key="district.value" 
              :label="district.label" 
              :value="district.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input 
            type="textarea" 
            :rows="3" 
            v-model="addressForm.detailAddress" 
            placeholder="请输入详细地址"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress" :loading="saving">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Address',
  data() {
    return {
      loading: false,
      saving: false,
      addresses: [],
      dialogVisible: false,
      isEdit: false,
      addressForm: {
        id: null,
        recipientName: '',
        phone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: '',
        isDefault: false
      },
      provinces: [
        { value: '北京市', label: '北京市' },
        { value: '上海市', label: '上海市' },
        { value: '广东省', label: '广东省' },
        { value: '浙江省', label: '浙江省' },
        { value: '江苏省', label: '江苏省' }
      ],
      cities: [],
      districts: [],
      cityData: {
        '北京市': ['北京市'],
        '上海市': ['上海市'],
        '广东省': ['广州市', '深圳市', '珠海市', '东莞市'],
        '浙江省': ['杭州市', '宁波市', '温州市', '嘉兴市'],
        '江苏省': ['南京市', '苏州市', '无锡市', '常州市']
      },
      districtData: {
        '北京市': ['东城区', '西城区', '朝阳区', '海淀区'],
        '上海市': ['黄浦区', '徐汇区', '长宁区', '静安区'],
        '广州市': ['越秀区', '荔湾区', '海珠区', '天河区'],
        '深圳市': ['罗湖区', '福田区', '南山区', '宝安区'],
        '杭州市': ['上城区', '下城区', '江干区', '拱墅区'],
        '南京市': ['玄武区', '秦淮区', '建邺区', '鼓楼区']
      },
      rules: {
        recipientName: [
          { required: true, message: '请输入收货人姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        province: [
          { required: true, message: '请选择省份', trigger: 'change' }
        ],
        city: [
          { required: true, message: '请选择城市', trigger: 'change' }
        ],
        district: [
          { required: true, message: '请选择区县', trigger: 'change' }
        ],
        detailAddress: [
          { required: true, message: '请输入详细地址', trigger: 'blur' },
          { min: 5, max: 100, message: '详细地址长度在 5 到 100 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑地址' : '添加地址'
    }
  },
  mounted() {
    this.loadAddresses()
  },
  methods: {
    async loadAddresses() {
      this.loading = true
      try {
        // 模拟数据
        const mockData = [
          {
            id: 1,
            recipientName: '张三',
            phone: '13800138000',
            province: '北京市',
            city: '北京市',
            district: '海淀区',
            detailAddress: '中关村大街1号',
            isDefault: true
          },
          {
            id: 2,
            recipientName: '李四',
            phone: '13900139000',
            province: '上海市',
            city: '上海市',
            district: '浦东新区',
            detailAddress: '陆家嘴金融中心2号楼',
            isDefault: false
          }
        ]
        this.addresses = mockData
      } catch (error) {
        this.$message.error('加载地址列表失败')
      } finally {
        this.loading = false
      }
    },
    showAddDialog() {
      this.isEdit = false
      this.dialogVisible = true
    },
    editAddress(address) {
      this.isEdit = true
      this.addressForm = { ...address }
      this.onProvinceChange(address.province)
      this.onCityChange(address.city)
      this.dialogVisible = true
    },
    async saveAddress() {
      this.$refs.addressForm.validate(async (valid) => {
        if (valid) {
          this.saving = true
          try {
            // 这里应该调用API保存地址
            await new Promise(resolve => setTimeout(resolve, 1000))
            this.$message.success(this.isEdit ? '更新成功' : '添加成功')
            this.dialogVisible = false
            this.loadAddresses()
          } catch (error) {
            this.$message.error('保存失败')
          } finally {
            this.saving = false
          }
        }
      })
    },
    async setDefault(address) {
      try {
        // 这里应该调用API设置默认地址
        await new Promise(resolve => setTimeout(resolve, 500))
        this.addresses.forEach(addr => {
          addr.isDefault = addr.id === address.id
        })
        this.$message.success('设置成功')
      } catch (error) {
        this.$message.error('设置失败')
      }
    },
    deleteAddress(address) {
      this.$confirm('确定要删除这个地址吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 这里应该调用API删除地址
          await new Promise(resolve => setTimeout(resolve, 500))
          this.$message.success('删除成功')
          this.loadAddresses()
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    onProvinceChange(province) {
      this.cities = (this.cityData[province] || []).map(city => ({
        value: city,
        label: city
      }))
      this.addressForm.city = ''
      this.addressForm.district = ''
      this.districts = []
    },
    onCityChange(city) {
      this.districts = (this.districtData[city] || []).map(district => ({
        value: district,
        label: district
      }))
      this.addressForm.district = ''
    },
    resetForm() {
      this.addressForm = {
        id: null,
        recipientName: '',
        phone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: '',
        isDefault: false
      }
      this.cities = []
      this.districts = []
      this.$refs.addressForm && this.$refs.addressForm.resetFields()
    }
  }
}
</script>

<style scoped>
.address-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.address-list {
  min-height: 200px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 16px;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.default-address {
  border-color: #67c23a;
  background-color: #f0f9ff;
}

.address-info {
  flex: 1;
}

.recipient {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.name {
  font-weight: bold;
  margin-right: 16px;
}

.phone {
  color: #606266;
  margin-right: 8px;
}

.address-detail {
  color: #606266;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
}

.dialog-footer {
  text-align: right;
}
</style>