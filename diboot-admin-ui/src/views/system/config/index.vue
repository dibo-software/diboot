<script setup lang="ts" name="SystemConfig">
import type { Directive } from 'vue'
import { checkPermission } from '@/utils/permission'
import { CircleCheck, CircleClose } from '@element-plus/icons-vue'
import type { SystemConfigType, SystemConfig } from './type'

const baseApi = '/systemConfig'
const type = ref('')
const typeList = ref<SystemConfigType[]>([])
const configList = ref<SystemConfig<string | boolean>[]>([])

const editable = checkPermission('update')

api.get<SystemConfigType[]>(`${baseApi}/typeList`).then(res => {
  if (res.data) typeList.value = res.data
  if (typeList.value && typeList.value.length > 0) {
    type.value = typeList.value[0]?.value
    typeList.value.forEach(e => {
      if (e.ext) e.data = {}
    })
  }
})

const getConfigData = () => {
  api.get<SystemConfig[]>(`${baseApi}/${type.value}`).then(res => {
    if (res.data) configList.value = res.data
    configList.value.forEach(e => {
      if (typeof e.defaultValue === 'boolean') e.value = e.value === 'true'
    })
  })
}

// 监听 type 变化，获取相应的配置信息
watch(type, value => value && getConfigData())

// 自动聚焦指令
const vFocus: Directive = el => {
  const child = el.children[0]
  child.children.length ? child.children[0].focus() : child.focus()
}

const edit = (data: SystemConfig) => {
  if (editable) data._edit = true
}

const cancelEdit = (data: SystemConfig) => {
  setTimeout(() => {
    data._edit = false
  }, 300)
}

const update = (data: SystemConfig) => {
  if (data.required && (data.value == null || `${data.value}`.trim().length === 0)) {
    return ElMessage.error('不能为空')
  }
  api
    .post(`${baseApi}`, data)
    .then(res => ElMessage.success('修改成功'))
    .catch(() => ElMessage.error('修改失败，稍后重试！'))
    .finally(getConfigData)
}

const deleteConfig = (prop = '') => {
  ElMessageBox.confirm(
    `重置${prop ? '该配置' : ` ·${typeList.value.find(e => e.value === type.value)?.label}· `}, 是否继续?`,
    '提示',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    api
      .delete(`${baseApi}/${type.value}/${prop}`)
      .then(res => ElMessage.success('重置成功'))
      .catch(() => ElMessage.error('重置失败，稍后重试！'))
      .finally(getConfigData)
  })
}

const configTest = (data: Record<string, string>) => {
  api
    .post(`${baseApi}/${type.value}`, data)
    .then(() => ElMessage.success('测试通过'))
    .catch(err => ElNotification.error({ title: '测试失败', message: err.msg }))
}
</script>

<template>
  <div style="margin: 10px">
    <el-empty v-if="!type" description="无系统配置项" />
    <el-button
      v-if="type && editable"
      size="small"
      type="warning"
      plain
      style="position: absolute; right: 20px; z-index: 99"
      @click="deleteConfig()"
    >
      <i class="el-icon-close" /> 重置
    </el-button>
    <el-tabs v-if="type" v-model="type">
      <el-tab-pane v-for="item in typeList" :key="item.value" :name="item.value" :label="item.label || '(未命名)'">
        <el-card shadow="hover" style="margin: 3vh 10vw">
          <div v-if="editable" style="color: #aaaaaa; text-align: end; zoom: 0.8">双击编辑</div>
          <table>
            <tr v-for="config in configList" :key="config.prop">
              <th style="padding: 15px">
                {{ config.propLabel }}
                <span v-show="config._edit && config.required" style="color: red">*</span>
              </th>
              <td>
                <span v-if="typeof config.defaultValue === 'boolean'" style="margin-left: 10px">
                  <el-switch v-if="editable" v-model="config.value" @change="update(config)" />
                  <el-tag v-else :type="config.value ? 'success' : 'info'">
                    {{ config.value }}
                  </el-tag>
                </span>
                <span v-else-if="config._edit">
                  <el-select
                    v-if="config.options"
                    v-model="config.value"
                    v-focus
                    style="width: calc(100% - 10px); margin-left: 5px"
                    @change="update(config)"
                    @clear="update(config)"
                    @blur="cancelEdit(config)"
                  >
                    <el-option v-for="option in config.options" :key="option" :value="option" />
                  </el-select>
                  <span v-else>
                    <el-input
                      v-model="config.value"
                      v-focus
                      style="width: calc(100% - 80px); margin-left: 5px"
                      @keyup.enter="update(config)"
                      @blur="getConfigData"
                    />
                    <el-icon class="action-icon" color="#00ff00" @mousedown="update(config)"><CircleCheck /></el-icon>
                    <el-icon class="action-icon" color="orange" @mousedown="deleteConfig(config.prop)">
                      <CircleClose />
                    </el-icon>
                  </span>
                </span>
                <span v-else class="content" :style="editable ? { cursor: 'pointer' } : {}" @dblclick="edit(config)">
                  {{ config.value }}
                </span>
              </td>
            </tr>
          </table>
          <el-collapse v-if="item.ext && editable" style="margin-top: 30px">
            <el-collapse-item>
              <template #title>
                <h2 style="color: #42b983">配置测试</h2>
              </template>
              <el-input
                v-for="key in item.ext"
                :key="key"
                v-model="item.data[key]"
                :placeholder="key"
                style="margin-bottom: 10px"
              />
              <div style="margin-bottom: -10px">
                <el-button type="primary" plain @click="configTest(item.data)">测试</el-button>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped lang="scss">
table,
table tr th,
table tr td {
  border: 1px solid #e8e8e8;
}

table {
  border-collapse: collapse;

  tr {
    th {
      background-color: #fafafa;
      width: 220px;
    }

    td {
      text-align: left;
      width: 100vw;
    }
  }
}

.action-icon {
  zoom: 2;
  cursor: pointer;
  vertical-align: middle;
}

.content {
  width: 100%;
  padding: 10px;
  display: inline-block;
}
</style>
