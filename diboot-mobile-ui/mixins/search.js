/**
 * search mixins
 *
 * @author : uu
 * @version v1.0
 * @Date 2023/9/19  23:05
 */
import more from './more'
export default {
  mixins: [more],
  data() {
    return {
      showSearch: false
    }
  },
  methods: {
    // 打开
    open() {
      this.showSearch = true
      this.attachMore()
    },
    // 关闭
    reset() {
      this.modelValue = {}
      this.confirm()
    },
    // 确认
    confirm() {
      this.$emit('search')
      this.close()
    },
    // 关闭
    close() {
      this.showSearch = false
    }
  },
  computed: {
    modelValue: {
      get() {
        return this.value
      },
      set(value) {
        this.$emit('input', value)
      }
    }
  },
  props: {
    value: {
      type: Object,
      default: () => {}
    }
  }

}
 