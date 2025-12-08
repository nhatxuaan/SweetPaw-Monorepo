// const createVNDate = (year, month, day, hour = 0, min = 0, sec = 0) => {
//   // month: 0-11
//   // Tạo ngày VN trực tiếp bằng cách trừ 7 giờ trong UTC
//   return new Date(Date.UTC(year, month, day, hour, min, sec));
// };

const toUTC7 = (date) => {
  return new Date(date.getTime());
};


const getDateRange = (type) => {
  const now = new Date();
  // console.log("now:", now);
  console.log("VN now:", now.toLocaleString("vi-VN"));
  console.log("UTC now:", now.toISOString());

  let start, end;

  if (type === "day") {
    start = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0);
    end   = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59);
  }

  if (type === "month") {
    start = new Date(now.getFullYear(), now.getMonth(), 1);
    end   = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59);
  }

  if (type === "year") {
    start = new Date(now.getFullYear(), 0, 1);
    end   = new Date(now.getFullYear(), 11, 31, 23, 59, 59);
  }

  console.log("start:", start);
  console.log("end:", end);
  console.log("VN start:", start.toLocaleString("vi-VN"));
  console.log("VN end:", end.toLocaleString("vi-VN"));
  return { start, end };
};

const getPreviousPeriod = (start, end) => {
  const periodLength = end - start;

  const previousStart = new Date(start.getTime() - periodLength);
  const previousEnd = new Date(end.getTime() - periodLength);

  return { previousStart, previousEnd };
};

function vnHourToUTC(startUTC, hourVN) {
  const date = new Date(startUTC);
  date.setUTCHours(hourVN - 7, 0, 0, 0);
  return date;
}

function vnHourEndToUTC(startUTC, hourVN) {
  const date = new Date(startUTC);
  date.setUTCHours(hourVN - 7, 59, 59, 999);
  return date;
}





module.exports = {
  getDateRange,
  getPreviousPeriod, 
  toUTC7,
  // createVNDate,
  vnHourToUTC,
  vnHourEndToUTC,
};
