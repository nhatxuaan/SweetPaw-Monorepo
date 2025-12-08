export interface OrderItem {
  productId: string;
  name: string;
  quantity: number;
  price: number;
  weight: number;
  _id: string;
}

export interface Order {
  _id: string;
  user: string;
  to_name: string;
  to_phone: string;
  to_address: string;
  to_ward_name: string;
  to_district_name: string;
  to_province_name: string;

  note?: string;

  items: OrderItem[];

  ghn_order_code: string;
  shipping_fee: number;
  cod_amount: number;
  total_price: number;
  status: string;
  paymentStatus: string;
  payment_method: string;

  tongtien: number;
  discountAmount: number;

  createdAt: string;
  updatedAt: string;
  __v?: number;
}
