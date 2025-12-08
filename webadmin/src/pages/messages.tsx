import { _posts } from 'src/_mock';
import { CONFIG } from 'src/config-global';

import { MessagesView } from 'src/sections/blog/view';

// ----------------------------------------------------------------------

export default function Page() {
  return (
    <>
      <title>{`Tin nhắn - ${CONFIG.appName}`}</title>

      <MessagesView />
    </>
  );
}
