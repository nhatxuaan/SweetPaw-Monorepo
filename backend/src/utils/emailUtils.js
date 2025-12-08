const transporter = require('../config/mail');

/**
 * Hàm gửi email bằng transporter (gmail)
 * @param {string} to - Email người nhận
 * @param {string} subject - Tiêu đề email
 * @param {string} text - Nội dung email dạng text (bắt buộc)
 * @param {string} [html] - Nội dung HTML (tùy chọn, nếu muốn gửi mail có format)
 */
async function sendEmail(to, subject, text) {
  try {
    const mailOptions = {
      from: process.env.MAIL_USER, 
      to: to,
      subject: subject,
      text: text
    };

    const info = await transporter.sendMail(mailOptions);
    console.log("Email sent:", info.response);
    return true;
  } catch (error) {
    console.error("Lỗi khi gửi email:", error);
    return false;
  }
}

module.exports = sendEmail;
