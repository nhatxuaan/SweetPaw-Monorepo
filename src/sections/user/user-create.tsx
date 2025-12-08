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

import { apiPostAuth } from "src/services/apiClient"; 
import { apiCreateUsers } from "src/services/userApi";
import { DashboardContent } from 'src/layouts/dashboard';

import { Iconify } from 'src/components/iconify';

// ----------------------------------------------------------------------
interface DiaChiType  {
  SoNha: string;
  TenDuong: string;
  PhuongXa: string;
  QuanHuyen: string;
  ThanhPho: string;
  MacDinh: boolean;
}

export default function UserEditView() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { state: customer } = useLocation();

  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phoneNumber: '',
    address: '',
  });


  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault();
  setLoading(true);

  try {
    const  diaChiArray: DiaChiType [] = [];


    if (formData.address.trim() !== "") {
      const parts = formData.address.split(",").map(i => i.trim());

      diaChiArray.push({
        SoNha: parts[0] || "",
        TenDuong: parts[1] || "",
        PhuongXa: parts[2] || "",
        QuanHuyen: parts[3] || "",
        ThanhPho: parts[4] || "",
        MacDinh: false
      });
    }

    const payload = {
      Hoten: formData.name,
      HoTen: formData.name,
      Email: formData.email,
      SoDienThoai: formData.phoneNumber,
      DiaChi: diaChiArray    // nếu không nhập → []
    };

    console.log("Gửi lên backend:", payload);

    await apiPostAuth("/api/admin/customers", payload);
    //await apiCreateUsers("/api/admin/customers", payload);

    navigate("/sweetpaw/user");

  } catch (error: any) {
    const msg =
      error?.response?.data?.message ||
      error?.message ||
      "Có lỗi xảy ra";

    alert(msg);
    
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
          onClick={() => navigate('/sweetpaw/user')}
          sx={{ cursor: 'pointer' }}
        >
          Khách hàng
        </Link>
        <Typography color="text.primary">
          Thêm mới
        </Typography>
      </Breadcrumbs>

      {/* Header */}
      <Stack direction="row" alignItems="center" justifyContent="space-between" mb={3}>
        <Typography variant="h4">
          Thêm mới thông tin khách hàng
        </Typography>
        <Button
          variant="text"
          onClick={() => navigate('/sweetpaw/user')}
        >
          Quay lại
        </Button>
      </Stack>

      {/* Form */}
      <form onSubmit={handleSubmit}>
        <Grid container spacing={3}>
          {/* Thông tin khách hàng */}
          <Grid size={{ xs: 12, md: 12 }}>
            <Card sx={{ p: 3 }}>
              <Typography variant="h6" sx={{ mb: 3 }}>
                Thông tin khách hàng
              </Typography>

              <Stack spacing={3}>
                {/* Tên khách hàng */}
                <TextField
                  fullWidth
                  required
                  name="name"
                  label="Họ tên khách hàng"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="Nhập họ tên khách hàng"
                />

                {/* Email */}
                <TextField
                  fullWidth
                  required
                  name="email"
                  label="Email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="Nhập email"
                />

                {/* Số điện thoại */}
                <TextField
                  fullWidth
                  name="phoneNumber"
                  label="Số điện thoại"
                  value={formData.phoneNumber}
                  onChange={handleChange}
                  placeholder="Nhập số điện thoại"
                />

                {/* Địa chỉ */}
                 <TextField
                  fullWidth
                  name="address"
                  label="Địa chỉ"
                  value={formData.address}
                  onChange={handleChange}
                  placeholder="Nhập địa chỉ"
                />

                {/* Buttons */}
                <Stack direction="row" spacing={2} justifyContent="flex-end">

                  <Button
                    type="submit"
                    variant="contained"
                    disabled={loading}
                  >
                    {loading ? 'Đang lưu...' : 'Tạo mới'}
                  </Button>
                </Stack>
              </Stack>
            </Card>
          </Grid>
        </Grid>
      </form>
    </DashboardContent>
  );
}

