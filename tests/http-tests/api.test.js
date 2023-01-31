require("dotenv").config();
const axios = require("axios");

const healthCheck = async (client) => {
    return await client.get(`/health`);
};

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
                baseURL: "http://localhost:3001",
                timeout: 10 * 1000
            });
        })
        it("check server is up and running", async () => {
            let response;
            try {
                response = await healthCheck(client);
            } catch (error) {
                if (error.code === 'ECONNREFUSED' || !error.response) {
                    console.error('Error: Connection refused by server');
                } else {
                    console.error("Error: Couldn't connect to server", error);
                }
            }
            expect(response?.status).toEqual(200)

        })
        it("create user", async ()=> {
            const user = {
                username: "engine",
                email: "engine@wilco.work",
                password: "wilco1234",
            };
                const response = await createUser(client, user);
                expect(response.data.user.username).toBe(user.username);
                expect(response.data.user.email).toBe(user.email);
                expect(response.data.user.token).toBeTruthy();
        })

        it("create item", async ()=> {
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

            expect(itemToCreate.title).toBe(item.title);
            expect(itemToCreate.description).toBe(item.description);
        })





});