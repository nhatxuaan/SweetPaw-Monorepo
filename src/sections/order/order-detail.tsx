import type { Order } from "src/model/order"; 

import { useLocation } from "react-router-dom";
import React, { useState, useEffect } from 'react';// Đã loại bỏ useCallback, useMemo không dùng
import { useParams, useNavigate } from 'react-router-dom';

// Thêm các import cần thiết cho Table
import Card from '@mui/material/Card';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import Button from '@mui/material/Button';
import TableRow from '@mui/material/TableRow';
import MenuItem from '@mui/material/MenuItem';
import TableHead from '@mui/material/TableHead';
import TextField from '@mui/material/TextField';
import TableCell from '@mui/material/TableCell';
import TableBody from '@mui/material/TableBody';
import Typography from '@mui/material/Typography';
import Breadcrumbs from '@mui/material/Breadcrumbs';
import TableContainer from '@mui/material/TableContainer';

import { DashboardContent } from 'src/layouts/dashboard';
// ----------------------------------------------------------------------

const PAYMENT_METHOD_OPTIONS = [
  { value: 'Thanh toán khi nhận hàng', label: 'Thanh toán khi nhận hàng' },
  { value: 'Chuyển khoản', label: 'Chuyển khoản' },
];

const DELIVERY_STATUS_OPTIONS = [
  { value: 'Đang xử lý', label: 'Đang xử lý' },
  { value: 'Đang giao hàng', label: 'Đang giao hàng' },
  { value: 'Đã giao thành công', label: 'Đã giao thành công' },
];

const PAYMENT_STATUS_OPTIONS = [
  { value: 'PENDING', label: 'Chưa thanh toán' },
  { value: 'SUCCESS', label: 'Đã thanh toán thành công' },
];

// Hàm định dạng tiền tệ
const formatCurrency = (amount: number | string) => {
    // Đảm bảo amount là số và định dạng sang tiền tệ Việt Nam
    const numberAmount = Number(amount);
    if (isNaN(numberAmount)) return amount; // Trả về nguyên gốc nếu không phải số
    return numberAmount.toLocaleString('vi-VN');
};


const formatFullAddress = (
  address?: string,
  ward?: string,
  district?: string,
  province?: string ) => {
  const parts = [address, ward, district, province].filter(Boolean);
  return parts.join(", ");
};

