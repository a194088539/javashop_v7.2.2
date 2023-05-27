/**
 * Created by Andste on 2018-12-03.
 */

import * as API_Shop from '@/api/shop'
import { domain } from '@/ui-domain' 
export default {
  async mounted() {
    const res = await API_Shop.getApplyShopInfo()
    if (res['shop_disable'] === 'APPLY') {
      this.$router.replace({path: '/shop/apply/'})
    }
    if (res['shop_disable'] === 'OPEN') {
      window.location.replace(domain.seller)
    }
  }
}
