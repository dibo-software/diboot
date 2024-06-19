<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue'
import type { Job, ScheduleJob } from './type'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
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
      title.value = i18n.t('title.update')
    } else {
      title.value = i18n.t('title.create')
      model.value.jobStatus = 'A'
      model.value.saveLog = true
      if (relatedData.initStrategyOptions?.length) model.value.initStrategy = relatedData.initStrategyOptions[0].value
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
  jobKey: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  jobName: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  cron: { required: true, message: i18n.t('rules.notnull'), whitespace: true }
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
    <el-form
      ref="formRef"
      v-loading="loading"
      :model="model"
      :rules="rules"
      :label-width="$i18n.locale === 'en' ? '150px' : '110px'"
    >
      <el-form-item prop="jobKey" :label="$t('scheduleJob.jobKey')">
        <el-select v-model="model.jobKey" @change="jobChange">
          <el-option
            v-for="(item, index) in jobList"
            :key="index"
            :value="item.jobKey"
            :label="item.jobKey + (item.jobName.length > 0 ? `（${item.jobName}）` : '')"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="jobName" :label="$t('scheduleJob.jobName')">
        <el-input v-model="model.jobName" />
      </el-form-item>
      <el-form-item prop="cron" :label="$t('scheduleJob.cron')">
        <el-input v-model="model.cron">
          <template #prefix>
            <el-tooltip class="item" effect="dark" placement="right-start">
              <template #content>
                {{ $t('scheduleJob.cronExplain.grammar') }}<br />
                {{ $t('scheduleJob.cronExplain.implication') }}
              </template>
              <el-icon style="cursor: pointer" :size="20">
                <question-filled />
              </el-icon>
            </el-tooltip>
          </template>
          <template #append>
            <el-link type="primary" href="https://www.pppet.net" target="_blank">
              {{ $t('scheduleJob.onlineEditor') }}
            </el-link>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="initStrategy" :label="$t('scheduleJob.initStrategy')">
        <el-select v-model="model.initStrategy">
          <el-option
            v-for="(item, index) in relatedData.initStrategyOptions"
            :key="index"
            :value="item.value"
            :label="item.label"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="paramJson" :label="$t('scheduleJob.paramJson')">
        <el-input v-model="model.paramJson" type="textarea" />
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item prop="jobStatus" :label="$t('scheduleJob.jobStatus')">
            <el-radio-group v-model="model.jobStatus" size="small">
              <el-radio-button label="A">{{ $t('scheduleJob.open') }}</el-radio-button>
              <el-radio-button label="I">{{ $t('scheduleJob.close') }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="saveLog" :label="$t('scheduleJob.saveLog')">
            <el-radio-group v-model="model.saveLog" size="small">
              <el-radio-button :label="true">{{ $t('scheduleJob.open') }}</el-radio-button>
              <el-radio-button :label="false">{{ $t('scheduleJob.close') }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item prop="jobComment" :label="$t('scheduleJob.jobComment')">
        <el-input v-model="model.jobComment" type="textarea" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
      <el-button type="primary" :loading="submitting" @click="submit(model, formRef)">{{
        $t('button.save')
      }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
