import { test, expect } from '@playwright/test';
import { login } from './fixtures';

test.describe('DASHBOARD: Statistics', () => {
  test.beforeEach(async ({ page }) => {
    await login(page);
  });

  test('should display dashboard page', async ({ page }) => {
    await page.goto('/dashboard');

    // Should be on dashboard
    await expect(page).toHaveURL(/.*dashboard/);
  });

  test('should show statistics cards', async ({ page }) => {
    await page.goto('/dashboard');

    // Wait for stats to load
    await page.waitForLoadState('networkidle');

    // Check for stat cards (adjust selectors based on actual UI)
    const cards = page.locator('[class*="card"]');
    await expect(cards.first()).toBeVisible({ timeout: 10000 });
  });

  test('should display navigation sidebar', async ({ page }) => {
    await page.goto('/dashboard');

    // Check sidebar navigation items using role links
    await expect(page.getByRole('link', { name: 'Dashboard' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Cookies' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Point URLs' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Point Logs' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Sites' })).toBeVisible();
  });

  test('should navigate to other pages from sidebar', async ({ page }) => {
    await page.goto('/dashboard');

    // Click on Cookies navigation
    await page.click('text=Cookies');
    await expect(page).toHaveURL(/.*cookies/);

    // Click on Point URLs navigation
    await page.click('text=Point URLs');
    await expect(page).toHaveURL(/.*point-urls/);

    // Click on Point Logs navigation
    await page.click('text=Point Logs');
    await expect(page).toHaveURL(/.*point-logs/);

    // Click on Sites navigation
    await page.click('text=Sites');
    await expect(page).toHaveURL(/.*sites/);

    // Back to dashboard
    await page.click('text=Dashboard');
    await expect(page).toHaveURL(/.*dashboard/);
  });

  test('should show user info in header', async ({ page }) => {
    await page.goto('/dashboard');

    // Check for user name display (stored from login)
    const userName = await page.evaluate(() => localStorage.getItem('userName'));
    if (userName) {
      await expect(page.locator(`text=${userName}`)).toBeVisible();
    }
  });
});
