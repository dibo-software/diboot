<script setup lang="ts">
import logo from '@/assets/image/logo.png'
import useAuthStore from '@/stores/auth'
import router from '@/router'
import auth from '@/utils/auth'

const authStore = useAuthStore()

const { curPosition, positions } = storeToRefs(authStore)


const login = () => {
  if (authStore.realname) return
  auth.clearToken()
  router.push({ name: 'Login' }).finally()
}
const hasMorePosition = computed(() => positions?.value?.length > 1)
const show = ref(false);
const showPopup = () => {
  if (!hasMorePosition.value) return
  show.value = true;
}


const switchPosition = async (selectedPosition: LabelValue) => {
  // 如果当前切换的值 等于 当前岗位 不做处理
  if (
      `${selectedPosition.value}${selectedPosition?.ext?.orgId || ''}` ===
      `${curPosition?.value?.value}${curPosition?.value?.ext?.orgId || ''}`
  ) return
  // 执行岗位切换
  await authStore.switchPosition(selectedPosition)
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
      <van-cell :title="$t('mine.orgName')" size="large" icon="cluster-o" :value="authStore.info?.orgIdLabel" />
      <van-cell v-if="hasMorePosition" is-link size="large" icon="points" :value="$t('mine.switchPosition')"  @click="showPopup">
        <template #title>
          <span class="custom-title">{{$t('mine.position')}}</span>
          <van-badge :dot="`${position.value}${position.ext?.orgId}` === `${curPosition?.value}${curPosition?.ext?.orgId}`"  v-for="(position, idx) in positions" :key="idx" :offset="[-5, 5]">
            <van-tag class="custom-tag" type="primary" :plain="position.value !== curPosition?.value">
              {{ position?.label }}{{ position?.ext?.orgName ? `(${position?.ext?.orgName})` : '' }}
            </van-tag>
          </van-badge>
        </template>
      </van-cell>
      <van-cell v-else :title="$t('mine.position')" size="large" icon="points" :value="curPosition?.label" />

      <van-cell :title="$t('mine.mobilePhone')" size="large" icon="phone-o" :value="authStore.info?.mobilePhone" />
      <van-cell :title="$t('mine.email')" size="large" icon="envelop-o" :value="authStore.info?.email" />
    </van-cell-group>
    <van-button type="danger" class="footer" block @click="authStore.logout()">{{ $t('mine.logout') }}</van-button>

    <van-popup v-model:show="show"   position="bottom" :style="{ padding: '5px 0' }">
      <van-cell-group>
        <van-cell v-for="(position, idx) in positions" :key="idx" @click="switchPosition(position)">
          <template #title>
            <van-badge :dot="`${position.value}${position.ext?.orgId}` === `${curPosition?.value}${curPosition?.ext?.orgId}`">
              {{ position?.label }}{{ position?.ext?.orgName ? `(${position?.ext?.orgName})` : '' }}
            </van-badge>
          </template>
        </van-cell>
      </van-cell-group>
    </van-popup>
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
.custom-title {
  margin-right: 5px;
}
.custom-tag {
  margin-right: 2px;
}
</style>
