const express = require('express')
const bodyParser = require('body-parser');

const asyncHandler = require("express-async-handler");

const app = express()
app.use(bodyParser.urlencoded({ extended: true }));

app.post('/users/:id/event', (req, res) => {
    const { id } = req?.params;
    const body = req?.body;
    console.log({id})
    console.log({body})


    res.send('eventEndPoint')
})


const handleUserEvent = (event) => {
    console.log(`received: ${event}`)
}

module.exports = {app}