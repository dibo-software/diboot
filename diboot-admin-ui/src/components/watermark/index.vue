<script setup lang="ts">
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const props = defineProps<{
  // 是否禁用
  disable?: boolean
  // 水印文本
  text?: string
  // 文字字体
  font?: string
  // 文字颜色
  color?: string
  // 水印图片链接
  imageUrl?: string
  // 图片不透明度
  imageOpacity?: number
  // 图片宽度
  imageWidth?: number
  // 图片高度
  imageHeight?: number
  // 水印宽度
  width?: number
  // 水印高度
  height?: number
  // 水印旋转角度
  rotate?: number
}>()

const watermark = ref()

//创建水印容器
const createWatermarkContainer = (width: number, height: number, dataUrl: string) => {
  const watermarkDiv = document.createElement('div')
  watermarkDiv.className = 'water-mark-div'
  watermarkDiv.style.position = 'absolute'
  watermarkDiv.style.inset = '0'
  watermarkDiv.style.zIndex = '1000'
  watermarkDiv.style.pointerEvents = 'none'
  watermarkDiv.style.backgroundPosition = `${width / 2}px ${height / 2}px, 0 0`
  watermarkDiv.style.backgroundImage = `url('${dataUrl}'),url('${dataUrl}')`
  watermark.value.appendChild(watermarkDiv)
}

// 清空dom
const clear = () => {
  const wmDom = watermark.value.querySelector('.water-mark-div')
  wmDom && wmDom.remove()
}

// 创建水印
const create = () => {
  clear()
  //创建画板
  const canvas = document.createElement('canvas')
  canvas.width = props.width || 300
  canvas.height = props.height || 300
  //绘制图片/文字
  const ctx = canvas.getContext('2d')
  if (!ctx) return ElMessage?.warning(i18n.t('components.watermark'))
  ctx.translate(canvas.width / 2, canvas.height / 2)
  // 逆时针旋转45度
  ctx.rotate(((props.rotate ? props.rotate : 0) * Math.PI) / 180)
  if (props.imageUrl) {
    const img = new Image()
    img.crossOrigin = 'anonymous'
    img.referrerPolicy = 'no-referrer'
    img.src = props.imageUrl
    //浏览器加载图片完毕后再绘制图片
    img.onload = () => {
      ctx.globalAlpha = props.imageOpacity || 0.3
      ctx.drawImage(
        //规定要使用的图像、画布或视频。
        img,
        //开始剪切的坐标位置。
        -canvas.width / 2,
        -canvas.height / 2,
        // 图片宽高
        props.imageWidth || img.width,
        props.imageHeight || img.height
      )
      createWatermarkContainer(canvas.width, canvas.height, canvas.toDataURL('image/png'))
    }
  } else if (props.text) {
    ctx.fillStyle = props.color || 'rgba(128,128,128,0.2)'
    ctx.font = props.font || 'lighter 20px Microsoft YaHei'
    ctx.textAlign = 'center'
    ctx.fillText(props.text, 0, 0)
    createWatermarkContainer(canvas.width, canvas.height, canvas.toDataURL('image/png'))
  }
}

watch(
  () => !props.disable,
  value => {
    nextTick(() => {
      if (value) create()
      else clear()
    })
  },
  { deep: true, immediate: true }
)

defineExpose({ create, clear })
</script>

<template>
  <div ref="watermark" class="water-mark">
    <slot />
  </div>
</template>

<style scoped lang="scss">
.water-mark {
  position: relative;
}
</style>
