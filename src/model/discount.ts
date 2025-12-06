export interface Discount {
  _id: string;
  code: string;
  name: string;
  description: string;
  type: "percent" | "fixed";    // percent = %, fixed = số tiền
  value: number;
  maxDiscount: number;
  minOrderValue: number;
  startDate: string;            // backend trả ISO string
  endDate: string;              // backend trả ISO string
  quantity: number;
  isActive: boolean;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
}