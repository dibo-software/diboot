<script setup lang="ts">
import logo from '@/assets/image/logo.png'
import useAuthStore from '@/stores/auth'
import router from '@/router'
import auth from '@/utils/auth'
import type { OrgModel } from './type'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
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
    .catch(err => showFailToast(err.msg || err.message || i18n.t('mine.updateFailed')))
}

const login = () => {
  if (authStore.realname) return
  auth.clearToken()
  router.push({ name: 'Login' }).finally()
}
</script>

<template>
  <div class="mine">
    <van-space class="cover">
      <van-space style="margin-left: 100%" direction="vertical" size="10px" align="center" @click="login">
        <van-image class="image" round width="8rem" height="8rem" :src="authStore.avatar || logo" />
        <view>{{ authStore.realname || $t('mine.login') }}</view>
      </van-space>
    </van-space>
    <van-cell-group>
      <van-cell :title="$t('mine.userNum')" size="large" icon="user-o" :value="authStore.info?.userNum" />
      <van-cell :title="$t('mine.orgName')" size="large" icon="cluster-o" :value="orgName" />
      <van-cell :title="$t('mine.position')" size="large" icon="points" :value="positions" />
      <van-cell :title="$t('mine.mobilePhone')" size="large" icon="phone-o" :value="authStore.info?.mobilePhone" />
      <van-cell :title="$t('mine.email')" size="large" icon="envelop-o" :value="authStore.info?.email" />
    </van-cell-group>
    <van-button type="danger" class="footer" block @click="authStore.logout()">{{ $t('mine.logout') }}</van-button>
  </div>
</template>
<style scoped lang="scss">
.mine {
  display: flex;
  flex-direction: column;
}
.cover {
  width: 100%;
  height: 280px;
  text-align: center;
  background-color: #7fcfbb;
  color: #ffffff;
}
.footer {
  margin-top: 20px;
}
</style>
