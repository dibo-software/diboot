<template>
	<u-checkbox-group ref='diCheckbox' class="di-checkbox-list" @change="handleChange" :active-color="activeColor">
		<u-checkbox class="di-checkbox-list__item"  v-model="item.checked" v-for="(item, index) in checkboxList" :key="index"
			:name="item.value">
			{{ item.label }}
		</u-checkbox>
	</u-checkbox-group>
</template>

<script>
	/**
	* di-checkbox-list checkbox列表
	* @description checkbox列表组件，封装u-checkbox，适配form
	* @property  {Array String}  value 支持数组和字符串，修改后响应为，所以建议使用字符串
	* @property  {String}  active-color 激活时候的颜色
	* @property  {Array}  list 传入labelValue列表
	*/
	export default {
		name: 'di-checkbox-list',
		data() {
			return {
				checkboxList: []
			}
		},
		methods: {
			/**
			 * 改变选择
			 * @param {Object} e
			 */
			handleChange() {
				let values = [];
				this.checkboxList.map(val => {
					if(val.checked) values.push(val.value);
				})
				let valueStr = values.join(',')
				this.$emit('input', valueStr)
				this.$forceUpdate()
				// 由于头条小程序执行迟钝，故需要用几十毫秒的延时
				setTimeout(() => {
					this.$refs.diCheckbox.dispatch('u-form-item', 'on-form-change', valueStr);
				}, 60)
				
			},
			/**
			 * 渲染成适配uview数据
			 * @param {Object} list
			 */
			__setCheckboxList(list) {
				let time = setTimeout(() => {
					clearTimeout(time)
				this.checkboxList = list.map(item => {
					item['checked'] = this.value2List.includes(item.value)
					return item
				})}, 0)
			}
		},
		computed: {
			/**
			 * 将数据转化成list
			 */
			value2List() {
				return this.value && typeof this.value === 'string' && this.value.split(',') || this.value || []
			}
		},
		watch: {
			list: {
				immediate: true,
				handler(value) {
					this.__setCheckboxList(value)
				}
			}
		},
		props: {
			value: {
				type: [Array, String],
				require: true
			},
			list: {
				type: Array,
				require: true
			},
			activeColor: {
				type: String,
				default: '#19be6b'
			}
		}
	}
</script>

<style lang="scss">

</style>
