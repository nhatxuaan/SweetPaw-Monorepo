// src/sections/product/product-item.tsx
import type { Product } from "src/model/product";

import Box from '@mui/material/Box';
import Link from '@mui/material/Link';
import Card from '@mui/material/Card';
import Stack from '@mui/material/Stack';
import Rating from '@mui/material/Rating';
import Typography from '@mui/material/Typography';

import { fCurrency } from 'src/utils/format-number';


// ----------------------------------------------------------------------

// export type ProductItemProps = {
//   id: string;
//   name: string;
//   price: number;
//   coverUrl: string;
//   ratingAverage?: number;
//   // Thêm optional fields để tránh lỗi type
//   priceSale?: number | null;
//   colors?: string[];
//   status?: string;
// };

export type ProductItemProps = Product;
// export type ProductItemProps = {
//   _id: string;
//   name: string;
//   price: number;
//   url: string;
//   rating_avg: number;

//   category: string;
//   flavor: string;
//   cost: number;
//   des: string;
//   stock: number;
//   sold_count: number;
//   weight: number;
// };

type Props = {
  product: ProductItemProps;
  // onEdit?: (productId: string) => void;
  onEdit?: (product: ProductItemProps) => void;
  
};

export function ProductItem({ product, onEdit }: Props) {
  const renderImg = (
    <Box
      component="img"
      alt={product.name}
      src={product.url}
      sx={{
        top: 0,
        width: 1,
        height: 1,
        objectFit: 'cover',
        position: 'absolute',
      }}
    />
  );

  const renderPrice = (
    <Typography variant="subtitle1">
      {fCurrency(product.price)}
    </Typography>
  );

  const renderRating = (
    <Stack direction="row" alignItems="center" spacing={0.5}>
      <Rating 
        name="read-only" 
        value={product.rating_avg}
        precision={0.1} 
        readOnly 
        size="small"
      />
    </Stack>
  );

  return (
    <Card
      sx={{
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        '&:hover': {
          boxShadow: 6,
          transform: 'translateY(-4px)',
        },
      }}
      //onClick={() => onEdit?.(product._id)}
      onClick={() => onEdit?.(product)}
    >
      <Box sx={{ pt: '100%', position: 'relative' }}>
        {renderImg}
      </Box>

      <Stack spacing={2} sx={{ p: 3 }}>
        <Link 
          color="inherit" 
          underline="hover" 
          variant="subtitle2" 
          noWrap
          onClick={(e) => {
            e.stopPropagation();
            //onEdit?.(product._id);
            onEdit?.(product);
          }}
          sx={{ cursor: 'pointer' }}
        >
          {product.name}
        </Link>
        {renderRating}
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          {renderPrice}
        </Box>
      </Stack>
    </Card>
  );
}