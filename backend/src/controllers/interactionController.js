 const { logInteraction } = require("../services/interactionService");

const logInteractionController = async (req, res) => {
  try {
    const interaction = await logInteraction(req.body);
    return res.status(201).json({ message: "Logged successfully", interaction });
  } catch (err) {
    console.error(err);
    return res.status(400).json({ message: err.message });
  }
};

module.exports = { logInteractionController };

