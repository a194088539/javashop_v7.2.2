import CategoryPicker from './CategoryPicker'
import GoodsSkuPicker from './GoodsSkuPicker/index'
import FloorTagsPicker from './FloorTagsPicker'
import FloorTitlePicker from './FloorTitlePicker'
import GradeEditor from './GradeEditor'
import ShopPicker from './ShopPicker'
import BrandPicker from './BrandPicker'
import UE from './UE'

const components = {
  CategoryPicker,
  GoodsSkuPicker,
  FloorTagsPicker,
  FloorTitlePicker,
  GradeEditor,
  ShopPicker,
  BrandPicker
}

components.install = function(Vue) {
  Object.keys(components).forEach(function(key) {
    key !== 'install' && Vue.component(components[key].name, components[key])
  })
}

export default components

export {
  CategoryPicker,
  GoodsSkuPicker,
  FloorTagsPicker,
  FloorTitlePicker,
  GradeEditor,
  ShopPicker,
  BrandPicker,
  UE
}
