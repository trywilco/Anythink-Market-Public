const express = require("express");
const asyncHandler = require("express-async-handler");
const { eventHandler } = require("./eventHandler");

const app = express();
app.use(express.json());

app.post(
  "/users/:id/event",
  asyncHandler(async (req, res) => {
    eventHandler(req?.body?.event);
    res.json({});
  })
);

module.exports = { app };
