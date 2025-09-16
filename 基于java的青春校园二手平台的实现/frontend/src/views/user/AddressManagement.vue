<template>
  <div class="address-management-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>地址管理</h2>
      <el-button type="primary" @click="showAddDialog = true">
        <i class="el-icon-plus"></i>
        添加新地址
      </el-button>
    </div>
    
    <!-- 地址列表 -->
    <div class="address-section">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      
      <div v-else-if="addresses.length > 0" class="address-list">
        <div
          v-for="address in addresses"
          :key="address.id"
          class="address-item"
          :class="{ 'is-default': address.isDefault }"
        >
          <div class="address-content">
            <div class="address-header">
              <div class="receiver-info">
                <span class="receiver-name">{{ address.receiverName }}</span>
                <span class="receiver-phone">{{ address.phone }}</span>
                <el-tag v-if="address.isDefault" type="success" size="mini">
                  默认地址
                </el-tag>
              </div>
              
              <div class="address-actions">
                <el-button
                  type="text"
                  size="small"
                  @click="editAddress(address)"
                >
                  编辑
                </el-button>
                
                <el-button
                  v-if="!address.isDefault"
                  type="text"
                  size="small"
                  @click="setDefaultAddress(address.id)"
                >
                  设为默认
                </el-button>
                
                <el-button
                  type="text"
                  size="small"
                  class="delete-btn"
                  @click="deleteAddress(address)"
                >
                  删除
                </el-button>
              </div>
            </div>
            
            <div class="address-detail">
              <div class="address-text">
                <i class="el-icon-location"></i>
                {{ getFullAddress(address) }}
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty description="暂无收货地址">
          <el-button type="primary" @click="showAddDialog = true">
            添加地址
          </el-button>
        </el-empty>
      </div>
    </div>
    
    <!-- 添加/编辑地址对话框 -->
    <el-dialog
      :title="isEdit ? '编辑地址' : '添加地址'"
      :visible.sync="showAddDialog"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="addressForm"
        :model="addressForm"
        :rules="addressRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收货人" prop="receiverName">
              <el-input
                v-model="addressForm.receiverName"
                placeholder="请输入收货人姓名"
                maxlength="20"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="addressForm.phone"
                placeholder="请输入手机号"
                maxlength="11"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="所在地区" prop="region">
          <el-cascader
            v-model="addressForm.region"
            :options="regionOptions"
            :props="{ expandTrigger: 'hover' }"
            placeholder="请选择省市区"
            style="width: 100%"
            @change="handleRegionChange"
          />
        </el-form-item>
        
        <el-form-item label="详细地址" prop="address">
          <el-input
            type="textarea"
            v-model="addressForm.address"
            placeholder="请输入详细地址（街道、门牌号等）"
            :rows="3"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">
            设为默认地址
          </el-checkbox>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '保存' : '添加' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getUserAddresses,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/address'

