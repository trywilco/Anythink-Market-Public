const { beforeAll, expect, describe, beforeEach } = require("@jest/globals");
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
    });

    test("Can create and retreive an item", async () => {
      const item = randomItemInfo();

      const createdItem = await anythinkClient.createItem(item, user);
      expect(createdItem).toMatchObject(item);

      const receivedItem = await anythinkClient.getItem(createdItem.slug);
      expect(receivedItem).toMatchObject(createdItem);
    });

    test("Can't create item without title", async () => {
      await expect(
        anythinkClient.createItem(randomItemInfo({ title: undefined }, user))
      ).rejects.toThrow();
    });

    test("Can't create item without description", async () => {
      await expect(
        anythinkClient.createItem(randomItemInfo({ description: undefined }))
      ).rejects.toThrow();
    });

    test("Can create item without image", async () => {
      const createdItem = await anythinkClient.createItem(
        randomItemInfo({ image: undefined }),
        user
      );
      expect(createdItem.image).toBeFalsy();
      expect(createdItem.slug).toBeDefined();
    });

    test("Can create item without tagList", async () => {
      const createdItem = await anythinkClient.createItem(
        randomItemInfo({ tagList: undefined }),
        user
      );
      expect(createdItem.tagList).toStrictEqual([]);
      expect(createdItem.slug).toBeDefined();
    });

    test("Creating item sends item_created event to the Wilco Engine", async () => {
      await execAndWaitForEvent("item_created", async () => {
        await anythinkClient.createItem(randomItemInfo(), user);
      });
    });
  });

  describe("Delete item", () => {
    let user;
    let otherUser;

    beforeAll(async () => {
      user = await anythinkClient.createUser(randomUserInfo());
    });

    test("Can delete item", async () => {
      const createdItem = await anythinkClient.createItem(
        randomItemInfo(),
        user
      );
      await anythinkClient.deleteItem(createdItem.slug, user);
      await expect(anythinkClient.getItem(createdItem.slug)).rejects.toThrow();
    });

    test("Can't delete item created by other user", async () => {
      const otherUser = await anythinkClient.createUser(randomUserInfo());
      const createdItem = await anythinkClient.createItem(
        randomItemInfo(),
        otherUser
      );

      await expect(
        anythinkClient.deleteItem(createdItem.slug, user)
      ).rejects.toThrow();
    });
  });

  describe("Update Item", () => {
    let user;

    beforeEach(async () => {
      user = await anythinkClient.createUser(randomUserInfo());
    });

    test("Can't update item created by other user", async () => {
      const otherUser = await anythinkClient.createUser(randomUserInfo());
      const createdItem = await anythinkClient.createItem(
        randomItemInfo(),
        otherUser
      );

      await expect(
        anythinkClient.updateItem(createdItem.slug, randomItemInfo(), user)
      ).rejects.toThrow();
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
      const item = await anythinkClient.createItem(origItemInfo, user);

      const updatedItemResult = await anythinkClient.updateItem(
        item.slug,
        updateInfo,
        user
      );

      expect(updatedItemResult).toMatchObject({
        ...origItemInfo,
        ...updateInfo,
      });

      const retreivedItem = await anythinkClient.getItem(item.slug);
      expect(retreivedItem).toMatchObject(updatedItemResult);
    };
  });

  describe("Favorite Item", () => {
    let user;
    let favoritingUserA;
    let favoritingUserB;

    beforeEach(async () => {
      user = await anythinkClient.createUser(randomUserInfo());
      favoritingUserA = await anythinkClient.createUser(randomUserInfo());
      favoritingUserB = await anythinkClient.createUser(randomUserInfo());
    });

    test("Users can favorite an item", async () => {
      const item = await anythinkClient.createItem(randomItemInfo(), user);

      await anythinkClient.favoriteItem(item.slug, favoritingUserA);

      const itemBeforeFavoriting = await anythinkClient.getItem(item.slug);
      expect(itemBeforeFavoriting.favoritesCount).toBe(1);
      expect(itemBeforeFavoriting.favorited).toBe(false);

      await anythinkClient.favoriteItem(item.slug, favoritingUserB);
      const itemAfterFavoriting = await anythinkClient.getItem(
        item.slug,
        favoritingUserB
      );
      expect(itemAfterFavoriting.favoritesCount).toBe(2);
      expect(itemAfterFavoriting.favorited).toBe(true);
    });

    test("User can favorite multiple items", async () => {
      const itemA = await anythinkClient.createItem(randomItemInfo(), user);
      const itemB = await anythinkClient.createItem(randomItemInfo(), user);

      await anythinkClient.favoriteItem(itemA.slug, favoritingUserA);
      await anythinkClient.favoriteItem(itemB.slug, favoritingUserB);

      const updatedItemA = await anythinkClient.getItem(itemA.slug);
      expect(updatedItemA.favoritesCount).toBe(1);

      const updatedItemB = await anythinkClient.getItem(itemB.slug);
      expect(updatedItemB.favoritesCount).toBe(1);
    });

    test("Users can unfavorite an item", async () => {
      const item = await anythinkClient.createItem(randomItemInfo(), user);

      await anythinkClient.favoriteItem(item.slug, favoritingUserA);
      await anythinkClient.favoriteItem(item.slug, favoritingUserB);

      const itemAfterTwoFavorites = await anythinkClient.getItem(item.slug);
      expect(itemAfterTwoFavorites.favoritesCount).toBe(2);

      await anythinkClient.unfavoriteItem(item.slug, favoritingUserA);
      const itemAfterFirstUnfavorite = await anythinkClient.getItem(item.slug);
      expect(itemAfterFirstUnfavorite.favoritesCount).toBe(1);

      await anythinkClient.unfavoriteItem(item.slug, favoritingUserB);
      const itemAfterSecondUnfavorite = await anythinkClient.getItem(item.slug);
      expect(itemAfterSecondUnfavorite.favoritesCount).toBe(0);
    });
  });

  describe("Comment on Item", () => {
    let user;
    let commentingUserA;
    let commentingUserB;

    beforeEach(async () => {
      user = await anythinkClient.createUser(randomUserInfo());
      commentingUserA = await anythinkClient.createUser(randomUserInfo());
      commentingUserB = await anythinkClient.createUser(randomUserInfo());
    });

    test("Users can comment on an item", async () => {
      const item = await anythinkClient.createItem(randomItemInfo(), user);
      await addComments(item, [commentingUserA, commentingUserB]);

      const itemsComments = await anythinkClient.getComments(item.slug);
      expect(itemsComments).toHaveLength(2);
    });

    test("Comments are retreived in reversed order", async () => {
      const item = await anythinkClient.createItem(randomItemInfo(), user);
      const commnets = await addComments(item, [
        commentingUserA,
        commentingUserB,
      ]);

      const itemsComments = await anythinkClient.getComments(item.slug);

      expect(itemsComments[0]).toMatchObject(commnets[1]);
      expect(itemsComments[1]).toMatchObject(commnets[0]);
    });

    test("Users can delete their own comments", async () => {
      const item = await anythinkClient.createItem(randomItemInfo(), user);
      const commnets = await addComments(item, [
        commentingUserA,
        commentingUserB,
      ]);

      expect(await anythinkClient.getComments(item.slug)).toHaveLength(2);

      await anythinkClient.deleteComment(
        item.slug,
        commnets[0].id,
        commentingUserA
      );
      expect(await anythinkClient.getComments(item.slug)).toHaveLength(1);

      await anythinkClient.deleteComment(
        item.slug,
        commnets[1].id,
        commentingUserB
      );
      expect(await anythinkClient.getComments(item.slug)).toHaveLength(0);
    });

    test("Users cannot delete other users comments", async () => {
      const item = await anythinkClient.createItem(randomItemInfo(), user);
      const commnets = await addComments(item, [
        commentingUserA,
        commentingUserB,
      ]);

      expect(await anythinkClient.getComments(item.slug)).toHaveLength(2);

      await expect(
        anythinkClient.deleteComment(item.slug, commnets[0].id, commentingUserB)
      ).rejects.toThrow();

      expect(await anythinkClient.getComments(item.slug)).toHaveLength(2);
    });

    const addComments = async (item, commentingUsers) => {
      let comments = [];

      for (const user of commentingUsers) {
        const commentBody = randomString(50);
        const comment = await anythinkClient.commentOnItem(
          item.slug,
          commentBody,
          user
        );
        comments.push(comment);
      }

      return comments;
    };
  });
});
