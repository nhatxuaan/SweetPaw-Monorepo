import { useLocation } from "react-router-dom";
import { useParams, useNavigate } from 'react-router-dom';
import React, { useState, useEffect, useCallback, useMemo } from 'react';

import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import MenuItem from '@mui/material/MenuItem';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Breadcrumbs from '@mui/material/Breadcrumbs';

import { DashboardContent } from 'src/layouts/dashboard';
import { apiUpdatePromotion } from "src/services/discountApi";

import { Iconify } from 'src/components/iconify';

// ----------------------------------------------------------------------

const TYPES_OPTIONS = [
  { value: 'fixed', label: 'Số tiền cố định' },
  { value: 'percent', label: 'Phần trăm' },
];
const ACTIVE_OPTIONS = [
  { value: 'true', label: 'Còn khả dụng' },
  { value: 'false', label: 'Hết hạn' },
];
export default function DiscountUpdateView() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { state: discount } = useLocation();

  const [loading, setLoading] = useState(false);

  const [formData, setFormData] = useState({
    id: '',
    code: '',
    name: '',
    description: '',
    type: '', 
    value:'',
    maxDiscount:'',
    minOrderValue:'',
    startDate:'',
    endDate:'',
    quantity:'',
    isActive:'',
    createdBy:'',
    createdAt:'',
    updatedAt:'',
  });


  // const formatDateTimeLocal = (dateStr: string | null | undefined) => {
  //   if (!dateStr) return "";
  //   const d = new Date(dateStr);
  //   return `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')}T${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`;
  // };

  function toLocalInputFormat(dateString: string) {
  const clean = dateString.replace("Z", "");
  const [datePart, timePart] = clean.split("T");
  const [hour, minute] = timePart.split(":");
  return `${datePart}T${hour}:${minute}`;
}
// Load dữ liệu vào form
//  useEffect(() => {
//   if (discount) {
//     setFormData({
//       id : discount._id,
//       code: discount.code || "",
//       name: discount.name || "",
//       description: discount.description || "",
//       type: discount.type || "",
//       value: discount.value?.toString() || "",
//       maxDiscount: discount.maxDiscount?.toString() || "",
//       minOrderValue: discount.minOrderValue?.toString() || "",
//       startDate: discount.startDate ? new Date(discount.startDate).toISOString().slice(0, 10) : "",
//       endDate: discount.endDate ? new Date(discount.endDate).toISOString().slice(0, 10) : "",
//       quantity: discount.quantity?.toString() || "",
//       isActive: discount.isActive ? "true" : "false",
//       createdBy: discount.createdBy || "",
//       createdAt: discount.createdAt || "",
//       updatedAt: discount.updatedAt || "",
//     });
//   }
// }, [discount]);

useEffect(() => {
  if (discount) {
    setFormData({
      id: discount._id,
      code: discount.code || "",
      name: discount.name || "",
      description: discount.description || "",
      type: discount.type || "",
      value: discount.value?.toString() || "",
      maxDiscount: discount.maxDiscount?.toString() || "",
      minOrderValue: discount.minOrderValue?.toString() || "",
      startDate: toLocalInputFormat(discount.startDate),
      endDate: toLocalInputFormat(discount.endDate),
      quantity: discount.quantity?.toString() || "",
      isActive: discount.isActive ? "true" : "false",
      createdBy: discount.createdBy || "",
      createdAt: discount.createdAt || "",
      updatedAt: discount.updatedAt || "",
    });
  }
}, [discount]);
// startDate: formatDateTimeLocal(discount.startDate),
// endDate: formatDateTimeLocal(discount.endDate),

const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
};


