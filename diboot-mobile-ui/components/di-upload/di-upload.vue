<template>
	<u-upload
	class="di-upload"
	:max-size="uploadConfig.maxSize"
	:max-count="uploadConfig.limitCount"
	:upload-text="uploadConfig.uploadText"
	:width="uploadConfig.width"
	:height="uploadConfig.height"
	name="file"
	:show-progress="uploadConfig.showProgress"
	:deletable="uploadConfig.deletable"
	:action="uploadAction"
	@on-success="handleSuccess"
	@on-remove="handleRemove"
	:form-data="uploadFormData"
	:header="header"
	:file-list="tempFileList"
	>
	</u-upload>
</template>

<script>
	/**
	 * 上传组件
	 * @property {String} action 上传文件地址，默认为：/uploadFile/upload/dto
	 * @property {String} rel-obj-type 绑定的业务对象，自动设置到form-data对象中
	 * @property {String} rel-obj-field 绑定的业务对象的字段，自动设置到form-data对象中
	 * @property {Object} form-data 提交的form-data
	 * @property {Array} fileList 文件存储数据
	 * @property {String} limit-count 限制数，自动设置到config对象中
	 * @property {String} show-progress 是否显示进度条 ，自动设置到config对象中
	 * @property {String} maxSize 选择单个文件的最大大小，单位B(byte)，默认不限制，自动设置到config对象中
	 * @property {String} deletable	是否显示删除图片的按钮，自动设置到config对象中
	 * @property {String} width 是否显示进度条 ，自动设置到config对象中
	 * @property {String} height 是否显示进度条 ，自动设置到config对象中
	 * @property {String} config 上传组件配置
	 * @event {Function} confirm 点击确定按钮，返回当前选择的值
	 */
	import Emitter from '../../uview-ui/libs/util/emitter.js';
	export default {
		mixins: [Emitter],
		computed: {
			header() {
				return {
					authtoken: uni.getStorageSync('authtoken') || ''
				}
			},
			// 更新回显
			tempFileList: {
				get() {
					return this.fileList
				},
				set() {}
			},
			uploadFormData() {
				let tempFormData = this.formData || {}
				tempFormData.relObjType = this.relObjType
				tempFormData.relObjField = this.relObjField
				return tempFormData
			},
			uploadConfig() {
				let tempConfig = this.config || {}
				tempConfig.maxSize = this.maxSize
				tempConfig.limitCount = this.limitCount
				tempConfig.uploadText = this.uploadText
				tempConfig.width = this.width
				tempConfig.height = this.height
				tempConfig.showProgress = this.showProgress
				tempConfig.deletable = this.deletable
				return tempConfig
			},
			uploadAction() {
				return `${this.$cons.host()}${this.action}`
			}
		},
		methods: {
			/**
			 * 上传成功触发
			 * @param {Object} data
			 * @param {Object} index
			 * @param {Object} lists
			 * @param {Object} name
			 */
			handleSuccess(data, index, lists, name) {
				lists.length = 0
				if (this.limitCount === 1) {
				    this.fileList.length = 0
				}
				this.fileList.push(this.fileFormatter(data.data))
				console.log('add end', this.fileList)
				this.$forceUpdate()
				this.handleInputEvent()
			},
			/**
			 * 删除图片触发
			 * @param {Object} index
			 * @param {Object} lists
			 * @param {Object} name
			 */
			handleRemove(index, lists, name) {
				const newFileList = this.fileList.slice()
				newFileList.splice(index, 1)
				this.fileList.length = 0
				this.fileList.push(...newFileList)
				console.log('remove end', this.fileList)
				this.handleInputEvent()
			},
			/**
			 * 文件转化
			 * 
			 * @param {Object} data
			 */
			fileFormatter(data) {
				return {
					uid: data.uuid,
					filePath: data.accessUrl,
					url: `${this.$cons.host()}${data.accessUrl}/image`
				}
			},
			/**
			 * 发送input消息
			 * 过一个生命周期再发送事件给u-form-item，否则this.$emit('input')更新了父组件的值
			 * 但是微信小程序上 尚未更新到u-form-item，导致获取的值为空
			 */
			handleInputEvent() {
				const value = this.fileList.map(file => file.filePath).join(',')
				this.$emit('input', value)
				this.$nextTick(function(){
					// 将当前的值发送到 u-form-item 进行校验
					this.dispatch('u-form-item', 'on-form-change', value);
				})
			}
		},
		watch: {
			tempFileList: {
				handler(val) {
					// 双向绑定
					this.$emit('update:fileList', val)
				}
			}
		},
		props: {
			action: {
				type: String,
				default: '/uploadFile/upload/dto'
			},
			relObjType: {
			  type: String,
			  required: true
			},
			relObjField: {
			  type: String,
			  required: true
			},
			// 回显操作
			fileList: {
				type: Array,
				require: true
			},
			limitCount: {
				type: Number,
				default: 1
			},
			maxSize: {
				type: Number,
				default: Number.MAX_VALUE
			},
			height: {
				type: [Number, String],
				default: Number.MAX_VALUE
			},
			width: {
				type: [Number, String],
				default: Number.MAX_VALUE
			},
			deletable: {
				type: Boolean,
				default: true
			},
			showProgress: {
				type: Boolean,
				default: true
			},
			uploadText: {
				type: String,
				default: '上传'
			},
			formData: {
				type: Object,
				default: () => {}
			},
			config: {
				type: Object,
				default: () => {}
			},
			value: {
				type: String,
				require: true
			}
		}
	}
</script>

<style lang="scss" scoped>
   .u-upload {
	   /deep/ .u-list-item {
		   display: flex;
	   }
   }
</style>
