import {dibootApi} from '@/utils/dibootApi'

export default {
	data() {
		return {
			// 请求接口基础路径
			baseApi: '/',
			// 是否从当前业务的attachMore接口中自动获取关联数据
			getMore: false,
			// 获取关联数据列表的配置列表
			attachMoreList: [],
			// 远程过滤关联数据列表的配置对象
			attachMoreLoader: {},
			// 远程过滤加载状态
			attachMoreLoading: false,
			// 关联相关的更多数据
			more: {}
		}
	},
	methods: {
		/**
		 * 加载当前页面关联的对象或者字典
		 */
		async attachMore() {
			const reqList = []
			// 个性化接口
			this.getMore === true && reqList.push(dibootApi.get(`${this.baseApi}/attachMore`))
			// 通用获取当前对象关联的数据的接口
			this.attachMoreList.length > 0 && reqList.push(dibootApi.post('/common/attachMore', this
				.attachMoreList))
			if (reqList.length > 0) {
				const resList = await Promise.all(reqList)
				resList.forEach(res => res.ok ? Object.keys(res.data).forEach(key => this.$set(this.more, key, res.data[key]))
					: uni.showToast({title: res.msg || '获取选项数据失败', icon: 'error'}))
			}
		},
		/**
		 * 远程过滤加载选项
		 *
		 * @param value 输入值
		 * @param loader 加载器类型
		 */
		attachMoreFilter(value, loader) {
			if (value == null || (value = value.trim()).length === 0) {
				this.$set(this.more, `${loader}Options`, [])
				return
			}
			this.attachMoreLoading = true
			const moreLoader = this.attachMoreLoader[loader]
			moreLoader.keyword = value
			dibootApi.post('/common/attachMoreFilter', moreLoader).then(res => {
				res.ok ? this.$set(this.more, `${loader}Options`, res.data)
					: uni.showToast({title: res.msg || '获取选项数据失败', icon: 'error'})
				this.attachMoreLoading = false
			}).catch(() => {
				uni.showToast({title: res.msg || '获取选项数据失败', icon: 'error'})
				this.attachMoreLoading = false
			})
		}
	}
}
