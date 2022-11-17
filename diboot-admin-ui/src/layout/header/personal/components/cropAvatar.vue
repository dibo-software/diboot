<template>
  <el-dialog v-model="dialogVisible" :before-close="handleClose" title="头像设置">
    <div class="cropperBox">
      <vue-cropper
        ref="cropper"
        :can-move-box="false"
        :img="imgSrc"
        :auto-crop="true"
        auto-crop-width="200"
        auto-crop-height="200"
        output-type="png"
      />
    </div>
    <div class="optionBtn">
      <el-button type="primary" @click="getPickAvatar"><i class="fa fa-save" />保存</el-button>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { VueCropper } from 'vue-cropper'
import 'vue-cropper/dist/index.css'
import { reactive } from 'vue'
import useAuthStore from '@/store/auth'

const baseApi = '/file'
const updateApi = '/iam/user'

const props = defineProps<{
  showSetAvatarDialog?: boolean
  avatarBase64?: string
  filename?: string
}>()
const dialogVisible = ref(false)
const imgSrc = ref('')
const filename = ref('')
watch(
  () => [props.showSetAvatarDialog, props.avatarBase64, props.filename],
  val => {
    // console.log(val, 'vallll')
    dialogVisible.value = val[0]
    imgSrc.value = val[1]
    filename.value = val[2]
  }
)

const emits = defineEmits(['cropDialog'])
const handleClose = () => {
  dialogVisible.value = false
  emits('cropDialog', false)
}

const dataURLtoFile = (dataurl, filename) => {
  const arr = dataurl.split(',')
  const mime = arr[0].match(/:(.*?);/)[1]
  const bstr = window.atob(arr[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  return new File([u8arr], filename, { type: mime })
}

const authStore = useAuthStore()
const getPickAvatar = () => {
  const file = dataURLtoFile(imgSrc.value, filename.value)
  const formData = new FormData()
  formData.set('file', file)
  api.upload<FileRecord>(`${baseApi}/upload`, formData).then(res => {
    if (res.code === 0) {
      const data = reactive(authStore.info)
      data.avatarUrl = res.data.accessUrl
      api.post<string>(`${updateApi}/update-current-user-info`, data).then(re => {
        if (re.code === 0) {
          ElMessage.success(re.msg)
          authStore.getNewInfo()
        }
      })
    }
  })
  handleClose()
}
</script>

<style scoped>
.cropperBox {
  width: 100%;
  height: 300px;
}
.optionBtn {
  font-weight: bold;
  text-align: center;
  margin-top: 10px;
}
</style>
