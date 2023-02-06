const { AnythinkClient } = require("./anytinkClient");
const { execAndWaitForEvent } = require("./wilcoEngine/utils");

let anythinkClient;

beforeAll(async () => {
  anythinkClient = new AnythinkClient();
});

describe("API TEST", () => {
  it("checks event user_created emitted", async () => {
    const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
    const user = {
      username: username,
      email: `${username}@test.work`,
      password: "wilco1234",
    };

    await execAndWaitForEvent("user_created", async () => {
      await anythinkClient.createUser(user);
    });
  });
});
