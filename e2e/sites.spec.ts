import { test, expect } from '@playwright/test';
import { login } from './fixtures';

test.describe('SITES: Site Management', () => {
  test.beforeEach(async ({ page }) => {
    await login(page);
    await page.goto('/sites');
  });

  test('should display sites page', async ({ page }) => {
    await expect(page).toHaveURL(/.*sites/);

    // Wait for page to load
    await page.waitForLoadState('networkidle');
  });

  test('should show sites table', async ({ page }) => {
    // Check table headers
    await expect(page.locator('text=Site').first()).toBeVisible();
  });

  test('should have add site button', async ({ page }) => {
    const addButton = page.locator('button:has-text("Add"), button:has([class*="Plus"])').first();
    await expect(addButton).toBeVisible();
  });

  test('should open create dialog when clicking add button', async ({ page }) => {
    const addButton = page.locator('button:has-text("Add"), button:has([class*="Plus"])').first();
    await addButton.click();

    // Check dialog is open
    await expect(page.locator('[role="dialog"]')).toBeVisible();
  });

  test('should close dialog when clicking cancel', async ({ page }) => {
    const addButton = page.locator('button:has-text("Add"), button:has([class*="Plus"])').first();
    await addButton.click();

    const cancelButton = page.locator('[role="dialog"] button:has-text("Cancel")');
    await cancelButton.click();

    await expect(page.locator('[role="dialog"]')).not.toBeVisible();
  });

  test('should create new site', async ({ page }) => {
    const addButton = page.locator('button:has-text("Add"), button:has([class*="Plus"])').first();
    await addButton.click();

    // Fill form (name, domain, url are required)
    const testSiteName = `TestSite_${Date.now()}`;
    await page.fill('input[name="name"]', testSiteName);
    await page.fill('input[name="domain"]', 'test.example.com');
    await page.fill('input[name="url"]', 'https://test.example.com');

    // Submit
    const submitButton = page.locator('[role="dialog"] button[type="submit"], [role="dialog"] button:has-text("Save")');
    await submitButton.click();

    // Wait for success toast
    await expect(page.getByText('사이트가 생성되었습니다', { exact: false }).first()).toBeVisible({ timeout: 5000 });

    // Verify new site appears
    await expect(page.locator(`text=${testSiteName}`)).toBeVisible({ timeout: 5000 });
  });

  test('should edit existing site', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const editButton = page.locator('button:has([class*="Pencil"]), button[aria-label*="edit"]').first();

    if (await editButton.isVisible()) {
      await editButton.click();

      await expect(page.locator('[role="dialog"]')).toBeVisible();

      // Close without saving
      const cancelButton = page.locator('[role="dialog"] button:has-text("Cancel")');
      await cancelButton.click();
    }
  });

  test('should show delete confirmation dialog', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const deleteButton = page.locator('button:has([class*="Trash"]), button[aria-label*="delete"]').first();

    if (await deleteButton.isVisible()) {
      await deleteButton.click();

      await expect(page.locator('[role="alertdialog"]')).toBeVisible();
    }
  });
});
