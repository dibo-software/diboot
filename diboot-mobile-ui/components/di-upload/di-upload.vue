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
	 * di-upload 上传组件
	 * @description 上传组件，基于u-upload，适配form
	 * @property {String} value 上传后的后端返回地址，支持v-model双向绑定
	 * @property {String} action 向后端发送的请求地址，默认'/uploadFile/upload/dto'
	 * @property {String} rel-obj-type 绑定的业务对象名，自动设置到form-data对象中
	 * @property {String} rel-obj-field 绑定业务对象的属性，自动设置到form-data对象中
	 * @property {Object} form-data 提交的form-data，优先级最低，单独的属性设置会覆盖对象的配置
	 * @property {Array} file-list 文件存储位置
	 * @property {String} limit-count 上传数量限制，默认1，自动设置到config对象中
	 * @property {Boolean} show-progress 是否显示进度条，默认true，自动设置到config对象中
	 * @property {String} max-size 选择单个文件的最大大小，单位B(byte)，默认不限制，自动设置到config对象中
	 * @property {Boolean} deletable 是否显示删除图片的按钮，默认true，自动设置到config对象中
	 * @property {String} width 图片预览区域和添加图片按钮的宽度(单位：rpx)，默认200 ，自动设置到config对象中
	 * @property {String} height 图片预览区域和添加图片按钮的高度(单位：rpx) ，默认200,自动设置到config对象中
	 * @property {String} upload-text 上传框里面的文本 ，默认 ‘上传’,自动设置到config对象中
	 * @property {Object} config 上传组件配置，优先级最低，单独的属性设置会覆盖对象的配置
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
				this.$emit('update:fileList', this.fileList)
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
				this.$emit('update:fileList', this.fileList)
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
		props: {
			value: {
				type: String,
				require: true
			},
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
				default: 200
			},
			width: {
				type: [Number, String],
				default: 200
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
