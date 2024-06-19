<script setup lang="ts">
import { UserFilled } from '@element-plus/icons-vue'
import useAuthStore from '@/store/auth'
import Account from './components/Account.vue'
import Password from './components/Password.vue'
import CropAvatar from './components/CropAvatar.vue'
import type { Role } from '@/views/system/role/type'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
useAuthStore().getInfo(true)
const authStore = useAuthStore()

const tagNameList = ref()
const roles = authStore.roles
tagNameList.value = roles.map((role: Role) => role.name)

const menu = shallowRef([
  {
    title: i18n.t('personal.info'),
    key: 'account',
    icon: 'Postcard',
    component: Account
  },
  {
    title: i18n.t('user.password'),
    key: 'password',
    icon: 'Lock',
    component: Password
  }
])

// 切换页面
const page = shallowRef(Account)
const pageKey = ref('account')
const openPage = (item: any) => {
  pageKey.value = item.index
  page.value = menu.value.filter(m => m.key === item.index)[0].component
}

//修改头像
const getFile = () => {
  const fileEle = document.getElementById('avatarFile')
  fileEle?.click()
}
const avatarBase64 = ref()
const showSetAvatarDialog = ref()
const filename = ref('')
const selectFile = (e: any) => {
  const filepath = e.target.value
  const file = e.target.files[0]
  const fileTypes = ['.jpg', '.jpeg', '.png', '.gif', '.bmp']
  const fileEnd = filepath.substring(filepath.lastIndexOf('.'))
  if (fileTypes.indexOf(fileEnd) > -1) {
    filename.value = file.name
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = e => {
      avatarBase64.value = e.target?.result
      showSetAvatarDialog.value = true
    }
  } else {
    ElMessage.error(i18n.t('personal.uploadImg'))
  }
}
const cropDialog = (val: boolean) => {
  showSetAvatarDialog.value = val
  avatarBase64.value = ''
}
</script>

<template>
  <el-container class="page-user">
    <input id="avatarFile" type="file" hidden accept=".jpg,.jpeg,.png,.gif,.bmp" @change="selectFile" />
    <crop-avatar
      :show-set-avatar-dialog="showSetAvatarDialog"
      :avatar-base64="avatarBase64"
      :filename="filename"
      @crop-dialog="cropDialog"
    />
    <el-aside class="el-aside" width="240px">
      <el-container>
        <el-header style="height: auto; display: block">
          <div class="user-info-top">
            <el-tooltip :content="$t('personal.modifyAvatar')">
              <el-avatar :size="70" :icon="UserFilled" :src="authStore.avatar" @click="getFile" />
            </el-tooltip>

            <h2>{{ authStore.realname }}</h2>
            <span v-for="(tagName, index) in tagNameList" :key="index">
              <el-tag effect="dark" round size="large" style="margin: 0 5px 5px 0">{{ tagNameList[index] }}</el-tag>
            </span>
          </div>
        </el-header>
        <el-main class="nopadding">
          <el-menu class="menu" :default-active="pageKey">
            <el-menu-item v-for="item in menu" :key="item.key" :index="item.key" @click="openPage">
              <!--              <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>-->
              <template #title>
                <el-icon v-if="item.icon">
                  <icon :name="item.icon" />
                </el-icon>
                <span>{{ item.title }}</span>
              </template>
            </el-menu-item>
          </el-menu>
        </el-main>
      </el-container>
    </el-aside>
    <el-main>
      <Suspense>
        <template #default>
          <component :is="page" />
        </template>
      </Suspense>
    </el-main>
  </el-container>
</template>

<style scoped lang="scss">
.page-user {
  height: 100%;
  .user-info-top {
    text-align: center;
  }
  .user-info-top h2 {
    font-size: 18px;
    margin-top: 5px;
  }
  .user-info-top p {
    margin: 8px 0 10px 0;
  }
  .menu {
    background: none;
    border-right: none;
  }
  .menu .el-menu-item {
    font-size: 14px;
    --el-menu-item-height: 50px;
  }
  .el-header {
    height: auto;
    padding: 10px;
    border-bottom: 1px solid var(--el-border-color);
  }
  .el-aside {
    box-sizing: border-box;
    border-right: solid 1px var(--el-menu-border-color);
  }
}
.setAvatar {
  position: relative;
  top: -14px;
  font-size: 14px;
  color: #000;
  margin: -10px;
  cursor: pointer;
}
</style>
