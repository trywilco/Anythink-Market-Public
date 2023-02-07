const { test, it } = require("@jest/globals");
const { AnythinkClient } = require("./anytinkClient");

let anythinkClient;

beforeAll(async () => {
  anythinkClient = new AnythinkClient();
});

describe("Health Route", () => {
  it("Returns valid response", async () => {
    const response = await anythinkClient.healthCheck();
    expect(response?.status).toEqual(200);
  });
});
