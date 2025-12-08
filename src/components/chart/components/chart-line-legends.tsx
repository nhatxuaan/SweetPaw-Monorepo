import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

type Props = {
  series: {
    name: string;
    data: number[];
  }[];
  colors: string[];
  sx?: any;
};

export function ChartLineLegends({ series, colors, sx }: Props) {
  return (
    <Stack direction="row" spacing={3} justifyContent="center" sx={sx}>
      {series.map((item, index) => (
        <Stack key={index} direction="row" alignItems="center" spacing={1}>
          <Box
            sx={{
              width: 12,
              height: 12,
              borderRadius: '50%',
              backgroundColor: colors[index],
            }}
          />
          <Typography variant="body2">{item.name}</Typography>
        </Stack>
      ))}
    </Stack>
  );
}
