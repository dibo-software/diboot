import type { FieldRuleValidator } from 'vant'
import type { FieldRule } from 'vant/lib/field/types'
import i18n from '@/i18n'

/**
 * 表单校验属性值
 *
 * @param validateApi 校验接口
 * @param prop 属性
 * @param id 数据ID
 * @param param 额外的参数
 */
export const checkValue = (
  validateApi: string,
  prop: string,
  id: () => string | undefined,
  param?: () => Record<string, unknown>
): FieldRuleValidator => {
  return (value: any, rule: FieldRule) => {
    if (value) {
      const params: Record<string, unknown> = (param && param()) ?? {}
      params.id = id()
      params[prop] = value
      return new Promise(resolve => {
        api
          .get<boolean | undefined>(validateApi, params)
          .then(res => {
            if (res.data !== false) {
              resolve(true)
            } else {
              resolve(i18n.global.t('rules.valueNotUnique', value))
            }
          })
          .catch(err => {
            resolve(err.msg || err)
          })
      })
    } else {
      return true
    }
  }
}
