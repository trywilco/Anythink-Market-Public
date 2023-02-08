const { beforeAll, expect } = require("@jest/globals");
const { AnythinkClient } = require("./anytinkClient");
const { randomItemInfo, randomUserInfo, randomImageUrl } = require("./utils");
const { execAndWaitForEvent } = require("./wilcoEngine/utils");

let anythinkClient;

beforeAll(async () => {
  anythinkClient = new AnythinkClient();
});

describe("Items Route", () => {
  describe("Create Item", () => {
    let user;

    beforeAll(async () => {
      user = await anythinkClient.createUser(randomUserInfo());
      anythinkClient.setToken(user.token);
    });

    test("Can create and retreive an item", async () => {
      const item = randomItemInfo();

      const createdItem = await anythinkClient.createItem(item);
      expect(createdItem).toMatchObject(item);

      const receivedItem = await anythinkClient.getItem(createdItem.slug);
      expect(receivedItem).toMatchObject(createdItem);
    });

    test("Can create item without description", async () => {
      const createdItem = await anythinkClient.createItem(
        randomItemInfo({ description: undefined })
      );
      expect(createdItem.description).toBeFalsy();
      expect(createdItem.slug).toBeDefined();
    });

    test("Can create item without image", async () => {
      const createdItem = await anythinkClient.createItem(
        randomItemInfo({ image: undefined })
      );
      expect(createdItem.image).toBeFalsy();
      expect(createdItem.slug).toBeDefined();
    });

    test("Can create item without tagList", async () => {
      const createdItem = await anythinkClient.createItem(
        randomItemInfo({ tagList: undefined })
      );
      expect(createdItem.tagList).toStrictEqual([]);
      expect(createdItem.slug).toBeDefined();
    });

    test("Creating item sends item_created event to the Wilco Engine", async () => {
      await execAndWaitForEvent("item_created", async () => {
        await anythinkClient.createItem(randomItemInfo());
      });
    });
  });

  describe("Update Item", () => {
    let user;

    beforeAll(async () => {
      user = await anythinkClient.createUser(randomUserInfo());
      anythinkClient.setToken(user.token);
    });

    test("Can update title of an item", async () => {
      const updateInfo = { title: "New Title" };
      await createAndValidateUpdate(updateInfo);
    });

    test("Can update description of an item", async () => {
      const updateInfo = { description: "New Description" };
      await createAndValidateUpdate(updateInfo);
    });

    test("Can update image of an item", async () => {
      const updateInfo = { image: randomImageUrl() };
      await createAndValidateUpdate(updateInfo);
    });

    test("Can update tagList of an item", async () => {
      const updateInfo = { tagList: ["tag1", "tag2"] };
      await createAndValidateUpdate(updateInfo);
    });

    test("Can update all fields of an item", async () => {
      const updateInfo = {
        title: "New Title",
        description: "New Description",
        image: randomImageUrl(),
        tagList: ["tag1", "tag2"],
      };

      await createAndValidateUpdate(updateInfo);
    });

    const createAndValidateUpdate = async (updateInfo) => {
      const origItemInfo = randomItemInfo();
      const item = await anythinkClient.createItem(origItemInfo);
      const updatedItemResult = await anythinkClient.updateItem(
        item.slug,
        updateInfo
      );

      expect(updatedItemResult).toMatchObject({
        ...origItemInfo,
        ...updateInfo,
      });

      const retreivedItem = await anythinkClient.getItem(item.slug);
      expect(retreivedItem).toMatchObject(updatedItemResult);
    };
  });
});
