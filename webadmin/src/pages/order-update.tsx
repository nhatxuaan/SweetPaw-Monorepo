
// Import Component View đã gộp logic và UI
import { CONFIG } from 'src/config-global';

import OrderUpdateView from 'src/sections/order/order-update';
// ----------------------------------------------------------------------

export default function Page() {

  
  return (
    <>
      <title>{`Cập nhật đơn hàng - ${CONFIG.appName}`}</title>
      
      <OrderUpdateView />
    </>
  );
}