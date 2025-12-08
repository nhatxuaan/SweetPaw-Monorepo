const express = require("express");
const router = express.Router();
const { logInteractionController } = require("../controllers/interactionController");
const authMiddleware = require("../middlewares/authMiddleware");

// POST /interactions/log
router.post("/log", authMiddleware, logInteractionController);


module.exports = router;

