const axiosLib = require("axios");
const fs = require("fs");

const WILCO_ID = process.env.WILCO_ID || fs.readFileSync('../.wilco', 'utf8')

const baseURL = 'https://engine.wilco.gg';
const axios = axiosLib.create({
  baseURL: baseURL,
  headers: {
    'Content-type': 'application/json',
  },
});

async function sendEvent(event, metadata) {
  if (process.env.APP_ENV === 'TEST') {
    return
  }
  const result = await axios.post(`/users/${WILCO_ID}/event`, JSON.stringify({ event, metadata }));
  return result.data;
}

module.exports = {
  sendEvent,
}
