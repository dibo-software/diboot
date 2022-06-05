import { OrgModel } from '@/views/orgUser/org/type'

interface OrgTreeOption {
  async?: boolean
}
export default (option: OrgTreeOption) => {
  const data = ref<OrgModel[]>([])
  const loadTree = async () => {
    const res = await api.get<OrgModel[]>('/org/tree')
    if (res.code === 0 && res.data !== undefined) {
      data.value = res.data
    }
  }
  return {
    data,
    loadTree
  }
}
