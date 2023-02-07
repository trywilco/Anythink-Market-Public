require("dotenv").config();
const { AnythinkClient } = require("./anytinkClient");

let anythinkClient;

beforeAll(async () => {
  anythinkClient = new AnythinkClient();
});

describe("Users Route", () => {
  test("Can create user with info", async () => {
    const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
    const user = {
      username: username,
      email: `${username}@test.work`,
      password: "wilco1234",
    };
    
    const response = await anythinkClient.createUser(user);
    expect(response.data.user).toEqual(expect.objectContaining({username, email: user.email, role: "user"}));
  });
});
