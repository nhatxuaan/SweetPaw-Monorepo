const nodemailer = require("nodemailer");

// Tạo transporter để gửi email
const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: process.env.MAIL_USER,
    pass: process.env.MAIL_PASS          
  }
});

module.exports = transporter;
