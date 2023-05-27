let uiUtils = process.env.uiUtils
uiUtils = JSON.parse(uiUtils)
Object.keys(uiUtils).forEach(key => {
  Object.keys(uiUtils[key]).forEach(_key => {
    uiUtils[key][_key] = eval(`(${uiUtils[key][_key]})`)
  })
})

export const Foundation = uiUtils.Foundation
export const RegExp = uiUtils.RegExp
