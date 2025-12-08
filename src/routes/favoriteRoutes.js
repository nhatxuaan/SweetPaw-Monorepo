const express = require("express");
const favoriteController = require("../controllers/favoriteController");
const authMiddleware = require("../middlewares/authMiddleware"); 
const router = express.Router();


router.get("/", authMiddleware, favoriteController.getFavorites);

router.post("/toggle", authMiddleware, favoriteController.toggleFavorite);


module.exports = router;