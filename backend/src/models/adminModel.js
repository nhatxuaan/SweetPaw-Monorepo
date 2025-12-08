const mongoose = require("mongoose");
const bcrypt = require("bcrypt");

const adminSchema = new mongoose.Schema({
    username: { type: String, required: true, unique: true },
    password: { type: String, required: true },
    email: { type: String, required: true, unique: true },
    displayName: { type: String, default: "SweetPaw" },
    role: { type: String, enum: ['superadmin', 'admin', 'moderator'], default: 'admin' },
    lastLogin: { type: Date },
}, { timestamps: true });

// hash mật khẩu
adminSchema.pre("save", async function (next) {
  if (!this.isModified("password")) return next();
  const salt = await bcrypt.genSalt(10);
  this.password = await bcrypt.hash(this.password, salt);
  next();
});

// so sánh mật khẩu
adminSchema.methods.comparePassword = function (password) {
    return bcrypt.compare(password, this.password);
};

const Admin = mongoose.model("Admin", adminSchema, "ADMIN");

module.exports = Admin;
