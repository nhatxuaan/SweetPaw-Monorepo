import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';

import Popover from '@mui/material/Popover';
import TableRow from '@mui/material/TableRow';
import Checkbox from '@mui/material/Checkbox';
import MenuList from '@mui/material/MenuList';
import TableCell from '@mui/material/TableCell';
import IconButton from '@mui/material/IconButton';
import MenuItem, { menuItemClasses } from '@mui/material/MenuItem';

import { Discount } from 'src/model/discount';
import { apiDeletePromotion } from 'src/services/discountApi';

import { Iconify } from 'src/components/iconify';




interface DiscountTableRowProps {
  row: Discount;
  selected: boolean;
  onSelectRow: () => void;
}

export function DiscountTableRow({ row, selected, onSelectRow }: DiscountTableRowProps) {
  const navigate = useNavigate();

  const [openPopover, setOpenPopover] = useState<HTMLButtonElement | null>(null);

  const handleOpenPopover = useCallback((event: React.MouseEvent<HTMLButtonElement>) => {
    setOpenPopover(event.currentTarget);
  }, []);

  const handleClosePopover = useCallback(() => {
    setOpenPopover(null);
  }, []);



  const handleEditDiscount = useCallback(() => {
    navigate(`/sweetpaw/discount/${row._id}/edit`, { state: row });
  }, [navigate, row]);

  // const handleDeleteDiscount = useCallback(
  //   async () => {
  //   if (!window.confirm('Bạn có chắc chắn muốn xóa khuyến mãi này không?')) return;

  //   try {
  //     const res = await apiDeletePromotion(row._id);

  //     alert(res.message || "Xóa thành công!");
  //     navigate('/sweetpaw/discount');
      
  //   } catch (error) {
  //     console.error("Delete failed:", error);
  //     alert("Có lỗi xảy ra, vui lòng thử lại!");
  //   }
  // },
  // [navigate, row._id]
  // );
  const handleDeleteDiscount = useCallback(
  async () => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa khuyến mãi này không?')) return;

    try {
      const res = await apiDeletePromotion(row._id);

      alert(res?.message || "Đã xoá thành công");
      window.location.reload();
    } catch (error: any) {
      console.error("Delete failed:", error);

      // BE gửi message trong error.response.data.message
      const msg =
        error?.response?.data?.message ||
        "Có lỗi xảy ra, vui lòng thử lại!";

      alert(msg);
    }
  },
  [navigate, row._id]
);



function formatDate(dateString: string) {
  // bỏ Z để tránh chuyển về UTC/local
  const clean = dateString.replace("Z", "");

  const [datePart, timePart] = clean.split("T");
  const [year, month, day] = datePart.split("-");
  const [hour, minute] = timePart.split(":");

  return `${day}/${month}/${year} ${hour}:${minute}`;
}



  return (
    <>
      <TableRow hover tabIndex={-1} role="checkbox" selected={selected}>
        <TableCell padding="checkbox">
          <Checkbox disableRipple checked={selected} onChange={onSelectRow} />
        </TableCell>

        <TableCell component="th" scope="row">
          {row.code}
        </TableCell>

        <TableCell>{row.name}</TableCell>

        <TableCell>{row.type}</TableCell>
        <TableCell>{row.quantity} </TableCell>
        <TableCell>{formatDate(row.startDate)}</TableCell>
        <TableCell>{formatDate(row.endDate)}</TableCell>

        <TableCell align="right"> 
            {row.isActive ? (
            <span style={{ color: "#22c55e", fontWeight: 600 }}>Còn</span>
            ) : (
                <span style={{ color: "#ef4444", fontWeight: 600 }}>Hết hạn</span>
            )} 
        </TableCell>

        <TableCell align="right">
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
            width: 140,
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
              handleEditDiscount();
            }}
          >
            <Iconify icon="solar:pen-bold" />
            Chỉnh sửa
          </MenuItem>

          <MenuItem
            onClick={() => {
              handleClosePopover();
              handleDeleteDiscount();
            }}
            sx={{ color: 'error.main' }}
          >
            <Iconify icon="solar:trash-bin-trash-bold" />
            Xóa
          </MenuItem>
        </MenuList>
      </Popover>
    </>
  );
}
