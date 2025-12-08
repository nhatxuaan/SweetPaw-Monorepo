// src/sections/product/view/products-view.tsx
import type { Product } from "src/model/product";

import { useNavigate } from 'react-router-dom';
import { useState, useCallback, useEffect } from 'react';

import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Button from '@mui/material/Button';
import Pagination from '@mui/material/Pagination';
import Typography from '@mui/material/Typography';

import { _products } from 'src/_mock';
import { DashboardContent } from 'src/layouts/dashboard';
import { apiGetProducts } from "src/services/productApi";

import { Iconify } from 'src/components/iconify';

import { ProductItem } from '../product-item';
import { ProductSort } from '../product-sort';
import { CartIcon } from '../product-cart-widget';
import { ProductFilters } from '../product-filters';

import type { FiltersProps } from '../product-filters';

// ----------------------------------------------------------------------

const CATEGORY_OPTIONS = [
  { value: 'all', label: 'Tất cả' },
  { value: 'Bánh kem', label: 'Bánh kem' },
  { value: 'Bánh mì', label: 'Bánh mì' },
  { value: 'Bánh ngọt', label: 'Bánh ngọt' },
  { value: 'Bánh quy', label: 'Bánh quy' },
  { value: 'Đồ uống', label: 'Đồ uống' },
  { value: 'Bánh mini', label: 'Bánh mini' },
];

const RATING_OPTIONS = ['up4Star', 'up3Star', 'up2Star', 'up1Star'];

const PRICE_OPTIONS = [
  { value: 'below100', label: '<100k' },
  { value: '100below200', label: '100k - <200k' },
  { value: '200below300', label: '200k - <300k' },
  { value: '300below400', label: '300k - <400k' },
  { value: 'high400', label: '>400k' },
];

const defaultFilters = {
  price: '',
  rating: RATING_OPTIONS[0],
  category: CATEGORY_OPTIONS[0].value,
};


export function ProductsView() {
  const navigate = useNavigate(); 
  const [products, setProducts] = useState<Product[]>([]);

  
   // GỌI API LẤY SẢN PHẨM
  useEffect(() => {
    async function loadProducts() {
      try {
        const data = await apiGetProducts();
        console.log("Products:", data);

        setProducts(data); // backend trả array → set luôn
      } catch (err) {
        console.error("Load products failed:", err);
      }
    }

    loadProducts();
  }, []);

  const [sortBy, setSortBy] = useState('featured');
  const [openFilter, setOpenFilter] = useState(false);
  const [filters, setFilters] = useState<FiltersProps>(defaultFilters);

  const handleOpenFilter = useCallback(() => {
    setOpenFilter(true);
  }, []);

  const handleCloseFilter = useCallback(() => {
    setOpenFilter(false);
  }, []);

  const handleSort = useCallback((newSort: string) => {
    setSortBy(newSort);
  }, []);

  const handleSetFilters = useCallback((updateState: Partial<FiltersProps>) => {
    setFilters((prevValue) => ({ ...prevValue, ...updateState }));
  }, []);

  // Xử lý khi click vào sản phẩm
  // const handleEditProduct = useCallback((productId: string) => {
  //   navigate(`/sweetpaw/products/${productId}/edit`);
  // }, [navigate]);

  const handleEditProduct = useCallback((product: Product) => {
    navigate(`/sweetpaw/products/${product._id}/edit`, { state: product });
  }, [navigate]);

  // Xử lý khi click "Thêm sản phẩm mới"
  const handleAddProduct = useCallback(() => {
    navigate('/sweetpaw/products/new/edit');
  }, [navigate]);

  const canReset = Object.keys(filters).some(
    (key) => filters[key as keyof FiltersProps] !== defaultFilters[key as keyof FiltersProps]
  );

  return (
    <DashboardContent>
      <Box
        sx={{
          mb: 2,
          display: 'flex',
          alignItems: 'center',
        }}
      >
        <Typography variant="h4" sx={{ flexGrow: 1 }}>
          Sản phẩm
        </Typography>
        <Button
          variant="contained"
          color="inherit"
          startIcon={<Iconify icon="mingcute:add-line" />}
          onClick={handleAddProduct} 
        >
          Thêm sản phẩm mới
        </Button>
      </Box>

      <Box
        sx={{
          mb: 5,
          display: 'flex',
          alignItems: 'center',
          flexWrap: 'wrap-reverse',
          justifyContent: 'flex-end',
        }}
      >
        <Box
          sx={{
            my: 1,
            gap: 1,
            flexShrink: 0,
            display: 'flex',
          }}
        >
          <ProductFilters
            canReset={canReset}
            filters={filters}
            onSetFilters={handleSetFilters}
            openFilter={openFilter}
            onOpenFilter={handleOpenFilter}
            onCloseFilter={handleCloseFilter}
            onResetFilter={() => setFilters(defaultFilters)}
            options={{
              categories: CATEGORY_OPTIONS,
              ratings: RATING_OPTIONS,
              price: PRICE_OPTIONS,
            }}
          />

          <ProductSort
            sortBy={sortBy}
            onSort={handleSort}
            options={[
              { value: 'priceDesc', label: 'Giá: Cao - Thấp' },
              { value: 'priceAsc', label: 'Giá: Thấp - Cao' },
            ]}
          />
        </Box>
      </Box>

      <Grid container spacing={3}>
        {products.map((product) => (
          <Grid key={product._id} size={{ xs: 12, sm: 6, md: 3 }}>
            <ProductItem 
              // product={{
              //    _id: product._id,
              //   name: product.name,
              //   price: product.price,
              //   url: product.url,
              //   rating_avg: product.rating_avg,
              // }}
              // onEdit={handleEditProduct} // ← THÊM PROP NÀY

              product={product}     // truyền full object từ backend
              onEdit={handleEditProduct}
            />
          </Grid>
        ))}
      </Grid>


 

      {/* <Pagination count={10} color="primary" sx={{ mt: 8, mx: 'auto' }} /> */}
    </DashboardContent>
  );
}