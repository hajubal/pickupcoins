import { test as base, expect } from '@playwright/test';

// Test credentials - should match seeded admin user
export const TEST_USER = {
  loginId: 'admin',
  password: 'admin123',
};

// API endpoints
export const API_BASE_URL = 'http://localhost:8080/api/v1';

// Extended test with authentication
export const test = base.extend<{ authenticatedPage: typeof base }>({
  authenticatedPage: async ({ page }, use) => {
    // Login before test
    await page.goto('/login');
    await page.fill('#loginId', TEST_USER.loginId);
    await page.fill('#password', TEST_USER.password);
    await page.click('button[type="submit"]');

    // Wait for redirect to dashboard
    await page.waitForURL('**/dashboard');

    await use(base);
  },
});

// Helper function to login
export async function login(page: any) {
  await page.goto('/login');
  await page.fill('#loginId', TEST_USER.loginId);
  await page.fill('#password', TEST_USER.password);
  await page.click('button[type="submit"]');
  await page.waitForURL('**/dashboard');
}

// Helper function to logout
export async function logout(page: any) {
  // Clear localStorage
  await page.evaluate(() => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userName');
    localStorage.removeItem('loginId');
  });
  await page.goto('/login');
}

export { expect };
