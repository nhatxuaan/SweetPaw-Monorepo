import Card from '@mui/material/Card';
import Table from '@mui/material/Table';
import TableRow from '@mui/material/TableRow';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import CardHeader from '@mui/material/CardHeader';
import Typography from '@mui/material/Typography';

type Props = {
  title?: string;
  list: {
    name: string;
    totalSpent: number;
  }[];
};

export function TopCustomers({ title = "Khách hàng nổi bật", list }: Props) {
  return (
    <Card>
      <CardHeader title={title} />

      <Table sx={{ minWidth: 360, p: 2 }}>
        <TableHead>
          <TableRow>
            <TableCell sx={{ fontWeight: 600 }}>Khách hàng</TableCell>
            <TableCell sx={{ fontWeight: 600 }} align="right">
              Chi tiêu
            </TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {list.map((item, idx) => (
            <TableRow key={idx}>
              <TableCell>{item.name}</TableCell>

              <TableCell align="right">
                <Typography fontWeight={500}>
                  {item.totalSpent.toLocaleString("vi-VN")}đ
                </Typography>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </Card>
  );
}
