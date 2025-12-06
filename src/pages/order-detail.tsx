
// Import Component View đã gộp logic và UI
import { CONFIG } from 'src/config-global';

import OrderDetailView from 'src/sections/order/order-detail';
// ----------------------------------------------------------------------

export default function Page() {

  
  return (
    <>
      <title>{`Xem chi tiết đơn hàng - ${CONFIG.appName}`}</title>
      
      <OrderDetailView />
    </>
  );
}