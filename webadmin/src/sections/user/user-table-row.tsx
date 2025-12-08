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

// ---------------------------------
// TYPE CUSTOMER (KHỚP 100% BACKEND)
// ---------------------------------
export interface UserProps {
  _id: string;
  HoTen: string;
  Email: string;
  SoDienThoai?: string;
  DiaChi?: Array<{
    TenDiaChi: string;
    SoNha: string;
    TenDuong: string;
    PhuongXa: string;
    QuanHuyen: string;
    ThanhPho: string;
    MacDinh: boolean;
  }>;
  totalSpent?: number;
}

interface UserTableRowProps {
  row: UserProps;
  selected: boolean;
  onSelectRow: () => void;
}

export function UserTableRow({ row, selected, onSelectRow }: UserTableRowProps) {
  const navigate = useNavigate();

  const [openPopover, setOpenPopover] = useState<HTMLButtonElement | null>(null);

  const handleOpenPopover = useCallback((event: React.MouseEvent<HTMLButtonElement>) => {
    setOpenPopover(event.currentTarget);
  }, []);

  const handleClosePopover = useCallback(() => {
    setOpenPopover(null);
  }, []);


  const handleEditCustomer = useCallback(
    (customer: UserProps) => {
      navigate(`/sweetpaw/user/${customer._id}/edit`, { state: customer });
    },
    [navigate]
  );


  const handleDeleteCustomer = useCallback(
    async (customerId: string) => {
      if (!window.confirm('Bạn có chắc chắn muốn xóa khách hàng này không?')) return;

      try {
        console.log("Deleting...", customerId);
        await new Promise((resolve) => setTimeout(resolve, 300));
        alert("Đã xóa khách hàng!");
        navigate('/sweetpaw/user');
      } catch (error) {
        console.error("Delete failed:", error);
        alert("Có lỗi xảy ra, vui lòng thử lại!");
      }
    },
    [navigate]
  );

  // -------------------------
  // TÍNH ĐỊA CHỈ MẶC ĐỊNH
  // -------------------------
  let defaultAddress = "Chưa có";

  if (row.DiaChi?.length) {
    const addr = row.DiaChi.find((a) => a.MacDinh) ?? row.DiaChi[0];

    if (addr) {
      defaultAddress = `${addr.SoNha} ${addr.TenDuong}, ${addr.PhuongXa}, ${addr.QuanHuyen}, ${addr.ThanhPho}`;
    }
  }

  return (
    <>
      <TableRow hover tabIndex={-1} role="checkbox" selected={selected}>
        <TableCell padding="checkbox">
          <Checkbox disableRipple checked={selected} onChange={onSelectRow} />
        </TableCell>

        <TableCell component="th" scope="row">
          {row.HoTen}
        </TableCell>

        <TableCell>{row.Email}</TableCell>

        <TableCell>{row.SoDienThoai || "—"}</TableCell>

        <TableCell sx={{ maxWidth: 320, overflow: 'hidden', textOverflow: 'ellipsis' }}>
          {defaultAddress}
        </TableCell>

        <TableCell align="right">
          {row.totalSpent ? row.totalSpent.toLocaleString() + "₫" : "—"}
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
              //handleEditCustomer(row._id);
              handleEditCustomer(row);
            }}
          >
            <Iconify icon="solar:pen-bold" />
            Chỉnh sửa
          </MenuItem>

          <MenuItem
            onClick={() => {
              handleClosePopover();
             // handleDeleteCustomer(row._id);
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
