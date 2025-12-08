const admin = require("firebase-admin");
const serviceAccount = require("../../serviceAccountKey.json"); // thẳng luôn

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
});

console.log("Firebase Admin đã khởi tạo thành công!");

module.exports = admin;
