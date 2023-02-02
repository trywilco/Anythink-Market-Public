require("dotenv").config();
const axios = require("axios");
const {app} = require("./mockEventListener");

const PORT = 3003

const server = app.listen(PORT, () => {
  console.log(`Mock eventListener app listening on port ${port}`)
})

const healthCheck = async (client) => {
  return await client.get(`/health`);
};

const createUser = async (client, user) => {
  try {
    return await client.post(`/api/users`, { user });
  } catch (error) {
    console.error("Create user request failed", error?.response?.data);
    throw error;
  }
};

const createItem = async (client, item) => {
  try {
    const itemRes = await client.post(`/api/items`, { item });
    return itemRes.data?.item;
  } catch (error) {
    console.error("Create Item request failed", error?.response?.data);
    throw error;
  }
};

describe("Test Items", () => {

  let client;
  beforeAll(() => {
    client = axios.create({
      baseURL: "http://localhost:3000",
      timeout: 10 * 1000,
    });
  });

  afterAll( ()=> {
    server.close()

  })
  it("Server is up and serving requests", async () => {
    let response;
    try {
      response = await healthCheck(client);
    } catch (error) {
      if (error.code === "ECONNREFUSED" || !error.response) {
        console.error("Error: Connection refused by server");
      } else {
        console.error("Error: Couldn't connect to server", error);
      }
    }
    expect(response?.status).toEqual(200);
  });
  it("Create user", async () => {
    const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
    const user = {
      username: username,
      email: `${username}@test.work`,
      password: "wilco1234",
    };
    const response = await createUser(client, user);
    expect(response.data.user.username).toBe(user.username);
    expect(response.data.user.email).toBe(user.email);
    expect(response.data.user.token).toBeTruthy();
  });

  it("Create item", async () => {
    const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
    const user = {
      username: username,
      email: `${username}@test.work`,
      password: "wilco1234",
    };
    const response = await createUser(client, user);
    client.defaults.headers.common[
      "Authorization"
    ] = `Token ${response.data.user.token}`;
    const itemToCreate = {
      title: "WilcoTitle",
      description: "description",
      tag_list: ["tag1"],
    };
    const itemResponse = await createItem(client, itemToCreate);

    const getItem = await client.get(`/api/items/${itemResponse?.slug}`);
    const item = getItem?.data.item;

    expect(itemToCreate.title).toBe(item.title);
    expect(itemToCreate.description).toBe(item.description);
  });

});