export default {
  name: 'AddressManagement',
  data() {
    return {
      loading: false,
      submitting: false,
      showAddDialog: false,
      isEdit: false,
      addresses: [],
      addressForm: {
        id: null,
        receiverName: '',
        phone: '',
        region: [],
        province: '',
        city: '',
        district: '',
        address: '',
        isDefault: false
      },
      addressRules: {
        receiverName: [
          { required: true, message: '请输入收货人姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        region: [
          { required: true, message: '请选择所在地区', trigger: 'change' }
        ],
        address: [
          { required: true, message: '请输入详细地址', trigger: 'blur' },
          { min: 5, max: 100, message: '详细地址长度在 5 到 100 个字符', trigger: 'blur' }
        ]
      },
      regionOptions: [
        {
          value: '110000',
          label: '北京市',
          children: [
            {
              value: '110100',
              label: '北京市',
              children: [
                { value: '110101', label: '东城区' },
                { value: '110102', label: '西城区' },
                { value: '110105', label: '朝阳区' },
                { value: '110106', label: '丰台区' },
                { value: '110107', label: '石景山区' },
                { value: '110108', label: '海淀区' },
                { value: '110109', label: '门头沟区' },
                { value: '110111', label: '房山区' },
                { value: '110112', label: '通州区' },
                { value: '110113', label: '顺义区' },
                { value: '110114', label: '昌平区' },
                { value: '110115', label: '大兴区' },
                { value: '110116', label: '怀柔区' },
                { value: '110117', label: '平谷区' },
                { value: '110118', label: '密云区' },
                { value: '110119', label: '延庆区' }
              ]
            }
          ]
        },
        {
          value: '310000',
          label: '上海市',
          children: [
            {
              value: '310100',
              label: '上海市',
              children: [
                { value: '310101', label: '黄浦区' },
                { value: '310104', label: '徐汇区' },
                { value: '310105', label: '长宁区' },
                { value: '310106', label: '静安区' },
                { value: '310107', label: '普陀区' },
                { value: '310109', label: '虹口区' },
                { value: '310110', label: '杨浦区' },
                { value: '310112', label: '闵行区' },
                { value: '310113', label: '宝山区' },
                { value: '310114', label: '嘉定区' },
                { value: '310115', label: '浦东新区' },
                { value: '310116', label: '金山区' },
                { value: '310117', label: '松江区' },
                { value: '310118', label: '青浦区' },
                { value: '310120', label: '奉贤区' },
                { value: '310151', label: '崇明区' }
              ]
            }
          ]
        },
        {
          value: '440000',
          label: '广东省',
          children: [
            {
              value: '440100',
              label: '广州市',
              children: [
                { value: '440103', label: '荔湾区' },
                { value: '440104', label: '越秀区' },
                { value: '440105', label: '海珠区' },
                { value: '440106', label: '天河区' },
                { value: '440111', label: '白云区' },
                { value: '440112', label: '黄埔区' },
                { value: '440113', label: '番禺区' },
                { value: '440114', label: '花都区' },
                { value: '440115', label: '南沙区' },
                { value: '440117', label: '从化区' },
                { value: '440118', label: '增城区' }
              ]
            },
            {
              value: '440300',
              label: '深圳市',
              children: [
                { value: '440303', label: '罗湖区' },
                { value: '440304', label: '福田区' },
                { value: '440305', label: '南山区' },
                { value: '440306', label: '宝安区' },
                { value: '440307', label: '龙岗区' },
                { value: '440308', label: '盐田区' },
                { value: '440309', label: '龙华区' },
                { value: '440310', label: '坪山区' },
                { value: '440311', label: '光明区' }
              ]
            }
          ]
        }
        // 可以继续添加更多省市区数据
      ]
    }
  },
  created() {
    this.loadAddresses()
  },
  methods: {
    async loadAddresses() {
      this.loading = true
      try {
        const res = await getUserAddresses()
        if (res.data.code === 200) {
          this.addresses = res.data.data || []
        }
      } catch (error) {
        console.error('加载地址失败:', error)
        this.$message.error('加载地址失败')
      } finally {
        this.loading = false
      }
    },
    
    editAddress(address) {
      this.isEdit = true
      this.addressForm = {
        id: address.id,
        receiverName: address.receiverName,
        phone: address.phone,
        region: [address.provinceCode, address.cityCode, address.districtCode].filter(Boolean),
        province: address.province,
        city: address.city,
        district: address.district,
        address: address.address,
        isDefault: address.isDefault
      }
      this.showAddDialog = true
    },
    
    resetForm() {
      this.isEdit = false
      this.addressForm = {
        id: null,
        receiverName: '',
        phone: '',
        region: [],
        province: '',
        city: '',
        district: '',
        address: '',
        isDefault: false
      }
      this.$nextTick(() => {
        this.$refs.addressForm && this.$refs.addressForm.clearValidate()
      })
    },
    
    handleRegionChange(values) {
      if (values && values.length === 3) {
        // 根据选择的地区代码找到对应的名称
        const province = this.findRegionName(this.regionOptions, values[0])
        const city = this.findRegionName(this.regionOptions, values[1], values[0])
        const district = this.findRegionName(this.regionOptions, values[2], values[1])
        
        this.addressForm.province = province
        this.addressForm.city = city
        this.addressForm.district = district
      }
    },
    
    findRegionName(options, code, parentCode = null) {
      for (const option of options) {
        if (option.value === code) {
          return option.label
        }
        if (option.children) {
          if (!parentCode || option.value === parentCode) {
            const result = this.findRegionName(option.children, code, parentCode)
            if (result) return result
          }
        }
      }
      return ''
    },
    
    async handleSubmit() {
      try {
        await this.$refs.addressForm.validate()
        
        this.submitting = true
        
        const data = {
          receiverName: this.addressForm.receiverName,
          phone: this.addressForm.phone,
          provinceCode: this.addressForm.region[0],
          cityCode: this.addressForm.region[1],
          districtCode: this.addressForm.region[2],
          province: this.addressForm.province,
          city: this.addressForm.city,
          district: this.addressForm.district,
          address: this.addressForm.address,
          isDefault: this.addressForm.isDefault
        }
        
        let res
        if (this.isEdit) {
          res = await updateAddress(this.addressForm.id, data)
        } else {
          res = await addAddress(data)
        }
        
        if (res.data.code === 200) {
          this.$message.success(this.isEdit ? '地址更新成功' : '地址添加成功')
          this.showAddDialog = false
          this.loadAddresses()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        if (error !== false) {
          console.error('提交地址失败:', error)
          this.$message.error('操作失败，请重试')
        }
      } finally {
        this.submitting = false
      }
    },
    
    async setDefaultAddress(addressId) {
      try {
        const res = await setDefaultAddress(addressId)
        if (res.data.code === 200) {
          this.$message.success('默认地址设置成功')
          this.loadAddresses()
        } else {
          this.$message.error(res.data.message || '设置失败')
        }
      } catch (error) {
        console.error('设置默认地址失败:', error)
        this.$message.error('设置失败，请重试')
      }
    },
    
    async deleteAddress(address) {
      try {
        await this.$confirm(
          `确定要删除收货地址"${address.receiverName} ${address.phone}"吗？`,
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        const res = await deleteAddress(address.id)
        if (res.data.code === 200) {
          this.$message.success('地址删除成功')
          this.loadAddresses()
        } else {
          this.$message.error(res.data.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除地址失败:', error)
          this.$message.error('删除失败，请重试')
        }
      }
    },
    
    getFullAddress(address) {
      return `${address.province}${address.city}${address.district}${address.address}`
    }
  }
}
</script>

<style scoped>
.address-management-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 10px;
  padding: 25px 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

/* 地址列表 */
.address-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

.loading-container {
  padding: 40px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-item {
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.address-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.address-item.is-default {
  border-color: #67c23a;
  background: #f0f9ff;
}

.address-content {
  padding: 20px;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.receiver-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.receiver-phone {
  font-size: 14px;
  color: #666;
}

.address-actions {
  display: flex;
  gap: 10px;
}

.delete-btn {
  color: #f56c6c !important;
}

.delete-btn:hover {
  color: #f78989 !important;
}

.address-detail {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.address-text {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.address-text i {
  color: #409EFF;
  margin-top: 2px;
  flex-shrink: 0;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

/* 对话框样式 */
.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .address-management-container {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
    text-align: center;
  }
  
  .address-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .receiver-info {
    flex-direction: column;
    gap: 5px;
    align-items: flex-start;
  }
  
  .address-actions {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .address-content {
    padding: 15px;
  }
  
  .receiver-name {
    font-size: 14px;
  }
  
  .receiver-phone {
    font-size: 13px;
  }
  
  .address-actions {
    flex-direction: column;
    gap: 5px;
  }
  
  .address-actions .el-button {
    width: 100%;
    margin: 0;
  }
}
</style>