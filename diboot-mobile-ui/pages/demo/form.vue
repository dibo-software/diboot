<template>
	<view class="u-p-24 page-bg-color" style="min-height: 100%;">
		<view class="card u-p-l-24 u-p-r-24 u-p-b-24">
			<u-form :model="form" ref="uForm" :label-width="150">
				<u-form-item label="姓名" prop="name">
					<u-input v-model="form.name" placeholder="请输入姓名" />
				</u-form-item>
				<!-- <u-form-item label="性别" prop="sex">
					<u-input v-model="form.sexLabel" @click="selectShow = true" disabled :select-open="selectShow"
						type="select" placeholder="请选择性别" />
				</u-form-item> -->
				<u-form-item label="单列选择" prop="sex">
					<!-- <u-input v-model="form.sexLabel" @click="selectShow = true" disabled :select-open="selectShow"
						type="select" placeholder="请选择性别" /> -->
						<!-- <di-select
						v-model="form.sex"
						placeholder="请选择性别"
						mode="mutil-column"
						:label-value-list="list2"
						></di-select> -->
				</u-form-item>
				<u-form-item label="水果" prop="fruitsLabel">
					<u-checkbox-group @change="checkboxGroupChange" :active-color="activeColor">
						<u-checkbox v-model="item.checked" v-for="(item, index) in checkboxList" :key="index"
							:name="item.name">
							{{ item.name }}
						</u-checkbox>
					</u-checkbox-group>
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
					<u-input v-model="form.calendarDate" disabled @click="openCalendar('date')" placeholder="请选择日历" />
				</u-form-item>
				<u-form-item label="日历范围" prop="calendarRange">
					<u-input v-model="form.calendarRange" disabled @click="openCalendar('range')"
						placeholder="请选择日历范围" />
				</u-form-item>
				<u-form-item label="滑块">
					<u-slider v-model="form.slider" :active-color="activeColor"></u-slider>
				</u-form-item>
				<u-form-item label="步进器">
					<u-number-box v-model="form.numberBox" :step="5" :min="0" :max="100"></u-number-box>
				</u-form-item>
				<u-form-item label="评分">
					<u-rate :count="5" v-model="form.rate" :active-color="activeColor"></u-rate>
				</u-form-item>
				<u-form-item label="地区" prop="region">
					<u-input v-model="form.region" @click="openPicker('region')" disabled :select-open="pickerShow"
						type="select" placeholder="请选择地区" />
				</u-form-item>
				<u-form-item label="时间" prop="time">
					<u-input v-model="form.time" @click="openPicker('time')" disabled :select-open="pickerShow"
						type="select" placeholder="请选择时间" />
				</u-form-item>
				<u-form-item label="上传图片" prop="picture">
					<di-upload
						:config="uploadConfig"
						:action="action" 
						:form-data="formData" 
						:file-list="fileList"
						@add="addPicture" 
						@remove="removePicture"
					>
						
					</di-upload>
				</u-form-item>
			</u-form>
			<view class="u-m-t-60">
				<u-button @click="submit" type="success">提交</u-button>
			</view>
		</view>
		<u-select v-model="selectShow" :list="list" @confirm="selectConfirm"></u-select>
		<u-picker v-model="pickerShow" :params="pickerParams" :mode="pickerMode" @confirm="pickerConfirm"></u-picker>
		<u-calendar v-model="calendarShow" :mode="calendarMode" @change="calendarChange" btn-type="success"
			:active-bg-color="activeColor" safe-area-inset-bottom z-index="99999"></u-calendar>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				selectShow: false,
				pickerShow: false,
				pickerMode: 'region',
				pickerParams: {
					year: true,
					month: true,
					day: true,
					hour: true,
					minute: true,
					second: true,
					province: true,
					city: true,
					area: true,
					timestamp: false, // 选择时间的时间戳
				},
				calendarShow: false,
				calendarMode: 'date',
				form: {
					name: '',
					sex: '',
					sexLabel: '',
					fruits: [],
					fruitsLabel: '',
					taste: '',
					switchVal: true,
					calendarDate: '',
					calendarRange: '',
					slider: 20,
					numberBox: 30,
					rate: 2,
					region: '',
					time: '',
					picture: ''
				},
				activeColor: this.$color.success,
				list: [{
						value: '1',
						label: '男'
					},
					{
						value: '2',
						label: '女'
					}
				],
				list2: [
									[
										{
											value: '1',
											label: '江'
										},
										{
											value: '2',
											label: '湖'
										}
									],
									[
										{
											value: '3',
											label: '夜'
										},
										{
											value: '4',
											label: '雨'
										}
									],[
										{
											value: '6',
											label: '夜'
										},
										{
											value: '7',
											label: '雨'
										}
									],
									
								],
				list3: [
					{
						value: 1,
						label: '中国',
						children: [
							{
								value: 2,
								label: '广东',
								children: [
									{
										value: 3,
										label: '深圳'
									},
									{
										value: 4,
										label: '广州'
									}
								]
							},
							{
								value: 5,
								label: '广西',
								children: [
									{
										value: 6,
										label: '南宁'
									},
									{
										value: 7,
										label: '桂林'
									}
								]
							}
						]
					},
					{
						value: 8,
						label: '美国',
						children: [
							{
								value: 9,
								label: '纽约',
								children: [
									{
										value: 10,
										label: '皇后街区'
									}
								]
							}
						]
					}
				],
				checkboxList: [{
						name: '苹果',
						checked: false,
						disabled: false
					},
					{
						name: '雪梨',
						checked: false,
						disabled: false
					},
					{
						name: '柠檬',
						checked: false,
						disabled: false
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
				action: `/uploadFile/upload/dto`,
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
						type: 'array',
						trigger: ['blur', 'change']
					}],
					fruitsLabel: [{
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
		methods: {
			selectConfirm(list) {
				this.form.sex = list[0].value
				this.form.sexLabel = list[0].label
			},
			checkboxGroupChange(e) {
				this.form.fruits = e
				this.form.fruitsLabel = e.join(',')
			},
			openCalendar(mode) {
				this.calendarMode = mode
				this.calendarShow = true
			},
			calendarChange(obj) {
				if (this.calendarMode === 'date') {
					this.form.calendarDate = obj.result
				} else {
					this.form.calendarRange = obj.startDate + ' - ' + obj.endDate
				}
			},
			openPicker(mode) {
				this.pickerMode = mode
				this.pickerShow = true
			},
			pickerConfirm(obj) {
				if (this.pickerMode === 'region') {
					this.form.region = obj.province.label + '-' + obj.city.label + '-' + obj.area.label
				} else if (this.pickerMode === 'time') {
					this.form.time = obj.year + '-' + obj.month + '-' + obj.day + ' ' + obj.hour + ':' + obj.minute + ':' +
						obj.second
				}
			},
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
