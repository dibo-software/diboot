const findLabel = (dataList: LabelValue[] = [], val: any | any[]): string | undefined => {
  if (Array.isArray(val)) {
    return val.map(e => findLabel(dataList, e)).join('、')
  } else {
    for (const data of dataList) {
      if (data.value === val) return data.label
      else if (data.children?.length) return findLabel(data.children, val)
    }
  }
}

export { findLabel }
