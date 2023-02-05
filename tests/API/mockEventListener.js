const express = require("express");
const asyncHandler = require("express-async-handler");
const { eventHandler } = require("./eventHandler");

const app = express();
app.use(express.json());

const PORT = 3003;

app.post(
  "/users/:id/event",
  asyncHandler(async (req, res) => {
    const { id } = req?.params;
    eventHandler(req?.body?.event);
    res.json({});
  })
);

module.exports = { app };
