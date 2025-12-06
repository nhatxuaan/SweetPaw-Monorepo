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
import { apiUpdateProduct, apiDeleteProduct } from 'src/services/productApi';

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

export default function ProductEditView() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { state: product } = useLocation();

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

  useEffect(() => {
    if (product) {
      setFormData({
        name: product.name,
        costprice: product.cost.toString(),
        sellingprice: product.price.toString(),
        category: product.category.toString(),
        stock: product.stock.toString(),
        description: product.des,
        coverUrl: product.url,
        flavor: product.flavor,
      });
    }
  }, [product]);


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
      await apiUpdateProduct(id!, {
        name: formData.name,
        category: formData.category,
        price: formData.sellingprice,
        cost: formData.costprice,
        des: formData.description,
        stock: formData.stock,
        flavor: formData.flavor,
        url: formData.coverUrl,
      });

      navigate("/sweetpaw/products");
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };


// Xử lý xóa sản phẩm
const handleDeleteProduct = useCallback(async () => {
  if (!window.confirm("Bạn có chắc chắn muốn xóa sản phẩm này không?")) return;

  try {
    setLoading(true);

    // Gọi API
    const res = await apiDeleteProduct(id!);

    navigate("/sweetpaw/products");

  } catch (error: any) {
    const msg =
      error?.response?.data?.message ||
      error?.message ||
      "Có lỗi xảy ra";

    alert(msg);
  } finally {
    setLoading(false);
  }
}, [id, navigate]);



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
        <Typography color="text.primary">
          Chỉnh sửa
        </Typography>
      </Breadcrumbs>

      {/* Header */}
      <Stack direction="row" alignItems="center" justifyContent="space-between" mb={3}>
        <Typography variant="h4">
         Chỉnh sửa sản phẩm
        </Typography>
        <Button
          variant="text"
          //startIcon={<Iconify icon="eva:arrow-back-fill" />}
          onClick={() => navigate('/sweetpaw/products')}
        >
          Quay lại
        </Button>
      </Stack>

      {/* Form */}
      <form onSubmit={handleUpdate}>
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
              <TextField
                fullWidth
                name="coverUrl"
                label="URL hình ảnh"
                value={formData.coverUrl}
                onChange={handleChange}
                placeholder="https://example.com/image.jpg"
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
                            variant="outlined"
                            color="error" // Màu đỏ, tượng trưng cho hành động nguy hiểm
                            onClick={handleDeleteProduct} 
                            disabled={loading}
                            sx={{ mr: 'auto' }} // Đẩy nút này về phía trái
                        >
                            Xóa sản phẩm
                        </Button>


                  <Button
                    type="submit"
                    variant="contained"
                    disabled={loading}
                    //startIcon={loading ? <Iconify icon="eos-icons:loading" /> : null}
                  >
                    {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
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

