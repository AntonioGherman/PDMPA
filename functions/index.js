const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.onProductStockChange = functions.firestore
  .document("products/{productId}")
  .onUpdate(async (change, context) => {

    const before = change.before.data();
    const after = change.after.data();

    if (!before || !after) return null;

    const oldQty = before.quantity ?? 0;
    const newQty = after.quantity ?? 0;
    const minStock = after.minStock ?? 0;

    let newStatus = "OK";

    if (newQty <= 0) {
      newStatus = "CRITICAL";
    } else if (newQty < minStock) {
      newStatus = "WARNING";
    }

    if (before.stockStatus === newStatus) {
      return null;
    }

    await change.after.ref.update({
      stockStatus: newStatus
    });

    let title = "";
    let body = "";

    if (newStatus === "CRITICAL") {
      title = "🚨 Critical stock alert";
      body = `${after.name} is out of stock!`;
    } else if (newStatus === "WARNING") {
      title = "⚠️ Stock running low";
      body = `${after.name} is below minimum stock.`;
    } else {
      return null;
    }

    const message = {
      notification: {
        title,
        body
      },
      topic: "stock-alerts"
    };

    await admin.messaging().send(message);

    return null;
  });
