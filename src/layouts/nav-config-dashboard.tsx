import { Label } from 'src/components/label';
import { SvgColor } from 'src/components/svg-color';

// ----------------------------------------------------------------------

const icon = (name: string) => <SvgColor src={`/assets/icons/navbar/${name}.svg`} />;

export type NavItem = {
  title: string;
  path: string;
  icon: React.ReactNode;
  info?: React.ReactNode;
};

export const navData = [
  {
    title: 'Dashboard',
    path: '/sweetpaw',
    icon: icon('ic-analytics'),
  },
  {
    title: 'Khách hàng',
    path: '/sweetpaw/user',
    icon: icon('ic-user'),
  },
  {
    title: 'Sản phẩm',
    path: '/sweetpaw/products',
    icon: icon('ic-cart'),
  },
  {
    title: 'Đơn hàng',
    path: '/sweetpaw/order',
    icon: icon('ic-order'),
  },
   {
    title: 'Khuyến mãi',
    path: '/sweetpaw/discount',
    icon: icon('ic-discount'),
  },
  {
    title: 'Tin nhắn',
    path: '/sweetpaw/messages',
    icon: icon('ic-chat'),
    //   info: (
    //   <Label color="error" variant="inverted">
    //     +3
    //   </Label>
    // ),
  },
  {
    title: 'Sign in',
    path: '/sign-in',
    icon: icon('ic-lock'),
  },
  {
    title: 'Not found',
    path: '/sweetpaw/404',
    icon: icon('ic-disabled'),
  },
];
