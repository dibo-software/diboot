<template>
	<view class="u-p-24 page-bg-color" style="min-height: 100%;">
		<u-toast ref="uToast" />
		<view class="page-card u-p-l-24 u-p-r-24 u-p-b-24">
			
			<u-form :model="form" ref="uForm" :label-width="150">
        <u-form-item label="单行" prop="testInput" required>   
          <u-input v-model="form.testInput" placeholder="请输入单行" />
        </u-form-item>
        <u-form-item label="多行" prop="testTextarea" required>   
          <u-input type="textarea" v-model="form.testTextarea" placeholder="请输入多行" />
        </u-form-item>
        <u-form-item label="单文件">   
        </u-form-item>
        <u-form-item label="多文件">   
        </u-form-item>
        <u-form-item label="单图" prop="testSingleImg" required>   
        </u-form-item>
        <u-form-item label="多图" prop="testMultiImg" required>
			<di-upload
				v-model="form.testMultiImg"
				:file-list="fileWrapper.testMultiImgList"
				rel-obj-field="testMultiImg"
				rel-obj-type="TestAllField"
			/>
        </u-form-item>
        <u-form-item label="多选select" prop="testMultiSelect" required>   
          <u-input v-model="form.testMultiSelect" placeholder="请输入多选select" />
        </u-form-item>
        <u-form-item label="单选select" prop="testSingleSelect" required>   
          <di-select v-model="form.testSingleSelect" placeholder="请选择单选select" :list="more.genderOptions"/>
        </u-form-item>
        <u-form-item label="checkbox" prop="testCheckbox" required>   
          <di-checkbox-list v-model="form.testCheckbox" :list="more.orderStatusOptions"/>
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
      baseApi: '/testAllField',
	  rules:{},
	 //  rules: {
		// testInput: [{
		// 		required: true,
		// 		message: '单行不能为空',
		// 		trigger: ['blur', 'change']
		// 	}],      	
		// testTextarea: [{
		// 		message: '多行不能为空',
		// 		trigger: ['blur', 'change']
		// 	}],      	
		// testSingleFile: [{
		// 		message: '单文件不能为空',
		// 		trigger: ['blur', 'change']
		// 	}],      	
		// testMultiFile: [{
		// 		message: '多文件不能为空',
		// 		trigger: ['blur', 'change']
		// 	}],      	
		// testSingleImg: [{
		// 		message: '单图不能为空',
		// 		trigger: ['blur', 'change']
		// 	}],      	
		// testMultiImg: [{
		// 		message: '多图不能为空',
		// 		trigger: ['blur', 'change']
		// 	}],      	
		// testMultiSelect: [{
		// 		message: '多选select不能为空',
		// 		trigger: ['blur', 'change']
		// 	}],      	
		// testSingleSelect: [{
		// 		message: '单选select不能为空',
		// 		trigger: ['blur', 'change']
		// 	}],      	
		// testCheckbox: [{
		// 		message: 'checkbox不能为空',
		// 		trigger: ['blur', 'change']
		// 	}],      	
		// },
      attachMoreList: [
        {
          type: 'D',
          target: 'GENDER'
        },
        {
          type: 'D',
          target: 'ORDER_STATUS'
        }
      ],
      filePrefix: '/api',
      fileAction: '/uploadFile/upload/dto',
      relObjType: 'TestAllField',
      fileWrapper: {
        testSingleFileList: [],
        testMultiFileList: [],
        testSingleImgList: [],
        testMultiImgList: [],
      },
      isUpload: true
    }
  },
  methods: {
    enhance() {
      this.__setFileUuidList__()
    },
    /****
     * 打开表单之后的操作
     * @param id
     */
    afterOpen (id) {
		console.log('-------', id)
    	if(id) {
        // 回显单文件
        if (this.form.testSingleFileFile) {
          this.fileWrapper.testSingleFileList = [this.fileFormatter(this.form.testSingleFileFile)]
        }
        // 回显多文件
        if (this.form.testMultiFileFileList && this.form.testMultiFileFileList.length > 0) {
          this.fileWrapper.testMultiFileList = this.form.testMultiFileFileList.map(file => this.fileFormatter(file))
        }
        // 回显单图
        this.$dibootApi.get(`/uploadFile/getList/${id}/${this.relObjType}/testSingleImg`).then(res => {
          if (res.code === 0) {
            if (res.data && res.data.length > 0) {
              res.data.forEach(data => {
                this.fileWrapper.testSingleImgList.push(this.fileFormatter(data, true))
              })
            }
          }
        })
        // 回显多图
		this.$dibootApi.get(`/uploadFile/getList/${id}/${this.relObjType}/testMultiImg`).then(res => {
          if (res.code === 0) {
            if (res.data && res.data.length > 0) {
              res.data.forEach(data => {
                this.fileWrapper.testMultiImgList.push(this.fileFormatter(data, true))
              })
            }
          }
        })
      }
    }
  }
}
</script>
<style lang="sass" scoped>
</style>
