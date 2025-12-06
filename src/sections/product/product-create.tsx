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
import { apiCreateProducts } from 'src/services/productApi';

import { Iconify } from 'src/components/iconify';

// ----------------------------------------------------------------------

const CATEGORY_OPTIONS = [
  { value: 'Bánh kem', label: 'Bánh kem' },
  { value: 'Bánh mì', label: 'Bánh mì' },
  { value: 'Bánh ngọt', label: 'Bánh ngọt' },
  { value: 'Bánh quy', label: 'Bánh quy' },
  { value: 'Đồ uống', label: 'Đồ uống' },
  { value: 'Bánh mini', label: 'Bánh mini' },
];

export default function ProductCreateView() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    costprice: '',
    sellingprice: '',
    category: 'Bánh kem',
    stock: '',
    description: '',
    coverUrl: '',
    flavor: '',
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
      await apiCreateProducts({
        name: formData.name,
        category: formData.category,
        price: formData.sellingprice,  
        cost: formData.costprice,      
        des: formData.description,    
        stock: formData.stock,
        flavor: formData.flavor,
        imageFile: imageFile,
      });

      navigate('/sweetpaw/products');
    } catch (error) {
      console.error('Error saving product:', error);
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
          onClick={() => navigate('/sweetpaw/products')}
          sx={{ cursor: 'pointer' }}
        >
          Sản phẩm
        </Link>
        <Typography color="text.primary">Thêm mới</Typography>
      </Breadcrumbs>

      {/* Header */}
      <Stack direction="row" alignItems="center" justifyContent="space-between" mb={3}>
        <Typography variant="h4">Thêm sản phẩm mới</Typography>
        <Button
          variant="text"
          //startIcon={<Iconify icon="eva:arrow-back-fill" />}
          onClick={() => navigate('/sweetpaw/products')}
        >
          Quay lại
        </Button>
      </Stack>

      {/* Form */}
      <form onSubmit={handleSubmit}>
        <Grid container spacing={3}>
          {/* Ảnh sản phẩm */}
          <Grid size={{ xs: 12, md: 4 }}>
            <Card sx={{ p: 3 }}>
              <Typography variant="h6" sx={{ mb: 2 }}>
                Hình ảnh
              </Typography>
              {formData.coverUrl && (
                <Box
                  component="img"
                  src={formData.coverUrl}
                  alt="Preview"
                  sx={{
                    width: '100%',
                    height: 300,
                    objectFit: 'cover',
                    borderRadius: 1,
                    mb: 2,
                  }}
                />
              )}
      

              <input
                type="file"
                accept="image/*"
                onChange={(e) => {
                  const file = e.target.files?.[0] || null;
                  setImageFile(file);

                  if (file) {
                    // preview ảnh ngay
                    setFormData((prev) => ({
                      ...prev,
                      coverUrl: URL.createObjectURL(file),
                    }));
                  }
                }}
              />
            </Card>
          </Grid>

          {/* Thông tin sản phẩm */}
          <Grid size={{ xs: 12, md: 8 }}>
            <Card sx={{ p: 3 }}>
              <Typography variant="h6" sx={{ mb: 3 }}>
                Thông tin sản phẩm
              </Typography>

              <Stack spacing={3}>
                {/* Tên sản phẩm */}
                <TextField
                  fullWidth
                  required
                  name="name"
                  label="Tên sản phẩm"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="Nhập tên sản phẩm"
                />

                {/* Giá vốn và giá bán */}
                <Grid container spacing={2}>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <TextField
                      fullWidth
                      required
                      type="number"
                      name="costprice"
                      label="Giá vốn (VNĐ)"
                      value={formData.costprice}
                      onChange={handleChange}
                      placeholder="0"
                    />
                  </Grid>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <TextField
                      fullWidth
                      required
                      type="number"
                      name="sellingprice"
                      label="Giá bán (VNĐ)"
                      value={formData.sellingprice}
                      onChange={handleChange}
                      placeholder="0"
                    />
                  </Grid>
                </Grid>

                {/* Danh mục và Số lượng */}
                <Grid container spacing={2}>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <TextField
                      fullWidth
                      select
                      required
                      name="category"
                      label="Danh mục"
                      value={formData.category}
                      onChange={handleChange}
                    >
                      {CATEGORY_OPTIONS.map((option) => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <TextField
                      fullWidth
                      required
                      type="number"
                      name="stock"
                      label="Số lượng"
                      value={formData.stock}
                      onChange={handleChange}
                      placeholder="0"
                    />
                  </Grid>
                </Grid>

                {/* Mô tả */}
                <TextField
                  fullWidth
                  multiline
                  name="flavor"
                  label="Hương vị"
                  value={formData.flavor}
                  onChange={handleChange}
                  placeholder="Nhập mô tả hương vị của sản phẩm..."
                />

                {/* Mô tả */}
                <TextField
                  fullWidth
                  multiline
                  rows={4}
                  name="description"
                  label="Mô tả sản phẩm"
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="Nhập mô tả chi tiết về sản phẩm..."
                />

                {/* Buttons */}
                <Stack direction="row" spacing={2} justifyContent="flex-end">
                  <Button
                    type="submit"
                    variant="contained"
                    disabled={loading}
                    //startIcon={loading ? <Iconify icon="eos-icons:loading" /> : null}
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
