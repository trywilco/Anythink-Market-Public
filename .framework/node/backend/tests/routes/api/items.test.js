//require items.js route from project
const items = require("../../../routes/api/items");

//write jest tests for items
describe("items", () => {
    it("should return a 200 status code", async () => {
        const response = await request(items).get("/items");
        expect(response.statusCode).toBe(200);
    });

    it("should return an array of items", async () => {
        const response = await request(items).get("/items");
        expect(response.body).toEqual(expect.any(Array));
    });


}