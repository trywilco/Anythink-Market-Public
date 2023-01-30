require("dotenv").config();
const axios = require("axios");

const createUser = async (client,user) => {
    return await client.post(`/api/users`, {user});
};


const createItem = async (client, item) => {
    const itemRes = await client.post(`/api/items`, {item});
    return itemRes.data?.item;
};


    describe("Test Items", ()=> {
        let client
        beforeAll(()=> {
             client = axios.create({
                baseURL: "http://localhost:3000",
                timeout: 10 * 1000
            });
        })
        it("create user", async ()=> {
            const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
            const user = {
                username: username,
                email: `${username}@test.work`,
                password: "wilco1234"
            };
            const response = await createUser(client, user);
            console.log(response)
            expect(response.data.user.username).toBe(user.username);
            expect(response.data.user.email).toBe(user.email);
            expect(response.data.user.token).toBeTruthy();
        })

        it("create user", async ()=> {
            const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
            const user = {
                username: username,
                email: `${username}@test.work`,
                password: "wilco1234"
            };
            const response = await createUser(client, user);
            client.defaults.headers.common["Authorization"] = `Token ${
                response.data.user.token
            }`;
            const itemToCreate = {
                title: "title255999",
                description: "description",
                tag_list: ["tag1"]
            };
            const itemResponse = await createItem(client, itemToCreate);

            const getItem = await client.get(`/api/items/${itemResponse?.slug}`);
            const item = getItem?.data.item;
            console.log(item)

            expect(itemToCreate.title).toBe(item.title);
            expect(itemToCreate.description).toBe(item.description);
        })





});