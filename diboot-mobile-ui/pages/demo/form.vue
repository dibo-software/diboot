<template>
	<view class="u-p-24 page-bg-color" style="min-height: 100%;">
		<view class="page-card u-p-l-24 u-p-r-24 u-p-b-24">
			<u-form :model="form" ref="uForm" :label-width="150">
				<u-form-item label="姓名" prop="name">
					<u-input v-model="form.name" placeholder="请输入姓名" />
				</u-form-item>
				<u-form-item label="性别" prop="sex">
					<di-select v-model="form.sex" placeholder="请选择性别" :list="list"></di-select>
				</u-form-item>
				<u-form-item label="水果" prop="fruits">
					<di-checkbox-list v-model="form.fruits" :list="checkboxList"></di-checkbox-list>
				</u-form-item>
				<u-form-item label="味道" prop="taste">
					<di-radio-list v-model="form.taste" :list="radioList"/>
				</u-form-item>
				<u-form-item label="开关">
					<template slot="right">
						<di-switch v-model="form.switchVal"></di-switch>
					</template>
				</u-form-item>
				<u-form-item label="日历" prop="calendarDate">
					<di-calendar-picker v-model="form.calendarDate" placeholder="请选择日期范围"/>
				</u-form-item>
				<u-form-item label="日历范围" prop="calendarRange">
					<di-calendar-picker v-model="form.calendarRange" mode="range" placeholder="请选择日期范围"/>
				</u-form-item>
				<u-form-item label="地区" prop="region">
					<di-region-picker v-model="form.region" placeholder="请选择地区"/>
				</u-form-item>
				<u-form-item label="时间" prop="time">
					<di-date-picker v-model="form.time" placeholder="请选择时间" mode="datetime"/>
				</u-form-item>
				<u-form-item label="上传图片" prop="picture">
					<di-upload v-model="form.picture" :file-list="fileWrapper.pictureList" rel-obj-field="picture" :rel-obj-type="relObjType"/>
				</u-form-item>
			</u-form>
			<view class="u-m-t-60">
				<u-button @click="submit" type="success">提交</u-button>
			</view>
		</view>
	</view>
</template>

<script>
	import form from '@/mixins/form'
	export default {
		data() {
			return {
				form: {
					"name": "123",
					"taste": "麻辣",
					"sex": "1",
					"switchVal": true,
					"fruits": "apple,mango",
					"calendarDate": "2021-12-24",
					"calendarRange": "2021-12-14~2021-12-24",
					"region": "北京市-市辖区-东城区",
					"time": "2021-12-29 17:36:38",
					"picture": ""
				},
				list: [{
						value: '1',
						label: '男'
					},
					{
						value: '2',
						label: '女'
					}
				],
				checkboxList: [
					{
						value: 'apple',
						label: '苹果'
					},
					{
						value: 'banana',
						label: '香蕉'
					},
					{
						value: 'mango',
						label: '芒果'
					}
				],
				radioList: [{
						label: '鲜甜',
						value: '鲜甜'
					},
					{
						label: '麻辣',
						value: '麻辣'
					}
				],
				relObjType: 'Demo',
				fileWrapper: {
				  pictureList: []
				},
				isUpload: true,
				rules: {
					name: [{
						required: true,
						message: '请输入姓名',
						trigger: ['blur', 'change']
					}],
					sex: [{
						required: true,
						message: '请选择性别',
						trigger: ['blur', 'change']
					}],
					fruits: [{
						required: true,
						message: '请选择水果',
						trigger: ['blur', 'change']
					}],
					taste: [{
						required: true,
						message: '请选择味道',
						trigger: ['blur', 'change']
					}],
					calendarDate: [{
						required: true,
						message: '请选择日历',
						trigger: ['blur', 'change']
					}],
					calendarRange: [{
						required: true,
						message: '请选择日历范围',
						trigger: ['blur', 'change']
					}],
					region: [{
						required: true,
						message: '请选择地区',
						trigger: ['blur', 'change']
					}],
					time: [{
						required: true,
						message: '请选择时间',
						trigger: ['blur', 'change']
					}],
					picture: [{
						required: true,
						message: '请上传图片',
						trigger: ['blur', 'change']
					}],
				}
			};
		},
		mixins:[form],
		methods: {
			enhance (values) {
			  this.__setFileUuidList__(values)
			},
			
			/****
			 * 打开表单之后的操作
			 * @param id
			 */
			afterOpen (id) {
				// 回显图片
				if(id) {
					// this.$dibootApi.get(`/uploadFile/getList/${id}/${this.relObjType}/picture`).then(res => {
					//   if (res.code === 0) {
					// 	if (res.data && res.data.length > 0) {
					// 	  res.data.forEach(data => {
					// 		this.fileWrapper.pictureList.push(this.fileFormatter(data,true))
					// 	  })
					// 	}
					//   }
					// })
				}
			}
		},
		// 必须要在onReady生命周期，因为onLoad生命周期组件可能尚未创建完毕
		onReady() {
			this.$refs.uForm.setRules(this.rules);
		}
	};
</script>
