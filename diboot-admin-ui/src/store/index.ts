import piniaPersist from 'pinia-plugin-persist'

const pinia = createPinia()

// 持久化插件
pinia.use(piniaPersist)

export default pinia
