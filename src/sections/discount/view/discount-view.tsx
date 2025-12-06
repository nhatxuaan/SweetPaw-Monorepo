import { useNavigate } from 'react-router-dom';
import { useState, useCallback, useEffect, ChangeEvent, MouseEvent } from 'react';

import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Table from '@mui/material/Table';
import Button from '@mui/material/Button';
import TableBody from '@mui/material/TableBody';
import Typography from '@mui/material/Typography';
import TableContainer from '@mui/material/TableContainer';
import TablePagination from '@mui/material/TablePagination';

import { Discount } from 'src/model/discount';
import { DashboardContent } from 'src/layouts/dashboard';
import { apiGetPromotion } from "src/services/discountApi";//apiGetDiscountsPromotion

import { Iconify } from 'src/components/iconify';
import { Scrollbar } from 'src/components/scrollbar';

import { TableNoData } from '../table-no-data';
import { TableEmptyRows } from '../table-empty-rows';
import { DiscountTableRow } from '../discount-table-row';
import { DiscountTableHead } from '../discount-table-head';
import { DiscountTableToolbar } from '../discount-table-toolbar';
import { emptyRows, applyFilter, getComparator } from '../utils';



export function DiscountView() {
  const navigate = useNavigate();
  const table = useTable();

  const [filterName, setFilterName] = useState('');
  const [discounts, setDiscounts] = useState<Discount[]>([]);
  const [loading, setLoading] = useState(true);


    // GET DATA FROM BACKEND
    useEffect(() => {
    const fetchDiscounts = async () => {
        try {
            const res = await apiGetPromotion();     // {status, data}
            setDiscounts(res);
        } catch (error: any) {
            alert(error.message || "Lỗi khi tải danh sách khuyến mãi!");
        } finally {
            setLoading(false);
        }
    };

    fetchDiscounts();
  }, []);

  // FILTER DATA
  const dataFiltered = applyFilter({
    inputData: discounts,
    comparator: getComparator(table.order, table.orderBy),
    filterName,
  });

  const notFound = !dataFiltered.length && !!filterName;

  const handleAddDiscount = useCallback(() => {
    navigate('/sweetpaw/discount/new/edit');
  }, [navigate]);

  return (
    <DashboardContent>
      <Box
        sx={{
          mb: 5,
          display: 'flex',
          alignItems: 'center',
        }}
      >
        <Typography variant="h4" sx={{ flexGrow: 1 }}>
          Khuyến mãi
        </Typography>

        <Button
          variant="contained"
          color="inherit"
          startIcon={<Iconify icon="mingcute:add-line" />}
          onClick={handleAddDiscount}
        >
          Thêm khuyến mãi mới
        </Button>
      </Box>

      <Card>
        <DiscountTableToolbar
          numSelected={table.selected.length}
          filterName={filterName}
          onFilterName={(event) => {
            setFilterName(event.target.value);
            table.onResetPage();
          }}
        />

        <Scrollbar>
          <TableContainer sx={{ overflow: 'unset' }}>
            <Table sx={{ minWidth: 800 }}>
              <DiscountTableHead
                order={table.order}
                orderBy={table.orderBy}
                rowCount={discounts.length}
                numSelected={table.selected.length}
                onSort={table.onSort}
                onSelectAllRows={(checked) =>
                  table.onSelectAllRows(
                    checked,
                    discounts.filter((d) => d && d._id).map((d) => d._id)
                  )
                }
                headLabel={[
                  { id: 'code', label: 'Mã code' },
                  { id: 'name', label: 'Tên khuyến mãi' },
                  { id: 'type', label: 'Loại' },
                  { id: 'quantity', label: 'Số lượng' },
                  { id: 'startDate', label: 'Ngày bắt đầu' },
                  { id: 'endDate', label: 'Ngày kết thúc' },
                  { id: 'isActive', label: 'Hiệu lưc', align: 'right'},
                  { id: '', width: 60 },
                ]}
              />

              <TableBody>
                {dataFiltered
                  .slice(
                    table.page * table.rowsPerPage,
                    table.page * table.rowsPerPage + table.rowsPerPage
                  )
                  .map((row) => (
                    <DiscountTableRow
                      key={row._id}
                      row={row}
                      selected={table.selected.includes(row._id)}
                      onSelectRow={() => table.onSelectRow(row._id)}
                    />
                  ))}

                <TableEmptyRows
                  height={68}
                  emptyRows={emptyRows(table.page, table.rowsPerPage, discounts.length)}
                />

                {notFound && <TableNoData searchQuery={filterName} />}
              </TableBody>
            </Table>
          </TableContainer>
        </Scrollbar>

        <TablePagination
          component="div"
          page={table.page}
          count={discounts.length}
          rowsPerPage={table.rowsPerPage}
          onPageChange={table.onChangePage}
          rowsPerPageOptions={[25, 50, 100]}
          onRowsPerPageChange={table.onChangeRowsPerPage}
        />
      </Card>
    </DashboardContent>
  );
}

// TABLE HOOK

export function useTable() {
  const [page, setPage] = useState(0);
  const [orderBy, setOrderBy] = useState('HoTen'); // default sort
  const [rowsPerPage, setRowsPerPage] = useState(25);
  const [selected, setSelected] = useState<string[]>([]);
  const [order, setOrder] = useState<'asc' | 'desc'>('asc');

  const onSort = useCallback(
    (id: string) => {
      const isAsc = orderBy === id && order === 'asc';
      setOrder(isAsc ? 'desc' : 'asc');
      setOrderBy(id);
    },
    [order, orderBy]
  );

  const onSelectAllRows = useCallback((checked: boolean, newSelecteds: string[]) => {
    setSelected(checked ? newSelecteds : []);
  }, []);

  const onSelectRow = useCallback(
    (inputValue: string) => {
      const newSelected = selected.includes(inputValue)
        ? selected.filter((value) => value !== inputValue)
        : [...selected, inputValue];
      setSelected(newSelected);
    },
    [selected]
  );

  const onResetPage = useCallback(() => setPage(0), []);

  const onChangePage = useCallback(
    (event: MouseEvent<HTMLButtonElement> | null, newPage: number) => {
      setPage(newPage);
    },
    []
  );

  const onChangeRowsPerPage = useCallback(
    (event: ChangeEvent<HTMLInputElement>) => {
      setRowsPerPage(parseInt(event.target.value, 10));
      onResetPage();
    },
    [onResetPage]
  );

  return {
    page,
    order,
    onSort,
    orderBy,
    selected,
    rowsPerPage,
    onSelectRow,
    onResetPage,
    onChangePage,
    onSelectAllRows,
    onChangeRowsPerPage,
  };
}
