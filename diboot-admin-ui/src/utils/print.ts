export const print = (...dom: Array<HTMLElement>) => {
  let currDom = ''
  for (const htmlElement of dom) {
    if (!htmlElement) continue
    currDom += htmlElement.outerHTML + getStyle()
  }
  writeIframe(currDom)
}

/**
 * 获取/设置打印样式
 */
const getStyle = () => {
  let str = ''
  const styles = document.querySelectorAll('style,link')
  for (let i = 0; i < styles.length; i++) {
    str += styles[i].outerHTML
  }
  str += '<style>' + '.no-print' + '{display:none;}</style>'
  str += '<style>html,body{background-color:#fff;}</style>'
  return str
}

/**
 * 创建iframe
 * @param content
 */
const writeIframe = (content: string) => {
  const iframe = document.createElement('iframe')
  const f = document.body.appendChild(iframe)
  iframe.id = 'myIframe'
  iframe.setAttribute('style', 'position:absolute;width:0;height:0;top:-10px;left:-10px;')
  const w = f.contentWindow
  const doc = f.contentDocument || (f.contentWindow && f.contentWindow.document)
  doc?.open()
  doc?.write(content)
  doc?.close()
  iframe.onload = function () {
    if (w) {
      toPrint(w)
    }
    setTimeout(function () {
      document.body.removeChild(iframe)
    }, 100)
  }
}

/**
 * 打印
 * @param frameWindow
 */
const toPrint = (frameWindow: Window) => {
  try {
    setTimeout(function () {
      frameWindow.focus()
      try {
        frameWindow.print()
      } catch (e) {
        console.log('e', e)
      }
      frameWindow.close()
    }, 10)
  } catch (err) {
    console.log('err', err)
  }
}
