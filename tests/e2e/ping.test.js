const { it } = require("@jest/globals");
const { AnythinkClient } = require("./anytinkClient");
const { execAndWaitForEvent } = require("./wilcoEngine/utils");

let anythinkClient;

beforeAll(async () => {
  anythinkClient = new AnythinkClient();
});

describe("Ping Route", () => {
  it("Sends ping event to the Wilco Engine", async () => {
    await execAndWaitForEvent("ping", async () => {
      await anythinkClient.ping();
    });
  });
});
