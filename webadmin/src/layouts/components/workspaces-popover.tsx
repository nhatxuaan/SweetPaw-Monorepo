import type { ButtonBaseProps } from '@mui/material/ButtonBase';

import { useState } from 'react';
import { varAlpha } from 'minimal-shared/utils';

import Box from '@mui/material/Box';


// ----------------------------------------------------------------------

export type WorkspacesPopoverProps = ButtonBaseProps & {
  data?: {
    id: string;
    name: string;
    logo: string;
  }[];
};

export function WorkspacesPopover({ data = [], sx, ...other }: WorkspacesPopoverProps) {
  const [workspace] = useState(data[0]);

  return (
    <Box
      sx={{
        pl: 2,
        py: 3,
        gap: 1.5,
        pr: 1.5,
        width: 1,
        borderRadius: 1.5,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        ...sx,
      }}
    >
      {renderAvatar(workspace?.name, workspace?.logo)}

      <Box
        sx={{
          gap: 1,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          typography: 'body2',
          fontSize: '20px',
          fontWeight: 'fontWeightSemiBold',
        }}
      >
        {workspace?.name}
      </Box>

      
    </Box>
  );
}

// Helper function
const renderAvatar = (alt: string, src: string) => (
  <Box component="img" alt={alt} src={src} sx={{ width: 64, height: 64}} />
);
