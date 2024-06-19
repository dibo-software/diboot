<script setup lang="ts">
import { VueCropper } from 'vue-cropper'
import 'vue-cropper/dist/index.css'
import useAuthStore from '@/store/auth'
import type { UserModel } from '@/views/org-structure/user/type'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/file'
const updateApi = '/iam/user'

const props = defineProps<{
  showSetAvatarDialog?: boolean
  avatarBase64?: string | undefined
  filename?: string
}>()
const dialogVisible = ref<boolean | undefined>(false)
const imgSrc = ref<string | undefined>('')
const filenameInner = ref<string | undefined>('')
watch(
  () => props.showSetAvatarDialog,
  val => {
    dialogVisible.value = val
  }
)
watch(
  () => props.avatarBase64,
  val => {
    imgSrc.value = val
  }
)
watch(
  () => props.filename,
  val => {
    filenameInner.value = val
  }
)

const emits = defineEmits(['cropDialog'])
const handleClose = () => {
  dialogVisible.value = false
  emits('cropDialog', false)
}

const dataURLtoFile = (dataurl: string, filename: string) => {
  const arr = dataurl.split(',')
  const test = arr[0].match(/:(.*?);/)
  if (test) {
    const mime = test[1]
    const bstr = window.atob(arr[1])
    let n = bstr.length
    const u8arr = new Uint8Array(n)
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n)
    }
    return new File([u8arr], filename, { type: mime })
  }
}

const loading = ref(false)
const authStore = useAuthStore()
const getPickAvatar = () => {
  loading.value = true
  const file = dataURLtoFile(imgSrc.value ?? '', filenameInner.value ?? '') as File
  const formData = new FormData()
  formData.set('file', file)
  api
    .upload<FileRecord>(`${baseApi}/upload`, formData)
    .then(res => {
      if (res.code === 0) {
        const data = ref(_.cloneDeep(authStore.info))
        if (data.value) {
          data.value.avatarUrl = res.data?.accessUrl
        }
        api
          .post<UserModel>(`${updateApi}/update-current-user-info`, data.value)
          .then(re => {
            if (re.code === 0) {
              ElMessage.success(re.msg)
              authStore.getInfo(true)
            }
          })
          .catch(err => ElMessage.error(err.msg || err.message || i18n.t('personal.updateFailed')))
          .finally(() => (loading.value = false))
      }
    })
    .catch(err => ElMessage.error(err.msg || err.message || i18n.t('personal.uploadFailed')))
    .finally(() => (loading.value = false))

  handleClose()
}
</script>

<template>
  <el-dialog v-model="dialogVisible" :before-close="handleClose" :title="$t('personal.avatarSetting')">
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
      <el-button type="primary" :loading="loading" @click="getPickAvatar">{{ $t('button.save') }}</el-button>
    </div>
  </el-dialog>
</template>

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
