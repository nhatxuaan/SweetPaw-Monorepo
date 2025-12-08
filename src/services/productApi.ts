import { apiGetAuth, apiPostAuthNonContent, apiPutAuth, apiDeleteAuth } from './apiClient';

export async function apiGetProducts() {
  return apiGetAuth('/api/admin/products');
}

export async function apiCreateProducts(productData: any) {
  const formData = new FormData();

  formData.append("name", productData.name);
  formData.append("category", productData.category);
  formData.append("price", productData.price);
  formData.append("cost", productData.cost);
  formData.append("des", productData.des);
  formData.append("stock", productData.stock);
  formData.append("flavor", productData.flavor);

  if (productData.imageFile) {
    formData.append("imageFile", productData.imageFile);
  }

  return apiPostAuthNonContent("/api/admin/products", formData);
}

export async function apiUpdateProduct(id: string, data: any) {
  return apiPutAuth(`/api/admin/products/${id}`, data);
}

// export async function apiDeleteProduct(id: string) {
//   return apiDeleteAuth(`/api/admin/products/${id}`);
// }

export async function apiDeleteProduct(id: string) {
  return apiDeleteAuth(`/api/admin/products/${id}`)
    .then((res) => res.data)   //chỉ trả về data
    .catch((error) => {
      throw error;             //ném lỗi để component bắt
    });
}

