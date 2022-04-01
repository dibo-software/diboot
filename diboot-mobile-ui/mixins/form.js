import {dibootApi} from '@/utils/dibootApi'
import more from './more'

export default {
	mixins: [more],
	data() {
		return {
			// 主键字段名
			primaryKey: 'id',
			// 请求接口基础路径
			baseApi: '/',
			// 新建接口
			createApi: '',
			// 更新接口
			updateApiPrefix: '',
			// 标题
			title: '',
			// 存储当前对象form数据
			form: {},
			// 当前form是否包含上传
			isUpload: false,
			// 确认提交
			confirmSubmit: false,
			/**
			 * 所有文件的集合都放置与fileWrapper对象中，提交的时候会自动遍历
			 * 格式如下：
			 * fileWrapper: {
			 *  singleImageList: [],
			 *  multiImageList: [],
			 *  singleFileList: [],
			 *  multiFileList: []
			 * }
			 */
			fileWrapper: {},
			/**
			 * uuid集合
			 */
			fileUuidList: [],
			/**
			 *
			 * 激活的颜色：主要用于checkbox、radio等，保持风格统一
			 */
			activeColor: this.$color.success
		}
	},
	/**
	 * 打开表单
	 * @param id ；/test?id=1
	 */
	onLoad(option) {
		this.open(option.id)
	},
	methods: {
		/**
		 * 打开
		 * @param {Object} id
		 */
		async open(id) {
			if (id === undefined) {
				// 没有id数据则认为是新建
				this.title = '新建'
				this.afterOpen(id)
			} else {
				uni.showLoading({
				    title: '加载中'
				});
				try{
					// 否则作为更新处理
					const res = await dibootApi.get(`${this.baseApi}/${id}`)
					if (res.code === 0) {
						this.form = res.data
						this.title = '更新'
						this.afterOpen(id)
					} else {
						uni.showToast({
							title: res.msg
						});
					}
				} finally {
					uni.hideLoading()
				}
			}
			await this.attachMore()
		},
		afterOpen(id) {
		},
		/** *
		 * 提交前的验证流程
		 * @returns {Promise<any>}
		 */
		validate() {
			return new Promise((resolve, reject) => {
				// rules存在，进行校验
				if(this.$refs.uForm.rules && Object.keys(this.$refs.uForm.rules).length > 0) {
					this.$refs.uForm.validate(valid => {
						valid ? resolve(true) : reject(false)
					});
				} else {
					resolve(true)
				}
			})
		},
		/** *
		 * 提交前对数据的处理（在验证正确之后的处理）
		 * @param values 提交的参数
		 */
		async enhance(values) {},
		/** *
		 * 新建记录的提交
		 * @param values 提交的参数
		 * @returns {Promise<string>}
		 */
		async add(values) {
			const createApi = this.createApi ? this.createApi : '/'
			const res = await dibootApi.post(`${this.baseApi}${createApi}`, values)
			if (res.code === 0) {
				return {
					data: res.data,
					msg: '添加成功'
				}
			} else {
				throw new Error(res.msg)
			}
		},
		/** *
		 * 更新记录的提交
		 * @param values
		 * @returns {Promise<string>}
		 */
		async update(values) {
			const updateApiPrefix = this.updateApiPrefix ? this.updateApiPrefix : ''
			const res = await dibootApi.put(`${this.baseApi}${updateApiPrefix}/${this.form[this.primaryKey]}`, values)
			if (res.code === 0) {
				return {
					data: res.data,
					msg: '更新记录成功'
				}
			} else {
				throw new Error(res.msg)
			}
		},
		/** *
		 * 表单提交事件
		 * @returns {Promise<void>}
		 */
		async onSubmit() {
			this.confirmSubmit = true
			uni.showLoading({
			    title: '提交中...'
			});
			try {
				const valid = await this.validate()
				if(!valid) {
					uni.hideLoading()
					return
				}
				await this.enhance()
				let result = {}
				if (this.form[this.primaryKey] === undefined) {
					// 新增该记录
					result = await this.add(this.form)
				} else {
					// 更新该记录
					result = await this.update(this.form)
				}
				// 执行提交成功后的一系列后续操作
				this.submitSuccess(result)
			} catch (e) {
				// 执行提交失败后的一系列后续操作
				this.submitFailed(e)
				console.log(e)
			} finally {
				uni.hideLoading()
				this.confirmSubmit = false
			}
		},
		/** *
		 * 提交成功之后的处理
		 * @param msg
		 */
		submitSuccess(result) {
			uni.showToast({
				title: '操作成功',
				duration: 2000,
				success: ()=>{
					uni.navigateBack({
					    delta: 1
					});
				}
			});
		},
		/** *
		 * 提交失败之后的处理
		 * @param e
		 */
		submitFailed(e) {
			// 如果是字符串，直接提示
			let msg
			if (typeof e === 'string') {
				msg = e
			} else if (typeof e === 'boolean') {
				msg = ''
			} else {
				msg = e.message || e.msg
			}
			if(msg) {
				uni.showToast({ title: msg, icon: 'error'})
			}
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
		 * 将属性值转化为数组
		 * @param fieldName
		 * @param separator
		 */
		transformStr2Arr(fieldName, separator = ',') {
			this.$set(this.form, fieldName, this.strSplit(this.form[fieldName], separator))
		},
		/**
		 * 字符串分割
		 * @param str
		 * @param separator
		 */
		strSplit(str, separator = ',') {
			return str ? str.split(',') : []
		},
		/**
		 * 设置文件uuid
		 * @private
		 */
		__setFileUuidList__() {
			if (!this.isUpload) {
				return
			}
			// 如果包含上传功能，那么设置uuid
			this.fileUuidList = []
			const fileWrapperKeys = Object.keys(this.fileWrapper)
			if (fileWrapperKeys.length === 0) {
				return
			}
			for (const fileWrapperKey of fileWrapperKeys) {
				const tempFileList = this.fileWrapper[fileWrapperKey]
				if (tempFileList && tempFileList.length && tempFileList.length > 0) {
					this.fileUuidList.push(...tempFileList.map(item => item.uid))
				}
			}
			this.form['fileUuidList'] = this.fileUuidList
		},
		/**
		 * 初始化fileWrapper
		 * @private
		 */
		__defaultFileWrapperKeys__() {
			const fileWrapperKeys = Object.keys(this.fileWrapper)
			if (fileWrapperKeys.length > 0) {
				for (const fileWrapperKey of fileWrapperKeys) {
					this.fileWrapper[fileWrapperKey] = []
				}
			} else {
				this.fileWrapper = {}
			}
			this.fileUuidList = []
		}
	}
}
