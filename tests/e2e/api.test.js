require("dotenv").config();
const { AnythinkClient } = require("./anytinkClient");

let anythinkClient;

beforeAll(async () => {
  anythinkClient = new AnythinkClient();
});

describe("Test Items", () => {
  it("Checks server is up and serving requests", async () => {
    let response;
    try {
      response = await anythinkClient.healthCheck();
    } catch (error) {
      if (error.code === "ECONNREFUSED" || !error.response) {
        console.error("Error: Connection refused by server");
        throw new Error("Error: Connection refused by server");
      } else {
        console.error("Error: Couldn't connect to server", error);
        throw new Error("Error: Connection refused by server");
      }
    }
    expect(response?.status).toEqual(200);
  });
  it("Creates a user successfully", async () => {
    const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
    const user = {
      username: username,
      email: `${username}@test.work`,
      password: "wilco1234",
    };
    const response = await anythinkClient.createUser(user);
    expect(response.data.user.username).toBe(user.username);
    expect(response.data.user.email).toBe(user.email);
    expect(response.data.user.token).not.toBe(null);
  });

  it("Creates an item successfully", async () => {
    const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
    const user = {
      username: username,
      email: `${username}@test.work`,
      password: "wilco1234",
    };
    const response = await anythinkClient.createUser(user);
    anythinkClient.setToken(response.data.user.token);

    const itemToCreate = {
      title: "WilcoTitle",
      description: "description",
      tag_list: ["tag1"],
    };
    const itemResponse = await anythinkClient.createItem(itemToCreate);

    const item = await anythinkClient.getItem(itemResponse?.slug);

    expect(itemToCreate.title).toBe(item.title);
    expect(itemToCreate.description).toBe(item.description);
  });
});
