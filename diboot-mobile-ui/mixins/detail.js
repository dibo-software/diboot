import {dibootApi} from '@/utils/dibootApi'

export default {
	data() {
		return {
			// 请求接口基础路径
			baseApi: '/',
			// 当前详情框详情数据
			model: {}
		}
	},
	/**
	 * 打开详情
	 * @param id ；/test?id=1
	 */
	onLoad(option) {
		this.open(option.id)
	},
	methods: {
		/**
		 * 打开详情
		 * @returns {Promise<void>}
		 */
		async open(id) {
			const res = await dibootApi.get(`${this.baseApi}/${id}`)
			if (res.code === 0) {
				this.model = res.data
			} else {
				uni.showToast({
					title: '获取数据失败',
					icon: 'error'
				});
			}
		},
		/**
		 * 预览保存图片
		 * @param path
		 */
		previewImage(path) {
			uni.previewImage({
				urls: [path],
				longPressActions: true
			})
		}
	}
}
