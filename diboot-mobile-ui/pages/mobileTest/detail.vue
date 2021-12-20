<template>

<di-descriptions>
   		<view slot="title">
        <u-tag :text="model.testDate" size="mini" />
      </view>
    <di-descriptions-item label="单行输入" :value="model.testInput"/>
    <di-descriptions-item label="多行输入" :value="model.testTextarea"/>
    <di-descriptions-item label="富文本" label-col="12" value-col="12">
			<u-parse slot="value" :html="model.testRichtext" :use-cache="true" :selectable="true" :lazy-load="true"/>
		</di-descriptions-item>
		<di-descriptions-item label="单图" label-col="12" value-col="12">
			<u-image v-if="model.testSingleImg" slot="value" width="160rpx" height="160rpx" :src="`${$cons.host()}${model.testSingleImg}/image`"/>
		</di-descriptions-item>
		<di-descriptions-item label="多图" label-col="12" value-col="12">
			<view slot="value" v-if="model.testMultiImg">
				<u-image
					width="160rpx"
					height="160rpx"
          v-for="(src,index) in (model.testMultiImg || '').split(',')"
          :src="`${$cons.host()}${src}/image`"
          :key="index"/>
			</view>
		</di-descriptions-item>
		<di-descriptions-item label="多文件" label-col="12" value-col="12">
			<view slot="value" v-if="model.testMultiFile">
				<u-image
					width="160rpx"
					height="160rpx"
          v-for="(src,index) in (model.testMultiFile || '').split(',')"
          :src="`${$cons.host()}${src}/image`"
          :key="index"/>
			</view>
		</di-descriptions-item>
		<di-descriptions-item label="单文件" label-col="12" value-col="12">
			<u-image v-if="model.testSingleFile" slot="value" width="160rpx" height="160rpx" :src="`${$cons.host()}${model.testSingleFile}/image`"/>
		</di-descriptions-item>
    <di-descriptions-item label="日期" :value="model.testDate"/>
    <di-descriptions-item label="日期时间" :value="model.testTime"/>
    <di-descriptions-item label="创建时间" :value="model.createTime"/>
    <di-descriptions-item label="多选" :value="model.testMultiSelectLabel"/>
    <di-descriptions-item label="单选" :value="model.testSingleSelectLabel"/>
    <di-descriptions-item label="checkbox" :value="model.testBoxLabel"/>
</di-descriptions>  
</template>

<script>
import detail from '@/mixins/detail'
export default {
  mixins: [detail],
  data () {
    return {
      baseApi: '/mobileTest'
    }
  }
}
</script>
<style lang="sass" scoped>
</style>