const handleUpdate = async (e: React.FormEvent) => {
  e.preventDefault();
  setLoading(true);

   try {
    const payload = {
      code: formData.code,
      name: formData.name,
      description: formData.description,
      type: formData.type,
      value: Number(formData.value),
      maxDiscount: Number(formData.maxDiscount),
      minOrderValue: Number(formData.minOrderValue),
      startDate: formData.startDate,
      endDate: formData.endDate,
      quantity: Number(formData.quantity),
      isActive: formData.isActive === "true",
    };

    console.log("UPDATE PAYLOAD:", payload);

    await apiUpdatePromotion(formData.id, payload);

    navigate("/sweetpaw/discount");

  } catch (error: any) {
    alert(error.message || "Lỗi cập nhật");
  } finally {
    setLoading(false);
  }
};




  return (
    <DashboardContent>
      {/* Breadcrumb */}
      <Breadcrumbs sx={{ mb: 3 }}>
        <Link
          underline="hover"
          color="inherit"
          onClick={() => navigate('/sweetpaw/discount')}
          sx={{ cursor: 'pointer' }}
        >
          Khuyến mãi
        </Link>
        <Typography color="text.primary">
          Chỉnh sửa
        </Typography>
      </Breadcrumbs>

      {/* Header */}
      <Stack direction="row" alignItems="center" justifyContent="space-between" mb={3}>
        <Typography variant="h4">
          Thêm mới thông tin khuyến mãi
        </Typography>
        <Button
          variant="text"
          onClick={() => navigate('/sweetpaw/discount')}
        >
          Quay lại
        </Button>
      </Stack>

      {/* Form */}
        <form onSubmit={handleUpdate}>
            <Grid container spacing={3}>
                 <Grid size={{ xs: 12, md: 12 }}>
                    <Card sx={{ p: 3 }}>
                        <Typography variant="h6" sx={{ mb: 3 }}>
                        Thông tin khuyến mãi
                        </Typography>

                         <Grid container spacing={3}>
                            {/* CỘT TRÁI */}
                            <Grid size={{ xs: 12, md: 6 }}>
                                <Stack spacing={3}>
                                    <TextField
                                    fullWidth
                                    required
                                    name="code"
                                    label="Mã code"
                                    value={formData.code}
                                    onChange={handleChange}/>

                                    

                                    <TextField
                                    fullWidth
                                    required
                                    name="minOrderValue"
                                    label="Giá trị đơn hàng tối thiểu"
                                    value={formData.minOrderValue}
                                    onChange={handleChange} />

                                    

                                    <Grid >
                                        <TextField
                                        fullWidth
                                        select
                                        required
                                        name="type"
                                        label="Loại giảm giá"
                                        value={formData.type}
                                        onChange={handleChange}>

                                        {TYPES_OPTIONS.map((option) => (
                                            <MenuItem key={option.value} value={option.value}>
                                            {option.label}
                                            </MenuItem>
                                        ))}
                                        </TextField>
                                    </Grid>

                                    <TextField
                                    fullWidth
                                    required
                                    name="startDate"
                                    type="datetime-local"
                                    label="Ngày bắt đầu"
                                    InputLabelProps={{ shrink: true }}
                                    value={formData.startDate}
                                    onChange={handleChange} />

                                    <TextField
                                    fullWidth
                                    required
                                    name="quantity"
                                    type="number"
                                    label="Số lượng"
                                    value={formData.quantity}
                                    onChange={handleChange} />

                                    <Grid >
                                        <TextField
                                        fullWidth
                                        select
                                        required
                                        name="isActive"
                                        label="Hiệu lực"
                                        value={formData.isActive}
                                        onChange={handleChange}>

                                        {ACTIVE_OPTIONS.map((option) => (
                                            <MenuItem key={option.value} value={option.value}>
                                            {option.label}
                                            </MenuItem>
                                        ))}
                                        </TextField>
                                    </Grid>


                                </Stack>
                            </Grid>
                             {/* CỘT PHẢI */}
                            <Grid size={{ xs: 12, md: 6 }}>
                                <Stack spacing={3}>
                                    <TextField
                                    fullWidth
                                    required
                                    name="name"
                                    label="Tên khuyến mãi"
                                    value={formData.name}
                                    onChange={handleChange}/>

                                    <TextField
                                    fullWidth
                                    required
                                    name="maxDiscount"
                                    label="Giảm giá tối đa"
                                    value={formData.maxDiscount}
                                    onChange={handleChange}/>

                                    <TextField
                                    fullWidth
                                    required
                                    name="value"
                                    label="Giảm giá"
                                    value={formData.value}
                                    onChange={handleChange}/>

                                    <TextField
                                    fullWidth
                                    required
                                    name="endDate"
                                    type="datetime-local"
                                    label="Ngày kết thúc"
                                    InputLabelProps={{ shrink: true }}
                                    value={formData.endDate}
                                    onChange={handleChange} />

                                    <TextField
                                    fullWidth
                                    name="description"
                                    label="Mô tả"
                                    multiline
                                    minRows={3}
                                    value={formData.description}
                                    onChange={handleChange}/>
                                </Stack>
                            </Grid>
                         </Grid>
                        <Stack direction="row" spacing={2} justifyContent="center" mt={3}>
                            <Button type="submit" variant="contained" disabled={loading}>
                                {loading ? 'Đang lưu...' : 'Cập nhật'}
                            </Button>
                        </Stack>
                    </Card>
                 </Grid>
            </Grid>
        </form>
    </DashboardContent>
  );
}

