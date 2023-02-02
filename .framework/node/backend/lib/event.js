const axiosLib = require("axios");
const fs = require("fs");

const WILCO_ID = process.env.WILCO_ID || fs.readFileSync('../.wilco', 'utf8')

const baseURL = 'http://localhost:3003/';
const axios = axiosLib.create({
  baseURL: baseURL,
  headers: {
    'Content-type': 'application/json',
  },
});

async function sendEvent(event, metadata) {
  try {
    const result = await axios.get(`/hello`, JSON.stringify({event, metadata}));
    // const result = await axios.post(`/users/${WILCO_ID}/event`, JSON.stringify({event, metadata}));
    return result?.data;
  } catch (e) {
    // pass
  }
}

module.exports = {
  sendEvent,
}
