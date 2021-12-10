<template>
	<u-upload
	:max-size="config.options.maxSize"
	:max-count="config.options.maxCount"
	:upload-text="config.options.uploadText"
	:width="config.options.width"
	:height="config.options.height"
	name="file"
	:show-progress="config.options.showProgress"
	:deletable="config.options.deletable"
	:action="action"
	@on-success="handleSuccess"
	@on-remove="handleRemove"
	:form-data="formData"
	:header="header"
	:file-list="fileList">
	</u-upload>
</template>

<script>
	export default {
		computed: {
			header() {
				return {
					authtoken: uni.getStorageSync('authtoken') || ''
				}
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
				this.$emit("add", lists)
			},
			/**
			 * 删除图片触发
			 * @param {Object} index
			 * @param {Object} lists
			 * @param {Object} name
			 */
			handleRemove(index, lists, name) {
				this.$emit("remove", index, lists)
			}
		},
		props: {
			// upload配置信息
			config: {
				type: Object,
				require: true
			},
			// 请求地址
			action: {
				type: String,
				default: '/uploadFile/upload/dto'
			},
			// 回显操作
			fileList: {
				type: Array,
				require: true
			},
			//上传的提交内容
			formData: {
				type: Object,
				require: true
			},
			
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
