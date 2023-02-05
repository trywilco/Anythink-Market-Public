require("dotenv").config();
const axios = require("axios");
const { app } = require("./mockEventListener");
const { eventHandler } = require("./eventHandler");
const { createUser, AnythinkClient } = require("./anytinkClient");
const { sleep } = require("../utils");

jest.mock("./eventHandler");
eventHandler.mockImplementation((event) =>
  console.log(`mock handling event: ${event}`)
);

const PORT = 3003;
let anythinkClient;
let server;

beforeAll(async () => {
  anythinkClient = new AnythinkClient();

  console.log("Starting server...");
  server = app.listen(PORT, () => {
    console.log(`Mock eventListener app listening on port ${PORT}`);
  });
});
afterAll(async () => {
  console.log("closing server...");
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
    await sleep(100); // allow server to handle async callback of handling the event
    expect(eventHandler).toBeCalledWith("user_created");
  });
});
