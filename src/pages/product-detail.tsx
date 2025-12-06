

// src/pages/product-edit.tsx

// Import các hằng số (nếu có, ví dụ CONFIG)
// import { CONFIG } from 'src/config-global'; 

// Import Component View đã gộp logic và UI
import { CONFIG } from 'src/config-global';

//import { ProductEditView } from 'src/sections/product/product-edit-form';
import ProductEditView from 'src/sections/product/product-edit-form';
// ----------------------------------------------------------------------

export default function Page() {

  
  return (
    <>
      <title>{`Chỉnh sửa sản phẩm - ${CONFIG.appName}`}</title>
      
      <ProductEditView />
    </>
  );
}