const { test, beforeAll } = require("@jest/globals");
const { AnythinkClient } = require("./anytinkClient");
const { randomUserInfo } = require("./utils");

let anythinkClient;

beforeAll(async () => {
  anythinkClient = new AnythinkClient();
});

describe("Profiles Route", () => {
  let user;

  beforeAll(async () => {
    user = await anythinkClient.createUser(randomUserInfo());

    await anythinkClient.setToken(user.token);
  });

  test("User marked as not following by default", async () => {
    const otherUser = await anythinkClient.createUser(randomUserInfo());

    const newUserProfile = await anythinkClient.getProfile(otherUser.username);
    expect(newUserProfile.following).toBe(false);
  });

  test("Can follow and unfollow other users", async () => {
    const otherUser = await anythinkClient.createUser(randomUserInfo());

    await anythinkClient.followUser(otherUser.username);

    const followedUserProfile = await anythinkClient.getProfile(
      otherUser.username
    );
    expect(followedUserProfile.following).toBe(true);

    await anythinkClient.unfollowUser(otherUser.username);
    const unfollowedUserProfile = await anythinkClient.getProfile(
      otherUser.username
    );
    expect(unfollowedUserProfile.following).toBe(false);
  });

  test("Receive error when trying to get profile of non existing user", async () => {
    await expect(
      anythinkClient.getProfile("non-existing-user")
    ).rejects.toThrow();
  });

  test("Receive error when trying to follow non existing user", async () => {
    await expect(
      anythinkClient.followUser("non-existing-user")
    ).rejects.toThrow();
  });

  test("Receives error when trying to unfollow non existing user", async () => {
    await expect(
      anythinkClient.unfollowUser("non-existing-user")
    ).rejects.toThrow();
  });
});
