<script setup lang="ts">
import logo from '@/assets/image/logo.png'
import useAuthStore from '@/stores/auth'
import router from '@/router'
import auth from '@/utils/auth'
import type { OrgModel } from './type'

  const authStore = useAuthStore()
  const positions = ref()
  const orgName = ref()
  if (authStore.info) {
    positions.value = authStore.info.positionList?.map(position => position.name).toString()
    api
      .get<OrgModel>(`/iam/org/${authStore.info.orgId}`)
      .then(res => {
        orgName.value = res.data?.name
      })
      .catch(err => showFailToast(err.msg || err.message || '更新失败！'))
  }

  const login = () => {
    if (authStore.realname) return
    auth.clearToken()
    router.push({ name: 'Login' }).finally()
  }
</script>

<template>
  <van-space class="cover">
    <van-space style='margin-left: 100%' direction="vertical" size='10px' align='center' @click='login'>
      <van-image class='image' round width='8rem' height='8rem' :src="authStore.avatar || logo" />
      <view>{{ authStore.realname || '点击登录' }}</view>
    </van-space>
  </van-space>
  <van-cell-group>
    <van-cell title="工号" size="large" icon="user-o" :value='authStore.info?.userNum' />
    <van-cell title="部门" size="large" icon="cluster-o" :value='orgName' />
    <van-cell title="岗位" size="large" icon="points" :value='positions' />
    <van-cell title="电话" size="large" icon="phone-o" :value='authStore.info?.mobilePhone' />
    <van-cell title="邮箱" size="large" icon="envelop-o" :value='authStore.info?.email' />
  </van-cell-group>
  <van-button type="danger" class='footer' block @click='authStore.logout()'>退出登录</van-button>
</template>
<style scoped lang="scss">
.cover {
  width: 100%;
  height: 40%;
  text-align: center;
  background-color: #7fcfbb;
  color: #ffffff;
}
.footer{
  position: fixed;
  bottom: 10%;
  width: 100%;
}
</style>
