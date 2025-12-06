import type { Order } from "src/model/order";

import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';

import Popover from '@mui/material/Popover';
import TableRow from '@mui/material/TableRow';
import Checkbox from '@mui/material/Checkbox';
import MenuList from '@mui/material/MenuList';
import TableCell from '@mui/material/TableCell';
import IconButton from '@mui/material/IconButton';
import MenuItem, { menuItemClasses } from '@mui/material/MenuItem';

import { Iconify } from 'src/components/iconify';

interface OrderTableRowProps {
  row: Order;
  selected: boolean;
  onSelectRow: () => void;
}


export function OrderTableRow({ row, selected, onSelectRow }: OrderTableRowProps) {
const navigate = useNavigate();

  const [openPopover, setOpenPopover] = useState<HTMLButtonElement | null>(null);

  const handleOpenPopover = useCallback((event: React.MouseEvent<HTMLButtonElement>) => {
    setOpenPopover(event.currentTarget);
  }, []);

  const handleClosePopover = useCallback(() => {
    setOpenPopover(null);
  }, []);

  const handleViewOrder = useCallback(
    () => {
      handleClosePopover();
      navigate(`/sweetpaw/order/${row._id}/view`, { state: row });
    },
    [navigate,row, handleClosePopover]
  );
 

  const handleUpdateOrder = useCallback(
    () => {
      handleClosePopover();
      navigate(`/sweetpaw/order/${row._id}/update`, { state: row });
    },
    [navigate, row, handleClosePopover]
  );

  // const handleEditDeliveryStatus = useCallback(
  //   (order: OrderProps) => {
  //     navigate(`/sweetpaw/order/${order._id}/editDeliveryStatus`, { state: order });
  //   },
  //   [navigate]
  // );

  // const handleEditPaymentStatus = useCallback(
  //   (order: OrderProps) => {
  //     navigate(`/sweetpaw/order/${order._id}/editPaymentStatus`, { state: order });
  //   },
  //   [navigate]
  // );
 
  return (
    <>
      <TableRow hover tabIndex={-1} role="checkbox" selected={selected}>
        <TableCell component="th" scope="row">
          {row.ghn_order_code}
        </TableCell>
        <TableCell>{row.to_name}</TableCell>
        <TableCell>{new Date(row.createdAt).toLocaleString("vi-VN")}</TableCell>
        <TableCell align="right">
          {row.total_price ? row.total_price.toLocaleString('vi-VN') + '₫' : '—'}
        </TableCell>        
        <TableCell>{row.status}</TableCell>
        <TableCell>{row.payment_method}</TableCell>
        <TableCell>{row.paymentStatus === "PENDING"? (
            <span style={{ color: "#ef4444", fontWeight: 600 }}>Chưa thanh toán</span>
            ) : (
                <span style={{ color: "#22c55e", fontWeight: 600 }}>Đã thanh toán</span>
            )} </TableCell>
        

        <TableCell align="right" onClick={(e) => e.stopPropagation()}>
          <IconButton onClick={handleOpenPopover}>
            <Iconify icon="eva:more-vertical-fill" />
          </IconButton>
        </TableCell>
      </TableRow>

        <Popover
        open={!!openPopover}
        anchorEl={openPopover}
        onClose={handleClosePopover}
        anchorOrigin={{ vertical: 'top', horizontal: 'left' }}
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
      >
        <MenuList
          disablePadding
          sx={{
            p: 0.5,
            gap: 0.5,
            width: 260,
            display: 'flex',
            flexDirection: 'column',
            [`& .${menuItemClasses.root}`]: {
              px: 1,
              gap: 2,
              borderRadius: 0.75,
              [`&.${menuItemClasses.selected}`]: { bgcolor: 'action.selected' },
            },
          }}
        >
         <MenuItem
            onClick={() => {
              handleClosePopover();
              handleUpdateOrder();
            }}
          >
            <Iconify icon="solar:pen-bold" />
            Cập nhật trạng thái đơn hàng
          </MenuItem>

          <MenuItem
            onClick={() => {
              handleClosePopover();
              handleViewOrder();
            }}
          >
            <Iconify icon="solar:eye-bold" />
            Xem chi tiết đơn hàng
          </MenuItem>
        </MenuList>
      </Popover>
   
  </>
  )
} 
