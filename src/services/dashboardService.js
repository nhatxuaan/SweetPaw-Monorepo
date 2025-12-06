const {default: ApiError} = require("../utils/ApiError");
const { totalRevenue, calculateGrowth, countNewCustomers, appVisits, countNewMessages, countNewOrders, revenueByCategory, topCustomers } = require("../utils/dashboardHelper");
const { getPreviousPeriod, createVNDate, toUTC7, vnHourToUTC, vnHourEndToUTC } = require("../utils/dateHelper");
const getDashboardService = async ({ type, start, end }) => {
  // Implement the logic to fetch and compute dashboard data based on type, start, and end
  // This is a placeholder implementation 
    let revenue = 0;
    let revenueGrowth = 0;
    let newCustomers = 0;
    let CustomerGrowth = 0;
    let newOrders = 0;
    let OrderGrowth = 0;
    let newMessages = 0;
    let messageGrowth = 0;
    const revenueByHours = [];
    const revenueByCategoryData = [];
    const categories = ["Bánh kem", "Bánh mì", "Bánh mini", "Bánh ngọt", "Đồ uống", "Bánh quy"];
    const trafficData = [];
    const topCustomerData = [];

    // Chuyển đổi thời gian sang UTC + 7 (giờ Việt Nam)
    
    const startUTC = toUTC7(start);
    const endUTC = toUTC7(end);

    if (type === "day") {



        console.log("Thời gian startUTC:", startUTC);
        console.log("Thời gian endUTC:", endUTC);

        // 1. overview hiển thị tổng doanh thu theo type, đơn hàng, khách hàng mới, tin nhắn
        //a. doanh thu tổng
        revenue = await totalRevenue(startUTC, endUTC);

        // Tăng trưởng doanh thu so với kỳ trước
        const { previousStart, previousEnd } = getPreviousPeriod(startUTC, endUTC);
        const previousRevenue = await totalRevenue( previousStart, previousEnd);
        console.log("Doanh thu kỳ trước:", previousRevenue);
        revenueGrowth = calculateGrowth(revenue, previousRevenue);

        //b. khách hàng mới
        newCustomers = await countNewCustomers(startUTC, endUTC);
        const previousNewCustomers = await countNewCustomers( previousStart, previousEnd);
        CustomerGrowth = calculateGrowth(newCustomers, previousNewCustomers);
        //c. đơn hàng mới
        newOrders = await countNewOrders(startUTC, endUTC);
        const previousNewOrders = await countNewOrders( previousStart, previousEnd);
        console.log("Đơn hàng kỳ trước:", previousNewOrders);
        OrderGrowth = calculateGrowth(newOrders, previousNewOrders);
        //d. tin nhắn mới
        newMessages = await countNewMessages(startUTC, endUTC);
        const previousNewMessages = await countNewMessages( previousStart, previousEnd);
        messageGrowth = calculateGrowth(newMessages, previousNewMessages);

        // 2. biểu đồ doanh thu theo ngày trong tuần/ tháng/ năm

        // Lấy số ngày trong khoảng từ start đến end
        for (let hour = 0; hour < 24; hour++) {
        const hourStart = vnHourToUTC(startUTC, hour);
        const hourEnd = vnHourEndToUTC(startUTC, hour);
        const hourlyRevenue = await totalRevenue(hourStart, hourEnd);

        revenueByHours.push({
            hour: hour,
            revenue: hourlyRevenue,
        });
        }

        // 3. doanh thu theo loại bánh 

        const tempArray = [];

        for (const category of categories) {
            const categoryRevenue = await revenueByCategory(category, startUTC, endUTC  );
            console.log(`Doanh thu loại ${category}:`, categoryRevenue);
            tempArray.push({
                category,
                revenue: categoryRevenue,
            });
        }

        // Tính phần trăm doanh thu theo loại bánh
        const totalCategoryRevenue = tempArray.reduce((sum, item) => sum + item.revenue, 0);
        for (const item of tempArray) {
            const percentage = totalCategoryRevenue === 0 ? 0 : (item.revenue / totalCategoryRevenue) * 100;
            revenueByCategoryData.push({
                category: item.category,
                revenue: item.revenue,
                percentage: percentage.toFixed(2), // giữ 2 chữ số thập phân
            });
        }
        
        // 4. biểu đồ lưu lượng truy cập app và đặt hàng trong thời gian type
        for (let hour = 0; hour < 24; hour++) {
            const hourStart = vnHourToUTC(startUTC, hour);
            const hourEnd = vnHourEndToUTC(startUTC, hour);
            // Doanh thu theo giờ
            const hourlyRevenue = await totalRevenue(hourStart, hourEnd);

            // Traffic theo giờ
            const hourlyVisits = await appVisits(hourStart, hourEnd);

            // Đơn theo giờ
            const hourlyOrders = await countNewOrders(hourStart, hourEnd);

            trafficData.push({
                hour,
                visits: hourlyVisits,
                orders: hourlyOrders,
            });
        }

        // 5. top khách hàng mua nhiều
        topCustomerData.push(...await topCustomers(startUTC, endUTC));
    }

    else if (type === "month") {


        console.log("Thời gian startUTC:", startUTC);
        console.log("Thời gian endUTC:", endUTC);

        // 1. overview hiển thị tổng doanh thu theo type, đơn hàng, khách hàng mới, tin nhắn
        //a. doanh thu tổng
        revenue = await totalRevenue(startUTC, endUTC);
        //Tăng trưởng doanh thu so với kỳ trước
        const { previousStart, previousEnd } = getPreviousPeriod(startUTC, endUTC);
        const previousRevenue = await totalRevenue( previousStart, previousEnd);
        console.log("Doanh thu kỳ trước:", previousRevenue);
        revenueGrowth = calculateGrowth(revenue, previousRevenue);
        //b. khách hàng mới
        newCustomers = await countNewCustomers(startUTC, endUTC);
        const previousNewCustomers = await countNewCustomers( previousStart, previousEnd);
        CustomerGrowth = calculateGrowth(newCustomers, previousNewCustomers);
        //c. đơn hàng mới
        newOrders = await countNewOrders(startUTC, endUTC);
        const previousNewOrders = await countNewOrders( previousStart, previousEnd);
        console.log("Đơn hàng kỳ trước:", previousNewOrders);
        OrderGrowth = calculateGrowth(newOrders, previousNewOrders);
        //d. tin nhắn mới
        newMessages = await countNewMessages(startUTC, endUTC);
        const previousNewMessages = await countNewMessages( previousStart, previousEnd);
        messageGrowth = calculateGrowth(newMessages, previousNewMessages);

        // 2. biểu đồ doanh thu theo ngày trong tuần/ tháng/ năm

        // Lấy số ngày trong khoảng từ start đến end
        const dayCount = Math.ceil((endUTC - startUTC) / (1000 * 60 * 60 * 24));
        // for (let day = 0; day < dayCount; day++) {
        //     const currentDay = vnHourToUTC(startUTC, day);
        //     const dayStart = vnHourToUTC(currentDay, 0);
        //     const dayEnd = vnHourEndToUTC(currentDay, 23);
        //     const dailyRevenue = await totalRevenue(dayStart, dayEnd);
        //     revenueByHours.push({
        //         day: day + 1,
        //         revenue: dailyRevenue,
        //     });
        // }

        // ------------------------------------------------------------------
        // for (let day = 0; day < dayCount; day++) {

        //     const current = new Date(startUTC);
        //     current.setUTCDate(current.getUTCDate() + day);

        //     const dayStart = new Date(current);
        //     dayStart.setUTCHours(17, 0, 0, 0); // = 00:00 VN

        //     const dayEnd = new Date(current);
        //     dayEnd.setUTCHours(16, 59, 59, 999); // = 23:59 VN

        //     const dailyRevenue = await totalRevenue(dayStart, dayEnd);

        //     revenueByHours.push({
        //         day: day + 1,
        //         revenue: dailyRevenue,
        //     });
        // }

        for (let day = 0; day < dayCount; day++) {

            // Ngày gốc từ VN
            const vn = new Date(startUTC); 
            vn.setDate(vn.getDate() + day);
        
            // Tạo 00:00 VN
            const vnStart = new Date(vn);
            vnStart.setHours(0, 0, 0, 0);
        
            // Tạo 23:59 VN
            const vnEnd = new Date(vn);
            vnEnd.setHours(23, 59, 59, 999);
        
            // Convert sang UTC chuẩn
            const dayStart = new Date(vnStart.getTime() - 7 * 3600 * 1000);
            const dayEnd   = new Date(vnEnd.getTime()   - 7 * 3600 * 1000);
        
            const dailyRevenue = await totalRevenue(dayStart, dayEnd);
        
            revenueByHours.push({
                day: day + 1,
                revenue: dailyRevenue,
            });
        }
        

        // 3. doanh thu theo loại bánh

        const tempArray = [];
        for (const category of categories) {
            const categoryRevenue = await revenueByCategory(category, startUTC, endUTC);
            console.log(`Doanh thu loại ${category}:`, categoryRevenue);
            tempArray.push({
                category,
                revenue: categoryRevenue,
            });
        }
        // Tính phần trăm doanh thu theo loại bánh
        const totalCategoryRevenue = tempArray.reduce((sum, item) => sum + item.revenue, 0);
        for (const item of tempArray) {
            const percentage = totalCategoryRevenue === 0 ? 0 : (item.revenue / totalCategoryRevenue) * 100;
            revenueByCategoryData.push({
                category: item.category,
                revenue: item.revenue,
                percentage: percentage.toFixed(2), // giữ 2 chữ số thập phân
            });
        }
        // 4. biểu đồ lưu lượng truy cập app và đặt hàng trong thời gian type
        for (let day = 0; day < dayCount; day++) {
            const currentDay = new Date(startUTC);
            currentDay.setDate(currentDay.getDate() + day);
            const dayStart = new Date(currentDay);
            dayStart.setHours(0, 0, 0, 0);
            const dayEnd = new Date(currentDay);
            dayEnd.setHours(23, 59, 59, 999);
            // Doanh thu theo ngày
            const dailyRevenue = await totalRevenue(dayStart, dayEnd);
            // Traffic theo ngày
            const dailyVisits = await appVisits(dayStart, dayEnd);
            // Đơn theo ngày
            const dailyOrders = await countNewOrders(dayStart, dayEnd);
            trafficData.push({
                day: day + 1,
                visits: dailyVisits,
                orders: dailyOrders,
            });
        }
        // 5. top khách hàng mua nhiều
        topCustomerData.push(...await topCustomers(startUTC, endUTC));
        

    }
    else if (type === "year") {


        console.log("Thời gian startUTC:", startUTC);
        console.log("Thời gian endUTC:", endUTC);
        
        //1. overview hiển thị tổng doanh thu theo type, đơn hàng, khách hàng mới, tin nhắn
        //a. doanh thu tổng
        revenue = await totalRevenue(startUTC, endUTC);
        //Tăng trưởng doanh thu so với kỳ trước
        const { previousStart, previousEnd } = getPreviousPeriod(startUTC, endUTC);
        const previousRevenue = await totalRevenue( previousStart, previousEnd);
        console.log("Doanh thu kỳ trước:", previousRevenue);
        revenueGrowth = calculateGrowth(revenue, previousRevenue);
        //b. khách hàng mới
        newCustomers = await countNewCustomers(startUTC, endUTC);
        const previousNewCustomers = await countNewCustomers( previousStart, previousEnd);
        CustomerGrowth = calculateGrowth(newCustomers, previousNewCustomers);
        //c. đơn hàng mới
        newOrders = await countNewOrders(startUTC, endUTC);
        const previousNewOrders = await countNewOrders( previousStart, previousEnd);
        console.log("Đơn hàng kỳ trước:", previousNewOrders);
        OrderGrowth = calculateGrowth(newOrders, previousNewOrders);
        //d. tin nhắn mới
        newMessages = await countNewMessages(startUTC, endUTC);
        const previousNewMessages = await countNewMessages( previousStart, previousEnd);
        messageGrowth = calculateGrowth(newMessages, previousNewMessages);
        // 2. biểu đồ doanh thu theo ngày trong tuần/ tháng/ năm

        // Lấy số tháng trong khoảng từ start đến end 
        for (let month = 0; month < 12; month++) {
            const monthStart = new Date(startUTC.getFullYear(), month, 1);
            const monthEnd = new Date(start.getFullYear(), month + 1, 0, 23, 59, 59, 999);
            const monthlyRevenue = await totalRevenue(monthStart, monthEnd);
            revenueByHours.push({
                month: month + 1,
                revenue: monthlyRevenue,
            });
        }
        // 3. doanh thu theo loại bánh
        const tempArray = [];
        for (const category of categories) {
            const categoryRevenue = await revenueByCategory(category, startUTC, endUTC);
            console.log(`Doanh thu loại ${category}:`, categoryRevenue);
            tempArray.push({
                category,
                revenue: categoryRevenue,
            });
        }
        // Tính phần trăm doanh thu theo loại bánh
        const totalCategoryRevenue = tempArray.reduce((sum, item) => sum + item.revenue, 0);
        for (const item of tempArray) {
            const percentage = totalCategoryRevenue === 0 ? 0 : (item.revenue / totalCategoryRevenue) * 100;
            revenueByCategoryData.push({
                category: item.category,
                revenue: item.revenue,
                percentage: percentage.toFixed(2), // giữ 2 chữ số thập phân
            });
        }
        // 4. biểu đồ lưu lượng truy cập app và đặt hàng trong thời gian type
        for (let month = 0; month < 12; month++) {
            const monthStart = new Date(startUTC.getFullYear(), month, 1);
            const monthEnd = new Date(startUTC.getFullYear(), month + 1, 0, 23, 59, 59, 999);
            // Doanh thu theo tháng
            const monthlyRevenue = await totalRevenue(monthStart, monthEnd);
            // Traffic theo tháng
            const monthlyVisits = await appVisits(monthStart, monthEnd);
            // Đơn theo tháng
            const monthlyOrders = await countNewOrders(monthStart, monthEnd);
            trafficData.push({
                month: month + 1,
                visits: monthlyVisits,
                orders: monthlyOrders,
            });
        }
        // 5. top khách hàng mua nhiều
        topCustomerData.push(...await topCustomers(startUTC, endUTC ));
    }

    return {
        revenue: revenue, // doanh thu
        revenueGrowth: revenueGrowth, // tăng trưởng doanh thu
        newCustomers: newCustomers, // khách hàng mới
        CustomerGrowth: CustomerGrowth, // tăng trưởng khách hàng
        newOrders: newOrders, // đơn hàng mới
        OrderGrowth: OrderGrowth, // tăng trưởng đơn hàng
        newMessages: newMessages, // tin nhắn mới
        messageGrowth: messageGrowth, // tăng trưởng tin nhắn
        revenueByHours: revenueByHours, // doanh thu theo ngày
        revenueByCategory: revenueByCategoryData, // doanh thu theo loại bánh
        trafficData: trafficData, // lưu lượng truy cập app
        topCustomers: topCustomerData, // top khách hàng mua nhiều

    }
}

module.exports = {
  getDashboardService,
};