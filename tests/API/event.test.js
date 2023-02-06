require("dotenv").config();
const { app } = require("./mockEventListener");
const { eventHandler } = require("./eventHandler");
const { AnythinkClient } = require("./anytinkClient");
const { sleep } = require("../utils");

jest.mock("./eventHandler");
eventHandler.mockImplementation((event) =>
  console.log(`mock handling event: ${event}`)
);

const PORT = 3003;
let anythinkClient;
let server;

const expectRepoEventToBeHandled = async (event_name, maxTime = 5000) => {
  const start = Date.now();

  while (Date.now() - start < maxTime) {
    try {
      expect(eventHandler).toBeCalledWith(event_name);
      return true;
    } catch (error) {
      // pass
    }
    await sleep(100);
  }

  throw new Error(`Repo Event ${event_name} wasn't received`);
};

beforeAll(async () => {
  anythinkClient = new AnythinkClient();

  server = app.listen(PORT, () => {});
});
afterAll(async () => {
  server.close();
});
describe("API TEST", () => {
  it("checks event user_created emitted", async () => {
    const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
    const user = {
      username: username,
      email: `${username}@test.work`,
      password: "wilco1234",
    };
    await anythinkClient.createUser(user);
    await expectRepoEventToBeHandled("user_created", 500);
  });
});
