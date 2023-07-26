<script setup lang="ts">
import useAuthStore from '@/stores/auth'
import JSEncrypt from 'jsencrypt'

const encryptor = new JSEncrypt()
encryptor.setPublicKey(`MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzPy1UcwzgRT8dRUpAW0H
eyVvIi4icqiwdBZMrh85+tJEZ/AXjELRzl89m2ZKoMHfoMDkajoxJeaL5IV9UpUl
+1RqWvWqgYL0r859FyDeNg9kiMAfApyIowqFqctDx7k77jDopBvcX8F0shl6SUtE
Vu96tc7+FrjP4OGwXJeB+b04O2SCV4mHxs8TRn7YsLoA10mjPNnsX0TiYkzSGUP/
E5OEYt/ixNwO/lC6TdFM9PXRaTjF76e5qHw6ksJU74mb3A9/ZQCb4nzVw15xTxIa
AnDX7+FqnCgpu26yXMLtVXyEa6CUvBjLLBleJ/cyHuUir7GYutf5LyuIEJPEWgnZ
BwIDAQAB`)

const authStore = useAuthStore()
const loading = ref(false)
const PROCESS_USERNAME = 'maql'
const PASSWORD = 'Dibo306!'
const model = reactive({ username: PROCESS_USERNAME, password: PASSWORD, captcha: '', traceId: '' })

const refreshTraceId = () => {
  model.traceId = Math.random().toString(36).slice(-8) + +new Date()
}
refreshTraceId()

const captchaSrc = `${baseURL}/auth/captcha`

const router = useRouter()

const redirect = () => {
  const query = router.currentRoute.value.query
  const redirect = query.redirect
  if (redirect) {
    delete query.redirect
    router.push({ path: '/redirect' + redirect, query })
  } else {
    router.push('/')
  }
}
const onSubmit = () => {
  loading.value = true
  authStore
    .login({ ...model, password: encryptor.encrypt(model.password) })
    .then(() => {
      redirect()
      loading.value = false
    })
    .catch(() => {
      refreshTraceId()
      loading.value = false
    })
}

</script>

<template>
  <div class="content">

    <van-form @submit="onSubmit">
      <h2 style="text-align: center">Diboot 移动端 v3.0</h2>
      <van-cell-group inset>
        <van-field
          v-model="model.username"
          name="username"
          label="用户名"
          placeholder="用户名"
          :rules="[{ required: true, message: '请填写用户名' }]"
        />
        <van-field
          v-model="model.password"
          type="password"
          name="password"
          label="密码"
          placeholder="密码"
          :rules="[{ required: true, message: '请填写密码' }]"
        />
        <van-field
          v-model="model.captcha"
          name="captcha"
          label="验证码"
          placeholder="验证码"
          :rules="[{ required: true, message: '请填写验证码' }]"
        >
          <template #button>
            <img
              :src="`${captchaSrc}?traceId=${model.traceId}`"
              alt="captcha"
              style="height: 30px"
              @click="refreshTraceId"
            />
          </template>
        </van-field>
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit"> 提交 </van-button>
      </div>
    </van-form>
  </div>
</template>
<style scoped lang="scss">
.content {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;

  background-image: radial-gradient(closest-side, rgb(120, 142, 225), rgba(248, 192, 147, 0)),
  radial-gradient(closest-side, rgb(168, 178, 220), rgba(170, 142, 245, 0)),
  radial-gradient(closest-side, rgb(143, 173, 210), rgba(235, 105, 78, 0)),
  radial-gradient(closest-side, rgb(129, 199, 211), rgba(243, 11, 164, 0)),
  radial-gradient(closest-side, rgb(137, 196, 148), rgba(254, 234, 131, 0));
  background-size: 130vmax 130vmax, 80vmax 80vmax, 90vmax 90vmax, 110vmax 110vmax, 90vmax 9vmax;
  background-position: -80vmax -80vmax, 60vmax -30vmax, 10vmax 10vmax, -30vmax -10vmax, 50vmax 50vmax;
  background-repeat: no-repeat;
  animation: 12s movement linear infinite;

  @keyframes movement {
    0%,
    100% {
      background-size: 130vmax 130vmax, 80vmax 80vmax, 90vmax 90vmax, 110vmax 110vmax, 90vmax 90vmax;
      background-position: -80vmax -80vmax, 60vmax -30vmax, 10vmax 10vmax, -30vmax -10vmax, 50vmax 50vmax;
    }

    25% {
      background-size: 100vmax 100vmax, 90vmax 90vmax, 100vmax 100vmax, 90vmax 90vmax, 60vmax 60vmax;
      background-position: -60vmax -90vmax, 50vmax -40vmax, 0vmax -20vmax, -40vmax -20vmax, 40vmax 60vmax;
    }

    50% {
      background-size: 80vmax 80vmax, 110vmax 110vmax, 80vmax 80vmax, 60vmax 60vmax, 80vmax 80vmax;
      background-position: -50vmax -70vmax, 40vmax -30vmax, 10vmax 0vmax, 20vmax 10vmax, 30vmax 70vmax;
    }

    75% {
      background-size: 90vmax 90vmax, 90vmax 90vmax, 100vmax 100vmax, 90vmax 90vmax, 70vmax 70vmax;
      background-position: -50vmax -40vmax, 50vmax -30vmax, 20vmax 0vmax, -10vmax 19vmax, 40vmax 60vmax;
    }
  }
  .van-radio--horizontal {
    margin-right: 0;
  }
  .van-radio__icon {
    display: none;
  }
  .van-radio__label {
    margin-left: 0;
  }
}
</style>
