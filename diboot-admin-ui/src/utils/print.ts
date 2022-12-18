/**
 * 表单转换器 （处理form表单控件value）
 *
 * @param element HTML元素
 */
const formConverter = (element: HTMLElement) => {
  for (const item of element.querySelectorAll<HTMLInputElement | HTMLTextAreaElement>('input,textarea'))
    item.tagName === 'INPUT' ? item.setAttribute('value', item.value) : (item.innerHTML = item.value)
  return element
}

/**
 * 构建 iframe
 *
 * @param elements 元素列表（或元素ID列表）
 * @param mountElement 指定挂载元素
 */
export const buildIframe = (elements: string[] | HTMLElement[], mountElement?: HTMLElement) => {
  if (!elements.length) throw new Error('No element.')
  // 创建iframe
  const iframe = document.createElement('iframe')
  ;(mountElement ?? document.body).appendChild(iframe)
  iframe.style.cssText = 'border:0; position:absolute; width:0px; height:0px; right:0px; top:0px;'
  const doc = iframe.contentDocument ? iframe.contentDocument : iframe.contentWindow?.document
  if (doc == null) throw new Error('Cannot find document.')
  // 写入内容
  doc.open()
  doc.write(
    `${(function () {
      let styles = '<style>html,body{height: max-content; background-color: #ffffff;}</style>'
      for (const item of document.querySelectorAll('style,link')) styles += item.outerHTML
      return styles + '<style>.print-ignore{display: none !important} .print-display{display: flex !important}</style>'
    })()}
    <body>
      ${(typeof elements[0] === 'string'
        ? (elements.map(id => document.getElementById(id as string)).filter(e => !!e) as HTMLElement[])
        : (elements as HTMLElement[])
      ).reduce((html, element) => html + formConverter(element).outerHTML, '')}
    </body>`
  )
  doc.close()
  return iframe
}

/**
 * 打印并移除挂载
 *
 * @param iframe
 */
export const printAndRemove = (iframe: HTMLIFrameElement) => {
  // 调用打印
  iframe.contentWindow?.focus()
  iframe.contentWindow?.print()
  // 移除挂载
  setTimeout(() => iframe.parentNode?.removeChild(iframe), 100)
}

/**
 * 打印
 *
 * @param elements 元素列表（或元素ID列表）
 */
export default (...elements: string[] | HTMLElement[]) => printAndRemove(buildIframe(elements))
