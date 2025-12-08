import { useEffect, useState } from 'react';

import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';

import { DashboardContent } from 'src/layouts/dashboard';
import { getDashboard } from 'src/services/dashboardApi';

import { TopCustomers } from '../top-customers';
import { AnalyticsCurrentVisits } from '../analytics-current-visits';
import { AnalyticsWebsiteVisits } from '../analytics-website-visits';
import { AnalyticsWidgetSummary } from '../analytics-widget-summary';
import { AnalyticsConversionRates } from '../analytics-conversion-rates';

//----------------------------------------------------------------------

export function OverviewAnalyticsView() {
  const [data, setData] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);

  const loadDashboard = async (type: 'day' | 'month' | 'year' = 'year') => {
    try {
      setLoading(true);
      const dashboardData = await getDashboard(type); // <- đã là object data bên trong
      // thêm filter để biết đang xem theo day / month / year
      setData({ ...dashboardData, filter: type });
    } catch (err) {
      console.error('Lỗi API:', err);
      setData(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboard(); // mặc định 'year'
  }, []);

  if (loading) {
    return (
      <div
        style={{
          width: '100%',
          height: '80vh',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          fontSize: '20px',
          fontWeight: 'bold',
        }}
      >
        Đang tải...
      </div>
    );
  }

  if (!data) {
    return (
      <div
        style={{
          width: '100%',
          height: '80vh',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          fontSize: '20px',
          fontWeight: 'bold',
          color: 'red',
        }}
      >
        Lỗi tải dữ liệu!
      </div>
    );
  }


  // -------- REVENUE (doanh thu) --------
  const revenueSource =
    data.revenueByHours ?? // year: [{ month, revenue }]
    data.revenueByDays ?? // month: [{ day, revenue }]
    data.revenueByHoursDaily ?? // day: [{ hour, revenue }]
    [];

  const revenueCategories: string[] = revenueSource.map((row: any, index: number) => {
    if (row.month != null) return `Th ${row.month}`;
    if (row.day != null) return `Ng ${row.day}`;
    if (row.hour != null) return `${row.hour}h`;
    return `#${index + 1}`;
  });

  const revenueSeries: number[] = revenueSource.map((row: any) => row.revenue ?? 0);

  const monthlyRevenueWidget = {
    categories: revenueCategories,
    series: revenueSeries,
  };

  const monthlyRevenueLine = {
    categories: revenueCategories,
    series: [{ name: 'Doanh thu', data: revenueSeries }],
  };

  // -------- TRAFFIC (visits + orders) --------
  const trafficSource =
    data.trafficByMonths ?? // year
    data.trafficByDays ?? // month
    data.trafficByHours ?? // day
    data.trafficData ?? // trường hợp API cũ em gửi ở trên
    [];

  const trafficCategories: string[] = trafficSource.map((row: any, index: number) => {
    if (row.month != null) return `Th ${row.month}`;
    if (row.day != null) return `Ng ${row.day}`;
    if (row.hour != null) return `${row.hour}h`;
    return `#${index}`;
  });

  const trafficSeries = [
    {
      name: 'Lượt truy cập',
      data: trafficSource.map((row: any) => row.visits ?? 0),
    },
    {
      name: 'Đơn hàng',
      data: trafficSource.map((row: any) => row.orders ?? 0),
    },
  ];

  // -------- PIE CHART: doanh thu theo loại bánh --------
  const revenueByCategory =
    data.revenueByCategory?.map((item: any) => ({
      label: item.category,
      value: item.revenue,
    })) ?? [];


  return (
    <DashboardContent maxWidth="xl">
      <Typography variant="h4" sx={{ mb: { xs: 3, md: 5 } }}>
        Chào mừng trở lại! 😔
      </Typography>

      {/* Nút lọc theo thời gian */}
      <Stack direction="row" spacing={2} sx={{ mb: 3 }}>
        <Button
          variant={data.filter === 'day' ? 'contained' : 'outlined'}
          onClick={() => loadDashboard('day')}
        >
          Hôm nay
        </Button>

        <Button
          variant={data.filter === 'month' ? 'contained' : 'outlined'}
          onClick={() => loadDashboard('month')}
        >
          Tháng này
        </Button>

        <Button
          variant={data.filter === 'year' ? 'contained' : 'outlined'}
          onClick={() => loadDashboard('year')}
        >
          Năm nay
        </Button>
      </Stack>

      <Grid container spacing={3}>
        {/* ========== WIDGET SUMMARY ========== */}
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <AnalyticsWidgetSummary
            title="Doanh thu"
            percent={data.revenueGrowth}
            total={data.revenue}
            icon={<img alt="revenue" src="/assets/icons/glass/ic-glass-bag.svg" />}
            chart={monthlyRevenueWidget}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <AnalyticsWidgetSummary
            title="Khách hàng mới"
            percent={data.CustomerGrowth}
            total={data.newCustomers}
            color="secondary"
            icon={<img alt="users" src="/assets/icons/glass/ic-glass-users.svg" />}
            chart={{
              categories: [],
              series: [],
            }}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <AnalyticsWidgetSummary
            title="Đơn đặt hàng"
            percent={data.OrderGrowth}
            total={data.newOrders}
            color="warning"
            icon={<img alt="orders" src="/assets/icons/glass/ic-glass-buy.svg" />}
            chart={{
              categories: [],
              series: [],
            }}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <AnalyticsWidgetSummary
            title="Tin nhắn mới"
            percent={data.messageGrowth}
            total={data.newMessages}
            color="error"
            icon={<img alt="messages" src="/assets/icons/glass/ic-glass-message.svg" />}
            chart={{
              categories: [],
              series: [],
            }}
          />
        </Grid>

        {/* ========== PIE CHART ========== */}
        <Grid size={{ xs: 12, md: 6, lg: 4 }}>
          <AnalyticsCurrentVisits
            title="Doanh thu theo loại bánh"
            chart={{ series: revenueByCategory }}
          />
        </Grid>

        {/* ========== LINE CHART ========== */}
        <Grid size={{ xs: 12, md: 6, lg: 8 }}>
          <AnalyticsWebsiteVisits
            title="Doanh thu theo thời gian"
            subheader={
              data.filter === 'year'
                ? 'Theo tháng trong năm'
                : data.filter === 'month'
                  ? 'Theo ngày trong tháng'
                  : 'Theo giờ trong ngày'
            }
            chart={monthlyRevenueLine}
          />
        </Grid>

        {/* ========== LINE CHART 2 ========== */}
        <Grid size={{ xs: 12, md: 6, lg: 8 }}>
          <AnalyticsConversionRates
            title="Lượt truy cập ứng dụng và đơn hàng"
            subheader="Biểu đồ theo thời gian"
            chart={{
              categories: trafficCategories,
              series: trafficSeries,
            }}
          />
        </Grid>

        {/* ========== TOP CUSTOMERS ========== */}
        <Grid size={{ xs: 12, md: 6, lg: 4 }}>
          <TopCustomers title="Khách hàng nổi bật" list={data.topCustomers ?? []} />
        </Grid>
      </Grid>
    </DashboardContent>
  );
}
