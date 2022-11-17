<template>
  <el-container class="page-user">
    <input id="avatarFile" type="file" hidden @change="selectFile" />
    <crop-avatar
      :show-set-avatar-dialog="showSetAvatarDialog"
      :avatar-base64="avatarBase64"
      :filename="filename"
      @cropDialog="cropDialog"
    />
    <el-aside class="el-aside" width="240px">
      <el-container>
        <el-header style="height: auto; display: block">
          <div class="user-info-top">
            <el-avatar :size="70" :src="authStore.avatar" />
            <div class="setAvatar">
              <el-tooltip content="修改头像">
                <el-icon :size="14">
                  <CameraFilled @click="getFile" />
                </el-icon>
              </el-tooltip>
            </div>
            <h2>{{ authStore.realname }}</h2>
            <p>
              <el-tag effect="dark" round size="large">{{ authStore.roles[0].name }}</el-tag>
            </p>
          </div>
        </el-header>
        <el-main class="nopadding">
          <el-menu class="menu" :default-active="pageKey">
            <el-menu-item v-for="item in menu" :key="item.key" :index="item.key" @click="openPage">
              <!--              <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>-->
              <template #title>
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

<script setup lang="ts">
import { CameraFilled } from '@element-plus/icons-vue'
import useAuthStore from '@/store/auth'
import account from './components/account.vue'
import password from './components/password.vue'
import cropAvatar from './components/cropAvatar.vue'

const authStore: any = useAuthStore()

const menu = shallowRef([
  {
    title: '个人信息',
    key: 'account',
    component: account
  },
  {
    title: '密码',
    key: 'password',
    component: password
  }
])

// 切换页面
const page = shallowRef(account)
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
const avatarBase64: any = ref('')
const showSetAvatarDialog = ref()
const filename = ref('')
const selectFile = (e: any) => {
  const file = e.target.files[0]
  const filesize = file.size
  filename.value = file.name
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = e => {
    const imgcode = e.target?.result
    avatarBase64.value = imgcode
    showSetAvatarDialog.value = true
  }
}
const cropDialog = (val: boolean) => {
  showSetAvatarDialog.value = val
  avatarBase64.value = ''
}
</script>

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
