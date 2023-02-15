import { expect, test } from "@playwright/test";

import {
  dispatch,
  execAndWaitForRequest,
  wrapWithRequestId,
} from "../requestValidator";

const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
const email = `${username}@email.com`;
const password = `pass${(Math.random() + 1).toString(36).substring(7)}`;

const expectCreateUserRequest = (page, username, email, password) => {
  return wrapWithRequestId((requestId) => {
    page.on("request", (request) => {
      if (
        request.url() === `${process.env.BACKEND_API_URL}/users` &&
        request.method() === "POST"
      ) {
        expect(JSON.parse(request.postData())?.user?.username).toEqual(
          username
        );
        expect(JSON.parse(request.postData())?.user?.email).toEqual(email);
        expect(JSON.parse(request.postData())?.user?.password).toEqual(
          password
        );
        dispatch(requestId);
      }
    });
  })();
};

const expectCreateItemRequest = (page, title, description, image) => {
  return wrapWithRequestId((requestId) => {
    page.on("request", (request) => {
      if (
        request.url() === `${process.env.BACKEND_API_URL}/items` &&
        request.method() === "POST"
      ) {
        expect(JSON.parse(request.postData())?.item?.title).toEqual(title);
        expect(JSON.parse(request.postData())?.item?.description).toEqual(
          description
        );
        expect(JSON.parse(request.postData())?.item?.image).toEqual(image);
        dispatch(requestId);
      }
    });
  })();
};

test("Creates a user", async ({ page }) => {
  await page.goto(`${process.env.REACT_APP_URL}`);
  await page.getByRole("link", { name: "Sign up" }).click();
  await page.getByPlaceholder("Username").fill(username);
  await page.getByPlaceholder("Password").fill(password);
  await page.getByPlaceholder("Email").fill(email);
  const requestId = expectCreateUserRequest(page, username, email, password);
  await execAndWaitForRequest(
    requestId,
    async () => await page.getByRole("button", { name: "SIGN UP" }).click()
  );
});

test("Creates a user 2", async ({ page }) => {
  await page.route(`${process.env.BACKEND_API_URL}/users`, async (route) => {
    const json = {
      user: {
        username: username,
        email: email,
        token:
          "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYzZWE2MjAxZjg3MjE1OGMzMTE1YWI0ZSIsInVzZXJuYW1lIjoidXNlcjJ3d3FlIiwiZXhwIjoxNjgxNDg1Mjk3LCJpYXQiOjE2NzYzMDQ4OTd9.Z45FqelGgXLU4q6xkhw_fTHZ5GXoVsx0vI_HoI3ccDo",
        role: "user",
      },
    };
    await route.fulfill({ json, contentType: "application/json", status: 200 });
  });

  await page.route(
    `${process.env.BACKEND_API_URL}/profiles/${username}`,
    async (route) => {
      const json = {
        profile: {
          username: username,
          image: "https://static.productionready.io/images/smiley-cyrus.jpg",
          following: false,
        },
      };
      await route.fulfill({
        json,
        headers: { "Content-Type": "application/json" },
      });
    }
  );

  await page.route(
    `${process.env.BACKEND_API_URL}/items?seller=${username}**`,
    async (route) => {
      const json = { items: [], itemsCount: 0 };
      await route.fulfill({
        json,
        headers: { "Content-Type": "application/json" },
      });
    }
  );

  await page.goto(`${process.env.REACT_APP_URL}`);
  await page.getByRole("link", { name: "Sign up" }).click();
  await page.getByPlaceholder("Username").fill(username);
  await page.getByPlaceholder("Password").fill(password);
  await page.getByPlaceholder("Email").fill(email);
  await page.getByRole("button", { name: "SIGN UP" }).click();
  await page.waitForSelector(`[href="/@${username}"]`);
});

test("Creates an item", async ({ page }) => {
  const title = "title";
  const description = "description";
  const imageUrl =
    "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";
  await page.goto(`${process.env.REACT_APP_URL}/editor`);
  await page.getByPlaceholder("Item Title").fill(title);
  await page.getByPlaceholder("What's this item about?").fill(description);
  await page.getByPlaceholder("Image url").fill(imageUrl);
  const requestId = expectCreateItemRequest(page, title, description, imageUrl);
  await execAndWaitForRequest(
    requestId,
    async () => await page.getByRole("button", { name: "Publish Item" }).click()
  );
});
