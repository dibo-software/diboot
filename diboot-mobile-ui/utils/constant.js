/**
 *
 * 全局静态变量
 * 通过this.$cons.xx调用
 *
 * created by uu
 */
// 环境修改
const ENV = 'dev'

// 后端多环境配置
const hostConfig = {
	dev: "http://localhost:8080/api",
	test: "",
	prod: ""
}
/**
 * 前端页面配置(公众号重定向需要)
 */
const frontIndexConfig = {
	dev: "http://www.diboot.com", // 自行替换
	test: "",
	prod: ""
}
const cons = {

	/**
	 * 获取当前环境配置
	 * @param {Object} env {dev、test、prod}
	 */
	host(env = ENV) {
		return hostConfig[env]
	},

	/**
	 * 获取当前环境配置
	 * @param {Object} env {dev、test、prod}
	 */
	frontIndex(env = ENV) {
		return frontIndexConfig[env]
	}

}
export default cons
