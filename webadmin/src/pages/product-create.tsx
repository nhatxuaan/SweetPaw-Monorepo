// Import Component View đã gộp logic và UI
import { CONFIG } from 'src/config-global';

import ProductCreateView from 'src/sections/product/product-create';
// ----------------------------------------------------------------------

export default function Page() {

  
  return (
    <>
      <title>{`Thêm sản phẩm mới - ${CONFIG.appName}`}</title>
      
      <ProductCreateView />
    </>
  );
}