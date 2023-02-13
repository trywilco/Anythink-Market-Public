import { test,  expect } from '@playwright/test';
import { uid } from 'uid';

import {dispatch, execAndWaitForRequest} from "../requestValidator";

const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
const email = `${username}@email.com`
const password = `pass${(Math.random() + 1).toString(36).substring(7)}`;

const expectCreateUserRequest = (page,  username, email, password ) => {
    const requestId = uid()
    page.on('request', (request) => {
        if(request.url() === `${process.env.BACKEND_API_URL}/users` && request.method() === 'POST') {
            expect(JSON.parse(request.postData())?.user?.username).toEqual(username)
            expect(JSON.parse(request.postData())?.user?.email).toEqual(email)
            expect(JSON.parse(request.postData())?.user?.password).toEqual(password)
            dispatch(requestId)
        }
    })
    return requestId
}

const expectCreateItemRequest = (page,  title, description, image ) => {
    const requestId = uid()
    page.on('request', (request) => {
        if(request.url() === `${process.env.BACKEND_API_URL}/items`  && request.method() === 'POST') {
            expect(JSON.parse(request.postData())?.item?.title).toEqual(title)
            expect(JSON.parse(request.postData())?.item?.description).toEqual(description)
            expect(JSON.parse(request.postData())?.item?.image).toEqual(image)
            dispatch(requestId)
        }
    })
    return requestId
}

test('Creates a user', async ({page}) => {
    await page.goto(`${process.env.REACT_APP_URL}`);
    await page.getByRole('link', {name: 'Sign up'}).click();
    await page.getByPlaceholder('Username').fill(username);
    await page.getByPlaceholder('Password').fill(password);
    await page.getByPlaceholder('Email').fill(email);
    const requestId = expectCreateUserRequest(page, username, email, password)
    await execAndWaitForRequest(requestId, async () => await page.getByRole('button', {name: 'SIGN UP'}).click() )
});

test('Creates an item', async ({page}) => {
    const title = "title"
    const description = "description"
    const imageUrl = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"
    await page.goto(`${process.env.REACT_APP_URL}/editor`);
    await page.getByPlaceholder('Item Title').fill(title);
    await page.getByPlaceholder('What\'s this item about?').fill(description);
    await page.getByPlaceholder('Image url').fill(imageUrl);
    const requestId = expectCreateItemRequest(page, title, description, imageUrl)
    await execAndWaitForRequest(requestId, async () => await page.getByRole('button', {name: 'Publish Item'}).click() )
});

