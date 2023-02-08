import { test,  expect } from '@playwright/test';


test('Creates a user', async ({ page }) => {
    const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
    const email = `${username}@email.com`
    await page.goto('http://localhost:3001/');
    await page.getByRole('link', { name: 'Sign up' }).click();
    await page.getByPlaceholder('Username').click();
    await page.getByPlaceholder('Username').fill(username);
    await page.getByPlaceholder('Password').fill('pass123456');
    await page.getByPlaceholder('Email').fill(email);
    await page.getByRole('button', { name: 'SIGN UP' }).click();
    await page.getByRole('heading', { name: username });
    await expect(page.getByRole('heading', { name: username })).toHaveText(username);
});