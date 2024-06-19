<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import useAuthStore from '@/store/auth'
import JSEncrypt from 'jsencrypt'
import useSso from '@/hooks/use-sso'
import auth from '@/utils/auth'
import i18nStore from '@/utils/i18n'
import { useI18n } from 'vue-i18n'
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
const formRef = ref<FormInstance>()
const form = reactive({ username: 'admin', password: '123456', captcha: '', traceId: '', tenantCode: '' })
const i18n = useI18n()
const rules = {
  username: { required: true, message: i18n.t('rules.notnull'), trigger: 'blur' },
  password: { required: true, message: i18n.t('rules.notnull'), trigger: 'blur' }
  // captcha: { required: true, message: '不能为空', trigger: 'blur' }
}

const refreshTraceId = () => {
  form.traceId = Math.random().toString(36).slice(-8) + +new Date()
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

const submitForm = async () => {
  await formRef.value?.validate(valid => {
    if (valid) {
      loading.value = true
      authStore
        .login({ ...form, password: encryptor.encrypt(form.password) })
        .then(() => {
          redirect()
          loading.value = false
        })
        .catch(() => {
          refreshTraceId()
          loading.value = false
        })
    }
  })
}
const enableTenant = import.meta.env.VITE_APP_ENABLE_TENANT === 'true'

const enableI18n = import.meta.env.VITE_APP_ENABLE_I18N === 'true'

// 单点登录集成
const { authorizeInfoMap, ssoLoading, initSsoParams, redirectTo } = useSso({
  callback: (token: string) => {
    auth.setToken(token)
    ElMessage.success(i18n.t('login.success'))
    redirect()
  }
})

onMounted(() => {
  initSsoParams()
})
</script>

<template>
  <div v-loading="ssoLoading" :element-loading-text="i18n.t('login.loading')" class="content">
    <div class="form">
      <h1 style="text-align: center">Diboot Admin UI</h1>
      <div v-if="enableI18n" style="text-align: right; margin-bottom: 5px">
        <el-dropdown
          @command="
            (command: string) => {
              i18nStore.set(command)
              $i18n.locale = command
            }
          "
        >
          <div class="item">
            <el-icon :size="22">
              <icon name="Local:Language" />
            </el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="item in $i18n.availableLocales"
                :key="item"
                :command="item"
                :disabled="$i18n.locale === item"
              >
                {{ $t('language', {}, { locale: item }) }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large">
        <el-form-item
          v-if="enableTenant"
          prop="tenantCode"
          label=""
          :rules="{ required: true, message: i18n.t('rules.notnull'), trigger: 'blur' }"
        >
          <el-input v-model="form.tenantCode">
            <template #prefix>
              <span style="width: 85px">{{ $t('login.tenantCode') }}</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="username" label="">
          <el-input v-model="form.username">
            <template #prefix>
              <span style="width: 85px">{{ $t('login.username') }}</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" show-password>
            <template #prefix>
              <span style="width: 85px">{{ $t('login.password') }}</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="captcha">
          <el-input v-model="form.captcha" @keyup.enter="submitForm">
            <template #prefix>
              <span style="width: 85px">{{ $t('login.captcha') }}</span>
            </template>
            <template #suffix>
              <img
                :src="`${captchaSrc}?traceId=${form.traceId}`"
                alt="captcha"
                style="height: 38px"
                @click="refreshTraceId"
              />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button style="width: 100%" type="primary" :loading="loading" @click="submitForm">{{
            $t('login.submit')
          }}</el-button>
        </el-form-item>
        <el-form-item v-if="authorizeInfoMap['CAS_SERVER']">
          <el-button style="width: 100%" type="primary" @click="redirectTo('CAS_SERVER')">{{
            $t('login.cas')
          }}</el-button>
        </el-form-item>
        <el-form-item v-if="authorizeInfoMap['OAuth2']">
          <el-button style="width: 100%" type="primary" @click="redirectTo('OAuth2')">{{
            $t('login.oauth2')
          }}</el-button>
        </el-form-item>
      </el-form>
    </div>
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
  background-size:
    130vmax 130vmax,
    80vmax 80vmax,
    90vmax 90vmax,
    110vmax 110vmax,
    90vmax 9vmax;
  background-position:
    -80vmax -80vmax,
    60vmax - 30vmax,
    10vmax 10vmax,
    -30vmax - 10vmax,
    50vmax 50vmax;
  background-repeat: no-repeat;
  animation: 12s movement linear infinite;

  @keyframes movement {
    0%,
    100% {
      background-size:
        130vmax 130vmax,
        80vmax 80vmax,
        90vmax 90vmax,
        110vmax 110vmax,
        90vmax 90vmax;
      background-position:
        -80vmax - 80vmax,
        60vmax -30vmax,
        10vmax 10vmax,
        -30vmax -10vmax,
        50vmax 50vmax;
    }

    25% {
      background-size:
        100vmax 100vmax,
        90vmax 90vmax,
        100vmax 100vmax,
        90vmax 90vmax,
        60vmax 60vmax;
      background-position:
        -60vmax -90vmax,
        50vmax -40vmax,
        0vmax -20vmax,
        -40vmax -20vmax,
        40vmax 60vmax;
    }

    50% {
      background-size:
        80vmax 80vmax,
        110vmax 110vmax,
        80vmax 80vmax,
        60vmax 60vmax,
        80vmax 80vmax;
      background-position:
        -50vmax -70vmax,
        40vmax -30vmax,
        10vmax 0vmax,
        20vmax 10vmax,
        30vmax 70vmax;
    }

    75% {
      background-size:
        90vmax 90vmax,
        90vmax 90vmax,
        100vmax 100vmax,
        90vmax 90vmax,
        70vmax 70vmax;
      background-position:
        -50vmax - 40vmax,
        50vmax - 30vmax,
        20vmax 0vmax,
        -10vmax 19vmax,
        40vmax 60vmax;
    }
  }

  .form {
    width: 330px;
    padding: 20px;
    border-radius: 16px;
    background: var(--el-color-primary-light-9);
    backdrop-filter: blur(10px);
    box-shadow: 3px 3px 6px rgb(100, 100, 100, 0.1);
  }
}
</style>
