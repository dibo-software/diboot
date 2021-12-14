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
					<u-radio-group v-model="form.taste" :active-color="activeColor">
						<u-radio v-for="(item, index) in radioList" :key="index" :name="item.name"
							:disabled="item.disabled">
							{{ item.name }}
						</u-radio>
					</u-radio-group>
				</u-form-item>
				<u-form-item label="开关">
					<u-switch slot="right" vibrate-short :active-color="activeColor" v-model="form.switchVal">
					</u-switch>
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
					<di-upload
						:config="uploadConfig"
						:action="action" 
						:form-data="formData" 
						:file-list="fileList"
						@add="addPicture" 
						@remove="removePicture"
					/>
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
						name: '鲜甜',
						disabled: false
					},
					{
						name: '麻辣',
						disabled: false
					}
				],
				uploadConfig: {
					options: {
						maxSize: 'Number.MAX_VALUE',
						maxCount: 9,
						uploadText: '选择图片',
						width: 140,
						height: 140,
						showProgress: false,
						deletable: false
					}
				},
				action: '/uploadFile/upload/dto',
				fileList: [],
				formData: {
					relObjField: '',
					relObjType: '',
				},
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
					// picture: [{
					// 	required: true,
					// 	message: '请上传图片',
					// 	trigger: ['blur', 'change']
					// }],
				}
			};
		},
		mixins:[form],
		methods: {
			// 删除文件
			removePicture(index, list) {
				this.fileList = this.fileList.filter((v, i) => i !== index)
				this.setUpload(this.fileList)
			},
			// 上传文件
			addPicture(list) {
				this.fileList.push(list[list.length - 1])
				this.setUpload(this.fileList)
			},
			setUpload(list) {
				let fileUuidList = []
				let pictureUrlList = list.map((item) => {
					if (item.response) {
						fileUuidList.push(item.response.data.uuid)
						return item.response.data.accessUrl
					} else {
						return ''
					}
				}) || []
				this.form['picture'] = pictureUrlList.join(",")
				this.form['fileUuidList'] = fileUuidList
			},
			/**
			 * 数据转化
			 */
			fileFormatter(data, isImage) {
				const file = {
					uid: data.uuid, // 文件唯一标识，建议设置为负数，防止和内部产生的 id 冲突
					name: data.fileName || ' ', // 文件名
					status: 'done', // 状态有：uploading done error removed
					response: {
						data: {
							accessUrl: data.accessUrl,
							fileName: data.fileName,
							uuid: data.uuid
						}
					}, // 服务端响应内容
					filePath: data.accessUrl
				}
				if (isImage) {
					Object.assign(file, {
						url: `${this.$cons.host()}${data.accessUrl}/image`,
						thumbUrl: `${this.$cons.host()}${data.accessUrl}/image`
					})
				}
				return file
			},
			submit() {
				this.$refs.uForm.validate(valid => {
					if (valid) {
						console.log('验证通过');
					} else {
						console.log('验证失败');
					}
				});
			}
		},
		// 必须要在onReady生命周期，因为onLoad生命周期组件可能尚未创建完毕
		onReady() {
			this.$refs.uForm.setRules(this.rules);
		}
	};
</script>
