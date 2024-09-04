<script setup lang="ts">
import Player from 'xgplayer'
import 'xgplayer/dist/index.min.css'

const props = defineProps<{
  // string: 文件链接
  value?: string
}>()

let player: Player | undefined

const fileName = ref<string>('')
const objectURL = ref<string>('')
const loading = ref(false)

const revoke = () => objectURL.value && URL.revokeObjectURL(objectURL.value)

const init = async (value: string | Blob | ArrayBuffer) => {
  if (typeof value === 'string') {
    loading.value = true
    await api
      .download(value)
      .then(res => {
        revoke()
        fileName.value = res.filename
        objectURL.value = URL.createObjectURL(new Blob([res.data]))
      })
      .catch(err => ElMessage.error(err.msg || err.message))
      .finally(() => (loading.value = false))
  } else {
    revoke()
    objectURL.value = URL.createObjectURL(value instanceof Blob ? value : new Blob([value]))
  }
}

watch(
  () => props.value,
  async value => {
    if (value) {
      await init(value)
      player = new Player({ id: 'player', url: objectURL.value, width: '100%' })
    } else {
      revoke()
      player?.pause()
      player?.destroy()
      player = void 0
    }
  },
  { immediate: true }
)

defineExpose({
  stop: () => player?.pause()
})
</script>

<template>
  <div id="player">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <el-skeleton-item v-loading="loading" variant="rect" style="width: 100%; height: 300px" />
      </template>
    </el-skeleton>
  </div>
</template>

<style scoped lang="scss">
#player {
  flex: auto;
}
</style>
