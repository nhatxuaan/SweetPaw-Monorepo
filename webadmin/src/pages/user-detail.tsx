

// src/pages/product-edit.tsx

// Import các hằng số (nếu có, ví dụ CONFIG)
// import { CONFIG } from 'src/config-global'; 

// Import Component View đã gộp logic và UI
import { CONFIG } from 'src/config-global';

//import { ProductEditView } from 'src/sections/product/product-edit-form';
import UserEditView from 'src/sections/user/user-edit';
// ----------------------------------------------------------------------

export default function Page() {

  
  return (
    <>
      <title>{`Chỉnh sửa khách hàng - ${CONFIG.appName}`}</title>
      
      <UserEditView />
    </>
  );
}