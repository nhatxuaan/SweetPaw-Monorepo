const Product = require("../models/productModel");
const Order = require("../models/orderModel");

// Tạo user-item matrix cho 1 user
// const buildUserMatrix = async(userId) => {
//     // Lấy danh sách product theo đúng thứ tự cột khi training
//     const productColumns = await Product.find().sort({ _id: 1 });

//     const productIndex = {};
//     productColumns.forEach((p, i) => productIndex[p._id] = i);

//     // Tạo vector 0
//     const matrix = new Array(productColumns.length).fill(0);

//     // Lấy lịch sử mua hàng
//     const orders = await Order.find({ customer_id: userId });

//     orders.forEach(order => {
//         const idx = productIndex[order.product_id];
//         if (idx !== undefined) {
//             matrix[idx] += order.quantity;
//         }
//     });

//     return [matrix];  // phải trả về dạng 2D
// }

const buildUserMatrix = async(userId) => {
    const productColumns = await Product.find().sort({ _id: 1 });
    // console.log("product columns: ", productColumns)

    const productIndex = {};
    productColumns.forEach((p, i) => productIndex[p._id.toString()] = i);
    // console.log("product index: ", productIndex)

    const matrix = new Array(productColumns.length).fill(0);

    const orders = await Order.find({ user: userId });
    // console.log("user id: ", userId)
    // console.log("order : ", orders)

    orders.forEach(order => {
        order.items.forEach(item => {
            const idx = productIndex[item.productId.toString()];
            if (idx !== undefined) {
                matrix[idx] += item.quantity;
            }
        });
    });

    return [matrix];  // 2D array
}

module.exports = {
    buildUserMatrix,
};


module.exports = {
    buildUserMatrix,
};
