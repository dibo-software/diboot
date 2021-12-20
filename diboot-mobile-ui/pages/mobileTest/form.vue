<template>
	<view class="u-p-24 page-bg-color" style="min-height: 100%;">
		<view class="page-card u-p-l-24 u-p-r-24 u-p-b-24">
			<u-form :model="form" ref="uForm" :label-width="150">
        <u-form-item label="单行输入">   
          <u-input v-model="form.testInput" placeholder="请输入单行输入" />
        </u-form-item>
        <u-form-item label="多行输入" prop="testTextarea" required>   
          <u-input type="textarea" v-model="form.testTextarea" placeholder="请输入多行输入" />
        </u-form-item>
        <u-form-item label="单图">   
          <di-upload v-model="form.testSingleImg" :file-list="fileWrapper.testSingleImgList" rel-obj-field="testSingleImg" :rel-obj-type="relObjType"/>
        </u-form-item>
        <u-form-item label="多图" prop="testMultiImg" required>   
          <di-upload v-model="form.testMultiImg" :file-list="fileWrapper.testMultiImgList" rel-obj-field="testMultiImg" :rel-obj-type="relObjType" :limit-count="9"/>   
        </u-form-item>
        <u-form-item label="多文件">   
          <di-upload v-model="form.testMultiFile" :file-list="fileWrapper.testMultiFileList" rel-obj-field="testMultiFile" :rel-obj-type="relObjType" :limit-count="9"/>   
        </u-form-item>
        <u-form-item label="单文件">   
          <di-upload v-model="form.testSingleFile" :file-list="fileWrapper.testSingleFileList" rel-obj-field="testSingleFile" :rel-obj-type="relObjType"/>
        </u-form-item>
        <u-form-item label="多选">   
          <di-select v-model="form.testMultiSelect" placeholder="请选择多选" :list="more.positionGradeOptions"/>
        </u-form-item>
        <u-form-item label="单选" prop="testSingleSelect" required>   
          <di-select v-model="form.testSingleSelect" placeholder="请选择单选" :list="more.genderOptions"/>
        </u-form-item>
        <u-form-item label="checkbox" prop="testBox" required>   
          <di-select v-model="form.testBox" placeholder="请选择checkbox" :list="more.orderStatusOptions"/>
        </u-form-item>
        <u-form-item label="日期" prop="testDate" required>   
          <di-calendar-picker v-model="form.testDate" placeholder="请选择日期"/>
        </u-form-item>
        <u-form-item label="日期时间" prop="testTime" required>   
          <di-date-picker v-model="form.testTime" placeholder="请选择日期时间" mode="datetime"/>
        </u-form-item>
			</u-form>
			<view class="u-m-t-60">
				<u-button @click="onSubmit" type="success">提交</u-button>
			</view>
		</view>
	</view>
</template>
<script>
import form from '@/mixins/form'
export default {
  onReady() {
		this.$refs.uForm.setRules(this.rules)
	},
  mixins: [form],
  data () {
    return {
      baseApi: '/mobileTest',
	 rules: {
   
		testTextarea: [{
    		required: true,
				message: '多行输入不能为空',
				trigger: ['blur', 'change']
			}],    
		testMultiImg: [{
    		required: true,
				message: '多图不能为空',
				trigger: ['blur', 'change']
			}],    
		testSingleSelect: [{
    		required: true,
				message: '单选不能为空',
				trigger: ['blur', 'change']
			}],    
		testBox: [{
    		required: true,
				message: 'checkbox不能为空',
				trigger: ['blur', 'change']
			}],    
		testDate: [{
    		required: true,
				message: '日期不能为空',
				trigger: ['blur', 'change']
			}],    
		testTime: [{
    		required: true,
				message: '日期时间不能为空',
				trigger: ['blur', 'change']
			}]    
		},
      attachMoreList: [
        {
          type: 'D',
          target: 'POSITION_GRADE'
        },
        {
          type: 'D',
          target: 'GENDER'
        },
        {
          type: 'D',
          target: 'ORDER_STATUS'
        }
      ],
      relObjType: 'MobileTest',
      fileWrapper: {
        testSingleImgList: [],
        testMultiImgList: [],
        testMultiFileList: [],
        testSingleFileList: [],
      },
      isUpload: true
    }
  },
  methods: {
    enhance (values) {
      this.__setFileUuidList__(values)
    },

    /****
     * 打开表单之后的操作
     * @param id
     */
    afterOpen (id) {
    	if(id) {
        // 回显单图
        this.$dibootApi.get(`/uploadFile/getList/${id}/${this.relObjType}/testSingleImg`).then(res => {
          if (res.code === 0) {
            if (res.data && res.data.length > 0) {
              res.data.forEach(data => {
                this.fileWrapper.testSingleImgList.push(this.fileFormatter(data,
 true))
              })
            }
          }
        })
        // 回显多图
        this.$dibootApi.get(`/uploadFile/getList/${id}/${this.relObjType}/testMultiImg`).then(res => {
          if (res.code === 0) {
            if (res.data && res.data.length > 0) {
              res.data.forEach(data => {
                this.fileWrapper.testMultiImgList.push(this.fileFormatter(data,
 true))
              })
            }
          }
        })
        // 回显多文件
        this.$dibootApi.get(`/uploadFile/getList/${id}/${this.relObjType}/testMultiFile`).then(res => {
          if (res.code === 0) {
            if (res.data && res.data.length > 0) {
              res.data.forEach(data => {
                this.fileWrapper.testMultiFileList.push(this.fileFormatter(data,
 true))
              })
            }
          }
        })
        // 回显单文件
        this.$dibootApi.get(`/uploadFile/getList/${id}/${this.relObjType}/testSingleFile`).then(res => {
          if (res.code === 0) {
            if (res.data && res.data.length > 0) {
              res.data.forEach(data => {
                this.fileWrapper.testSingleFileList.push(this.fileFormatter(data,
 true))
              })
            }
          }
        })
      }
    }
,
  }
}
</script>
<style lang="sass" scoped>
</style>
