import { ReactNode } from 'react';

type Props = {
  children: ReactNode;
};

export function DashboardChatContent({ children }: Props) {
  return (
    <div
      style={{
        width: "100%",
        height: "100%",      // ✔ Không còn 100vh → Không gây scroll ngoài
        minHeight: 0,
        overflow: "hidden",  // ✔ Ngăn cuộn toàn trang
        display: "flex",
        flexDirection: "column",
        background: "#f4f6f8",
      }}
    >
      {/* MAIN CONTENT */}
      <div style={{ flex: 1, overflow: "hidden" }}>
        {children}
      </div>
    </div>
  );
}
