const { beforeAll, expect, describe } = require("@jest/globals");
const { AnythinkClient } = require("./anytinkClient");
const {
  randomItemInfo,
  randomUserInfo,
  randomImageUrl,
  randomString,
} = require("./utils");
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

    test("Can't create item without title", async () => {
      await expect(
        anythinkClient.createItem(randomItemInfo({ title: undefined }))
      ).rejects.toThrow();
    });

    test("Can't create item without description", async () => {
      await expect(
        anythinkClient.createItem(randomItemInfo({ description: undefined }))
      ).rejects.toThrow();
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

  describe("Delete item", () => {
    let user;
    let otherUser;

    beforeAll(async () => {
      user = await anythinkClient.createUser(randomUserInfo());
      anythinkClient.setToken(user.token);
    });

    test("Can delete item", async () => {
      const createdItem = await anythinkClient.createItem(randomItemInfo());
      await anythinkClient.deleteItem(createdItem.slug);
      await expect(anythinkClient.getItem(createdItem.slug)).rejects.toThrow();
    });

    test("Can't delete item created by other user", async () => {
      const otherUser = await anythinkClient.createUser(randomUserInfo());
      anythinkClient.setToken(otherUser.token);

      const createdItem = await anythinkClient.createItem(randomItemInfo());

      anythinkClient.setToken(user.token);
      await expect(
        anythinkClient.deleteItem(createdItem.slug)
      ).rejects.toThrow();
    });
  });

  describe("Update Item", () => {
    let user;

    beforeAll(async () => {
      user = await anythinkClient.createUser(randomUserInfo());
      anythinkClient.setToken(user.token);
    });

    test("Can update title of an item", async () => {
      const updateInfo = { title: randomString() };
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

    test("Can update all fields of an item", async () => {
      const updateInfo = {
        title: "New Title",
        description: "New Description",
        image: randomImageUrl(),
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

  describe("Favorite Item", () => {});
});
