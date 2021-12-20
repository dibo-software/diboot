<template>
<di-descriptions>  
    <di-descriptions-item label="单行" :value="model.testInput"/>
    <di-descriptions-item label="多行" :value="model.testTextarea"/>
    <di-descriptions-item label="富文本" label-col="12" value-col="12">
			<u-parse slot="value" :html="model.testRichtext" :use-cache="true" :selectable="true" :lazy-load="true"/>
		</di-descriptions-item>
		<di-descriptions-item label="单文件" label-col="12" value-col="12">
			<u-image slot="value" width="160rpx" height="160rpx" :src="`${$cons.host()}${model.testSingleFile}/image`"/>
		</di-descriptions-item>
		<di-descriptions-item label="多文件" label-col="12" value-col="12">
			<view slot="value" v-if="model.testMultiFile">
				<u-image
					width="160rpx"
					height="160rpx"
          v-for="(src,index) in (model.testMultiFile || '').split(',')"
          :src="`${$cons.host()}${model.testMultiFile}/image`"
          :key="index"/>
			</view>
		</di-descriptions-item>
		<di-descriptions-item label="单图" label-col="12" value-col="12">
			<u-image slot="value" width="160rpx" height="160rpx" :src="`${$cons.host()}${model.testSingleImg}/image`"/>
		</di-descriptions-item>
		<di-descriptions-item label="多图" label-col="12" value-col="12">
			<view slot="value" v-if="model.testMultiImg">
				<u-image
					width="160rpx"
					height="160rpx"
          v-for="(src,index) in (model.testMultiImg || '').split(',')"
          :src="`${$cons.host()}${model.testMultiImg}/image`"
          :key="index"/>
			</view>
		</di-descriptions-item>
    <di-descriptions-item label="多选select" :value="model.testMultiSelect"/>
    <di-descriptions-item label="树结构" :value="model.testTreeSelect"/>
    <di-descriptions-item label="忽略不显示" :value="model.testIgnore"/>
    <di-descriptions-item label="创建时间" :value="model.createTime"/>
    <di-descriptions-item label="单选select" :value="model.testSingleSelectLabel"/>
    <di-descriptions-item label="checkbox" :value="model.testCheckboxLabel"/>
</di-descriptions>  
</template>

<script>
import detail from '@/mixins/detail'
export default {
  mixins: [detail],
  data () {
    return {
      baseApi: '/testAllField'
    }
  }
}
</script>
<style lang="sass" scoped>
</style>