export default function OrdertDetailView() {
  //const { id } = useParams();
  const navigate = useNavigate();
  const { state: order } = useLocation() as { state: Order };

 // const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    _id: '',
    to_name: '',
    to_phone:'',
    to_address:'',
    to_ward_name:'',
    to_district_name:'',
    to_province_name:'',
    note: '',
    ghn_order_code: '',
    shipping_fee: '',
    tongtien: '',
    discountAmount: '',
    total_price: '',
    createdAt: '',
    payment_method: '',
    status: '',
    paymentStatus: '',
    items: [] as Order['items'],
    full_address: '',
  });

  useEffect(() => {
    if (order) {
      setFormData({
        _id: order._id || '',
        to_name: order.to_name || '',
        to_phone:order.to_phone || '',
        to_address:order.to_address || '',
        to_ward_name:order.to_ward_name || '',
        to_district_name:order.to_district_name || '',
        to_province_name: order.to_province_name || '',
        note: order.note || '',
        ghn_order_code: order.ghn_order_code || '',
        shipping_fee: order.shipping_fee.toString() || '',
        tongtien: order.tongtien.toString() || '',
        discountAmount: order.discountAmount.toString() || '',
        total_price: order.total_price.toString() || '',
        createdAt: order.createdAt || '',
        payment_method: order.payment_method || '',
        status: order.status || '',
        paymentStatus: order.paymentStatus || '',
        items: order.items || [],
        
        full_address: formatFullAddress(
          order.to_address,
          order.to_ward_name,
          order.to_district_name,
          order.to_province_name
        ),
      });
    }
  }, [order]);

  return (
    <DashboardContent>
      {/* Breadcrumb */}
      <Breadcrumbs sx={{ mb: 3 }}>
        <Link
          underline="hover"
          color="inherit"
          onClick={() => navigate('/sweetpaw/order')}
          sx={{ cursor: 'pointer' }}
        >
          Đơn hàng
        </Link>
        <Typography color="text.primary">
          Xem chi tiết đơn hàng
        </Typography>
      </Breadcrumbs>

      {/* Header */}
      <Stack direction="row" alignItems="center" justifyContent="space-between" mb={3}>
        <Typography variant="h4">
          Xem chi tiết đơn hàng
        </Typography>
        <Button
          variant="text"
          onClick={() => navigate('/sweetpaw/order')}
        >
          Quay lại
        </Button>
      </Stack>

      {/* Form */}
      {/* Wrapper Grid Container */}
      <form>
        <Grid container spacing={3} justifyContent="center">
          {/* Cột chính chứa Card */}
          <Grid size={{xs: 12, md: 12}}>
            <Card sx={{ p: 3 }}>
              <Typography variant="h6" sx={{ mb: 3 }}>
                Chi tiết đơn hàng
              </Typography>

              <Stack spacing={3}>
                <Grid container spacing={3}>
                  
                  {/*Tên khách hàng */}
                  <Grid size={{xs: 12, md: 6}}>
                    <TextField
                      fullWidth
                      required
                      name="to_name"
                      label="Họ tên khách hàng"
                      value={formData.to_name}
                      InputProps={{ readOnly: true }}
                    />
                  </Grid>

                  {/* Mã đơn hàng */}
                  <Grid size={{xs: 12, md: 6}}>
                    <TextField
                      fullWidth
                      required
                      name="ghn_order_code"
                      label="Mã đơn hàng"
                      value={formData.ghn_order_code}
                      InputProps={{ readOnly: true }}
                    />
                  </Grid>

                  {/*Sdt */}
                  <Grid size={{xs: 12, md: 6}}>
                    <TextField
                      fullWidth
                      required
                      name="to_phone"
                      label="Số điện thoại"
                      value={formData.to_phone}
                      InputProps={{ readOnly: true }}
                    />
                  </Grid>

                  {/* Ngày đặt */}
                  <Grid size={{xs: 12, md: 6}}>
                    <TextField
                      fullWidth
                      required
                      name="createdAt"
                      label="Ngày đặt"
                      value={ formData.createdAt ? new Date(formData.createdAt).toLocaleString("vi-VN") : ''}
                     
                      InputProps={{ readOnly: true }}
                    />
                  </Grid>

                  {/*Ghi chu*/}
                  <Grid size={{xs: 12, md: 6}}>
                    <TextField
                      fullWidth
                      required
                      name="note"
                      label="Ghi chú"
                      value={formData.note}
                      InputProps={{ readOnly: true }}
                    />
                  </Grid>

                   {/* Trạng thái giao hàng */}
                  <Grid size={{xs: 12, md: 6}}>
                    <TextField
                      fullWidth
                      select
                      required
                      name="status"
                      label="Trạng thái giao hàng"
                      value={formData.status}
                      InputProps={{ readOnly: true }}
                    >
                      {DELIVERY_STATUS_OPTIONS.map((option) => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>

                    {/*Phương thức thanh toán */}
                  <Grid size={{xs: 12, md: 6}}>
                    <TextField
                      fullWidth
                      select
                      required
                      name="payment_method"
                      label="Phương thức thanh toán"
                      value={formData.payment_method}
                      InputProps={{ readOnly: true }}
                    >
                      {PAYMENT_METHOD_OPTIONS.map((option) => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>

                  {/* Trạng thái thanh toán */}
                  <Grid size={{xs: 12, md: 6}}>
                    <TextField
                      fullWidth
                      select
                      required
                      name="paymentStatus"
                      label="Trạng thái thanh toán"
                      value={formData.paymentStatus}
                      InputProps={{ readOnly: true }}
                    >
                      {PAYMENT_STATUS_OPTIONS.map((option) => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>


                  {/*Dia chi*/}
                  <Grid size={{xs: 12, md: 12}}>
                    <TextField
                      fullWidth
                      required
                      multiline
                      minRows={1}
                      name="full_address"
                      label="Địa chỉ"
                      value={formData.full_address}
                      InputProps={{ readOnly: true }}
                    />
                  </Grid>
                </Grid>
              </Stack>
            </Card>
          </Grid>  
              {/* KHỐI 2: DANH SÁCH SẢN PHẨM (Dạng 12 cột, nằm bên dưới Khối 1) */}
        <Grid size= {{xs:12, md:12}}>
            <Card sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ mb: 3 }}>
                    Danh sách sản phẩm
                </Typography>
                
                {/* Bảng sản phẩm */}
                <TableContainer>
                    <Table size="small" sx={{ minWidth: '100%' }}>
                        <TableHead>
                            <TableRow>
                                <TableCell>Tên sản phẩm</TableCell>
                                <TableCell align="center">Số lượng</TableCell>
                                <TableCell align="right">Giá bán</TableCell>
                                <TableCell align="right">Thành tiền</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {formData.items.map((item, index) => (
                                <TableRow key={index}>
                                    <TableCell>{item.name}</TableCell>
                                    <TableCell align="center">{item.quantity}</TableCell>
                                    <TableCell align="right">{formatCurrency(item.price)}</TableCell>
                                    <TableCell align="right">{formatCurrency(item.quantity * item.price)}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>

                {/* Tổng kết đơn hàng (width 280px) */}
                <Stack spacing={1} alignItems="flex-end" sx={{ mt: 3, pr: 2 }}>
                    <Stack direction="row" justifyContent="space-between" width={280}>
                        <Typography variant="body2" sx={{ fontWeight: 'bold' }}>Tổng tiền hàng</Typography>
                        <Typography variant="body2">{formatCurrency(formData.tongtien)}</Typography>
                    </Stack>
                    <Stack direction="row" justifyContent="space-between" width={280}>
                        <Typography variant="body2" sx={{ fontWeight: 'bold' }}>Phí vận chuyển</Typography>
                        <Typography variant="body2">{formatCurrency(formData.shipping_fee)}</Typography>
                    </Stack>
                    <Stack direction="row" justifyContent="space-between" width={280}>
                        <Typography variant="body2" sx={{ fontWeight: 'bold' }}>Giảm giá</Typography>
                        <Typography variant="body2" color="error"> {formatCurrency(formData.discountAmount)}</Typography>
                    </Stack>
                    <Stack direction="row" justifyContent="space-between" width={280} sx={{ pt: 1, borderTop: '1px dashed grey' }}>
                        <Typography variant="subtitle1">Tổng tiền</Typography>
                        <Typography variant="subtitle1" color="primary">{formatCurrency(formData.total_price)}</Typography>
                    </Stack>
                </Stack>
            </Card>
          </Grid>    
        </Grid>
      </form>
    </DashboardContent>
  );
}