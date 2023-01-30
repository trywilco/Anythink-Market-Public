require("dotenv").config();
const axios = require("axios");
const assert = require('assert');

const createUser = async (client,user) => {
    return  await client.post(`/api/users`, {user});
};

const testUser = async () => {
    const client = axios.create({
            baseURL: `http://localhost:3000`,
            timeout: 10 * 1000,
        }
    );
    const username = `user${(Math.random() + 1).toString(36).substring(7)}`
    const user = {
        username: username,
            email: `${username}@test.work`,
            password: "wilco1234",
    }
    const response = await createUser(client,user);
    assert(response.data.user.username === user.username , "User created with bad username ")
    assert(response.data.user.email === user.email , "User created with bad email ")
    assert(!!response.data.user.token , "User created with bad token ")
    return true
};

testUser()
    .then((res) => process.exit(res ? 0 : 1))
    .catch((e) => {
        console.log("error while checking api: " + e);
        process.exit(1);
    });