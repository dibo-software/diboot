<template>
	<di-descriptions :title="model.title" :title-bottom="true">
		<u-tag slot="right" :text="model.sexLabel" type="success" size="mini"/>
		<di-descriptions-item label-col="0" value="12">
			<u-image slot="value" width="160rpx" height="160rpx" :src="model.avatar" round />
		</di-descriptions-item>
		<di-descriptions-item label="姓名" :value="model.name"/>
		<di-descriptions-item label="年龄" :value="model.age"/>
		<di-descriptions-item label="链接" :ellipsis="true">
			<u-link slot="value" href="https://www.diboot.com">Diboot 低代码开发平台：写的更少，性能更好 - 开发人员的低代码框架</u-link>
		</di-descriptions-item>
		<di-descriptions-item label="简介">
			<u-parse slot="value" :html="model.description" :use-cache="true" :selectable="true" :lazy-load="true" loading-img="https://dibootm.oss-cn-shanghai.aliyuncs.com/loading-img.png"></u-parse>
		</di-descriptions-item>
	</di-descriptions>
</template>

<script>
	import detail from '@/mixins/detail'
	export default {
		data() {
			return {
				model: {
					title: '用户明细',
					sexLabel: '男',
					avatar: require('@/static/logo.png'),
					name: 'diboot-mobile-ui',
					age: '1',
					description: '<p>简述：请点击链接查看更多：<img style="max-width:100%" alt="logo" src="https://dibootm.oss-cn-shanghai.aliyuncs.com/loading-img.png" data-img-size-val="900,500"/></p>'
				}
			}
		},
		mixins:[detail]
	}
</script>

<style>
</style>
