<script setup lang="ts">
import { Html5Qrcode } from 'html5-qrcode'
import type { QrDimensions } from 'html5-qrcode/src/core'

const props = withDefaults(
  defineProps<{
    position?: 'top' | 'bottom' | 'right' | 'left' | 'center'
    squareLength?: number
    aspectRatio?: number
  }>(),
  {
    position: 'top'
  }
)

const show = ref(false)

const emit = defineEmits<{
  (e: 'change', value?: unknown): void
}>()

let html5QrCode: Html5Qrcode

const getCameras = async () => {
  if (!html5QrCode)
    await Html5Qrcode.getCameras()
      .then(devices => {
        if (devices && devices.length) {
          html5QrCode = new Html5Qrcode('reader')
        }
      })
      .catch((err: any) => {
        // handle err
        html5QrCode = new Html5Qrcode('reader')
        console.error('您需要授予相机访问权限')
      })

  html5QrCode
    .start(
      // environment后置摄像头 user前置摄像头
      { facingMode: 'environment' },
      {
        fps: 2, // 可选，每秒帧扫描二维码
        qrbox:
          (props.squareLength || 0) > 0
            ? ({ width: props.squareLength, height: props.squareLength } as QrDimensions)
            : void 0, // 可选，如果你想要有界框UI
        aspectRatio: props.aspectRatio // 可选，视频馈送需要的纵横比，(4:3--1.333334, 16:9--1.777778, 1:1--1.0)传递错误的纵横比会导致视频不显示
      },
      (decodedText: string) => {
        // do something when code is read
        // console.log('decodedText', decodedText)
        emit('change', decodedText)
        // emit('change', decodedText)
        show.value = false
      },
      void 0
    )
    .then(() => void 0)
    .catch((err: any) => {
      console.log('扫码错误信息', err)
      // 错误信息处理仅供参考，具体情况看输出！！！
      if (typeof err == 'string') {
        // this.$toast(err)
      } else {
        // if (err.name == 'NotAllowedError') return this.$toast("您需要授予相机访问权限")
        // if (err.name == 'NotFoundError') return this.$toast('这个设备上没有摄像头')
        // if (err.name == 'NotSupportedError') return this.$toast('摄像头访问只支持在安全的上下文中，如https或localhost')
        // if (err.name == 'NotReadableError') return this.$toast('相机被占用')
        // if (err.name == 'OverconstrainedError') return this.$toast('安装摄像头不合适')
        // if (err.name == 'StreamApiNotSupportedError') return this.$toast('此浏览器不支持流API')
      }
    })
}

const open = () => (show.value = true)

defineExpose({ open })
</script>

<template>
  <slot v-bind="{ open }">
    <van-icon name="scan" @click="open" />
  </slot>
  <van-popup
    v-model:show="show"
    closeable
    :position="position"
    :lazy-render="false"
    @opened="getCameras"
    @close="html5QrCode?.stop()"
  >
    <div id="reader"></div>
  </van-popup>
</template>
