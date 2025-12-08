// const Interaction = require("../models/InteractionModel");

// const logInteraction = async (data) => {
//   const { userId, productId, action, score, keyword } = data;

//   // score mặc định
//   let finalScore = score || 1;

//   // validate rating
//   if (action === "rate" && (!score || score < 1 || score > 5)) {
//     throw new Error("Rating score phải từ 1 đến 5");
//   }

//   const interaction = new Interaction({
//     userId,
//     productId: productId || null,
//     action,
//     score: finalScore,
//     metadata: { keyword: keyword || null },
//   });

//   return await interaction.save();
// };

// module.exports = { 
//     logInteraction 
// };
