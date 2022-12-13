<script setup lang="ts">
const props = defineProps<{
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
  watermarkDiv.setAttribute('class', 'watermarkDiv')
  const styleStr = `position:absolute;top:0;left:0;right:0;bottom:0;z-index:99;pointer-events:none;background-repeat:repeat;
  background-position:${width / 2}px ${height / 2}px, 0 0;background-image:url('${dataUrl}'),url('${dataUrl}');`
  watermarkDiv.setAttribute('style', styleStr)
  watermark.value.appendChild(watermarkDiv)
}

// 清空dom
const clear = () => {
  const wmDom = watermark.value.querySelector('.watermarkDiv')
  wmDom && wmDom.remove()
}

// 创建水印
const create = () => {
  clear()
  //创建画板
  const canvas = document.createElement('canvas')
  canvas.width = props.width || 300
  canvas.height = props.height || 300
  canvas.style.display = 'none'
  //绘制图片/文字
  const ctx = canvas.getContext('2d')
  if (!ctx) return ElMessage?.warning('浏览器不支持水印功能')
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
    ctx.font = props.font || 'bold 20px Microsoft YaHei'
    ctx.textAlign = 'center'
    ctx.fillText(props.text, 0, 0)
    createWatermarkContainer(canvas.width, canvas.height, canvas.toDataURL('image/png'))
  }
}

defineExpose({ create, clear })

onMounted(() => {
  create()
})
</script>

<template>
  <div ref="watermark" class="water-mark">
    <slot />
  </div>
</template>

<style scoped lang="scss">
.water-mark {
  position: relative;
  display: inherit;
  width: 100%;
  height: 100%;
}
</style>
