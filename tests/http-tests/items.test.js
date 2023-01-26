require("dotenv").config();
const axios = require("axios");
const assert = require('assert');

const createUser = async (client,user) => {
    return await client.post(`/api/users`, {user});
};


const createItem = async (client, item) => {
    const itemRes = await client.post(`/api/items`, {item});
    return itemRes.data?.item;
};

const testItem = async () => {
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

    client.defaults.headers.common["Authorization"] = `Token ${response.data.user.token}`;
    const itemToCreate = {
            title: "title2555",
            description: "description",
            tag_list: ["tag1"],
    };
    const itemResponse = await createItem(client, itemToCreate);

    const getItem = await client.get(`/api/items/${itemResponse?.slug}`);
    const item = getItem?.data.item
    assert(itemToCreate.title === item.title, "item have bad title")
    assert(itemToCreate.description === item.description, "item have bad description")
    console.log('PASSED ALL CHECKS')
    return true
};


testItem()
    .then((res) => process.exit(res ? 0 : 1))
    .catch((e) => {
        console.log("error while checking api: " + e);
        process.exit(1);
    });