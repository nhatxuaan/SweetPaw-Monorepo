import type { CardProps } from '@mui/material/Card';
import type { ChartOptions } from 'src/components/chart';

import Card from '@mui/material/Card';
import { useTheme } from '@mui/material/styles';
import CardHeader from '@mui/material/CardHeader';

import { Chart, useChart } from 'src/components/chart';
import { ChartLineLegends } from "src/components/chart/components/chart-line-legends";

type Props = CardProps & {
  title?: string;
  subheader?: string;
  chart: {
    categories: string[];
    series: {
      name: string;
      data: number[];
    }[];
    options?: ChartOptions;
  };
};

export function AnalyticsConversionRates({ title, subheader, chart, sx, ...other }: Props) {
  const theme = useTheme();

  const chartOptions = useChart({
    chart: {
      type: 'line',
      toolbar: { show: false },
    },
    stroke: {
      width: 3,
      curve: 'smooth',
    },
    markers: {
      strokeWidth: 2,
    },
    xaxis: {
      categories: chart.categories,
      title: {
        text: '',
        style: { fontSize: '13px', fontWeight: 500 },
      },
    },
    yaxis: {
      title: {
        text: 'Số lượng',
        style: { fontSize: '13px', fontWeight: 500 },
      },
      min: 0,
    },
    tooltip: {
      shared: true,
      intersect: false,
    },
    legend: {
      position: 'bottom',
      markers: { 
        size: 8,           // size của chấm
        strokeWidth: 0,    // bỏ viền }
      }
    },
    colors: [
      theme.palette.primary.main,      // line 1
      theme.palette.warning.main,      // line 2
    ],
    ...chart.options,
  });

  return (
    <Card sx={sx} {...other}>
      <CardHeader title={title} subheader={subheader} />

      <Chart
        type="line"
        series={chart.series}
        options={chartOptions}
        sx={{
          pl: 1,
          py: 2.5,
          pr: 2.5,
          height: 360,
        }}
      />
      <ChartLineLegends
          series={chart.series}
          colors={chartOptions?.colors as string[]}
          sx={{ p: 2, justifyContent: 'center' }}
      />
    </Card>
  );
}
