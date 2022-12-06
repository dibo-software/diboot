<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue'
import type { Job, ScheduleJob } from './type'

const baseApi = '/schedule-job'

const { loadData, loading, model } = useDetail<ScheduleJob>(baseApi)

const title = ref('')

const visible = ref(false)

const jobList = ref<Array<Job>>()

const { relatedData, initRelatedData } = useOption({ dict: 'INIT_STRATEGY' })
initRelatedData()

defineExpose({
  open: (id?: string) => {
    loadData(id)
    if (!jobList.value) api.get<Array<Job>>(`${baseApi}/all-job`).then(res => (jobList.value = res.data))
    if (id) {
      title.value = '更新'
    } else {
      title.value = '新建'
      model.value.jobStatus = true
      model.value.saveLog = true
      if (relatedData.initStrategyOptions) model.value.initStrategy = relatedData.initStrategyOptions[0].value
    }
    visible.value = true
  }
})

// 表单
const formRef = ref<FormInstance>()

watch(visible, value => {
  if (!value) formRef.value?.resetFields()
})

const emit = defineEmits<{
  (e: 'complete', id?: string): void
}>()

const { submitting, submit } = useForm({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})

const rules: FormRules = {
  jobKey: { required: true, message: '不能为空', whitespace: true },
  jobName: { required: true, message: '不能为空', whitespace: true },
  cron: { required: true, message: '不能为空', whitespace: true }
}

const jobChange = (jobKey: string) => {
  const job = jobList.value?.find((e: Job) => e.jobKey === jobKey) ?? ({} as Partial<Job>)
  model.value.jobName = job.jobName
  model.value.cron = job.jobCron
  model.value.paramJson = job.paramJsonExample
}
</script>

<template>
  <el-dialog v-model="visible" :title="title" top="10vh" width="65vw">
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="110px">
      <el-form-item prop="jobKey" label="定时任务">
        <el-select v-model="model.jobKey" @change="jobChange">
          <el-option
            v-for="(item, index) in jobList"
            :key="index"
            :value="item.jobKey"
            :label="item.jobKey + (item.jobName.length > 0 ? `（${item.jobName}）` : '')"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="jobName" label="任务名称">
        <el-input v-model="model.jobName" />
      </el-form-item>
      <el-form-item prop="cron" label="定时表达式">
        <el-input v-model="model.cron">
          <template #prefix>
            <el-tooltip class="item" effect="dark" placement="right-start">
              <template #content>
                格式： * * 1 * * ?<br />
                含义: 秒 分 时 日 月 星期 年
              </template>
              <el-icon style="cursor: pointer" :size="20">
                <question-filled />
              </el-icon>
            </el-tooltip>
          </template>
          <template #append>
            <el-link type="primary" href="http://cron.ciding.cc/" target="_blank"> 在线编辑器</el-link>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="initStrategy" label="初始化策略">
        <el-select v-model="model.initStrategy">
          <el-option
            v-for="(item, index) in relatedData.initStrategyOptions"
            :key="index"
            :value="item.value"
            :label="item.label"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="paramJson" label="参数">
        <el-input v-model="model.paramJson" type="textarea" />
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item prop="jobStatus" label="状态">
            <el-radio-group v-model="model.jobStatus" size="small">
              <el-radio-button :label="true">启用</el-radio-button>
              <el-radio-button :label="false">停用</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="saveLog" label="日志">
            <el-radio-group v-model="model.saveLog" size="small">
              <el-radio-button :label="true">启用</el-radio-button>
              <el-radio-button :label="false">停用</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item prop="jobComment" label="备注">
        <el-input v-model="model.jobComment" type="textarea" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit(model, formRef)">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
