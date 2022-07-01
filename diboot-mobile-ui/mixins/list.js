import {dibootApi} from '@/utils/dibootApi'
import more from './more'

export default {
	mixins: [more],
	data() {
		return {
			primaryKey: 'id',
			// 请求接口基础路径
			baseApi: '/',
			// 列表数据接口
			listApi: '',
			// 删除接口
			deleteApiPrefix: '',
			// 是否在页面初始化时自动加载列表数据
			getListFromMixin: true,
			// 与查询条件绑定的参数（会被查询表单重置和改变的参数）
			queryParam: {},
			// //下拉刷新的状态
			triggered: false,
			// load状态
			status: 'loadmore',
			loadText: {
				loadmore: '上拉加载更多',
				loading: '努力加载中',
				nomore: '没有更多了'
			},
			// 分页
			page: {
				pageIndex: 1,
				pageSize: 20,
				totalCount: 0,
				totalPage: 0
			},
			// 激活的Index
			activeIndex: -100,
			// 是否弹出删除
			deleteShow: false,
			// 右滑菜单列表
			actionOptions: [{
				text: '编辑',
				type: 'handleUpdate',
				style: {
					backgroundColor: this.$color.warning
				}
			}, {
				text: '删除',
				type: 'handleDelete',
				style: {
					backgroundColor: this.$color.error
				}
			}],
			// 数据列表
			list: [],
			// 是否允许访问详情
			allowGoDetail: true,
			// 状态栏高度
			diStatusBarHeight: 0,
			// 阻止重复发送
			keyMap: {}
		}
	},
	onLoad() {
		this.diStatusBarHeight = uni.getSystemInfoSync().statusBarHeight

	},
	onShow() {
		this.activeIndex = -100
		this.getListFromMixin && this.getList(true)
	},
	methods: {
		/**
		 * 新增
		 */
		handleCreate() {
			uni.navigateTo({
				url: './form'
			})
		},
		/*
		 * 详情
		 */
		handleDetail(id) {
			if(!this.allowGoDetail) {
				return
			}
			uni.navigateTo({
				url:`./detail?id=${id}`
			})
		},
		/*
		 * 编辑
		 */
		handleUpdate(id) {
			uni.navigateTo({
				url: `./form?id=${id}`
			})
		},
		/**
		 * 删除
		 */
		handleDelete(id) {
			this.deleteShow = true
			this.activeIndex = id
		},
		/**
		 * 确认删除
		 * @param {Object} id
		 */
		async handleConfirmDel() {
			try{
				const deleteApiPrefix = this.deleteApiPrefix ? this.deleteApiPrefix : ''
				const res = await dibootApi.delete(`${this.baseApi}${deleteApiPrefix}/${this.activeIndex}`)
				this.showToast(res.msg, res.code === 0 ? 'success' : 'error')
			}catch(e){
				console.log(e)
				this.showToast('网络异常！')
			} finally {
				this.page.pageIndex = 1
				this.setQueryParamPage()
				this.getList(true)
				this.handleCancelDel()
			}

		},
		/**
		 * 取消删除
		 */
		handleCancelDel() {
			this.deleteShow = false
			this.activeIndex = -100
		},
		/*
		 * 打开左滑操作
		 */
		handleActiveSwipeAction(index) {
			this.activeIndex = index
		},
		/**
		 * 点击左滑按钮
		 * @param {Number} index  所在列表的primaryKey
		 * @param {Number} optionIdx  操作列表actionOptions的下标
		 */
		handleActionClick(index, optionIdx) {
			this[this.actionOptions[optionIdx]['type']](index)
		},
		/**
		 * 下拉刷新
		 */
		handlePullDownRefresh() {
			if (this.triggered) return
			this.triggered = true
			this.page.pageIndex = 1
			this.setQueryParamPage()
			this.getList(true)
		},
		/**
		 * 触底加载
		 */
		handleOnreachBottom() {
			// 将当前pageIndex制作成下标，并查看缓存中是否存在指定下标的值
			const key = `_${this.page.pageIndex}`
			const value = this.keyMap[key]
			// 如果value存在，表示缓存有值，那么阻止请求
			if(value) {
				return
			}
			// value不存在，表示第一次请求,设置占位
			this.keyMap[key] = 'temp'
			this.status = 'nomore'
			if (this.page.pageIndex <= this.page.totalPage) {
				this.setQueryParamPage()
				this.getList()
			}
		},
		/**
		 * 获取数据列表
		 */
		async getList(replace = false) {
			try{
				this.status = 'loading'
				const res = await dibootApi.get(this.listApi ? `${this.baseApi}/${this.listApi}` : `${this.baseApi}/list`, this.queryParam)
				if (res.code === 0) {
					this.list = replace ? res.data : this.list.concat(res.data)
					this.page = res.page
					this.page.pageIndex++
					console.log(this.page)
				} else {
					this.showToast(res.msg)
				}
			}catch(e){
				//TODO handle the exception
			} finally {
				this.triggered = false
				this.status = (this.list || []).length == this.page.totalCount ? 'nomore' : 'loadmore'
			}

		},
		setQueryParamPage() {
			this.queryParam.pageIndex = this.page.pageIndex
		},
		/**
		 * 展示提示
		 * @param {Object} title 提示内容
		 * @param {Object} icon 提示icon, 默认使用error
		 */
		showToast(title, icon = 'error') {
			uni.showToast({
				title,
				icon
			});
		}
	},
	computed: {
		listMargin() {
			return `margin: ${this.list.length === 0 ? 0 : 20}rpx`
		}
	}
}