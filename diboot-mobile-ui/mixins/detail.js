import {
	dibootApi
} from '@/utils/request'

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
		const res = await dibootApi.get(`${this.baseApi}/${option.id}`)
		if (res.code === 0) {
			this.model = res.data、
		} else {
			uni.showToast({
				title: '获取数据失败',
				icon: 'error'
			});
		}
	},
	methods: {
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
