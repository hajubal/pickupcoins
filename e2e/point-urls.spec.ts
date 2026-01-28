import { test, expect } from '@playwright/test';
import { login } from './fixtures';

test.describe('POINT-URLS: Point URL Management', () => {
  test.beforeEach(async ({ page }) => {
    await login(page);
    await page.goto('/point-urls');
  });

  test('should display point URLs page', async ({ page }) => {
    await expect(page).toHaveURL(/.*point-urls/);
    await page.waitForLoadState('networkidle');
  });

  test('should show point URLs table', async ({ page }) => {
    // Check table exists
    const table = page.locator('table');
    await expect(table).toBeVisible();
  });

  test('should have add point URL button', async ({ page }) => {
    const addButton = page.locator('button:has-text("Add"), button:has([class*="Plus"])').first();
    await expect(addButton).toBeVisible();
  });

  test('should open create dialog when clicking add button', async ({ page }) => {
    const addButton = page.locator('button:has-text("Add"), button:has([class*="Plus"])').first();
    await addButton.click();

    await expect(page.locator('[role="dialog"]')).toBeVisible();
  });

  test('should close dialog when clicking cancel', async ({ page }) => {
    const addButton = page.locator('button:has-text("Add"), button:has([class*="Plus"])').first();
    await addButton.click();

    const cancelButton = page.locator('[role="dialog"] button:has-text("Cancel")');
    await cancelButton.click();

    await expect(page.locator('[role="dialog"]')).not.toBeVisible();
  });

  test('should create new point URL', async ({ page }) => {
    const addButton = page.locator('button:has-text("Add"), button:has([class*="Plus"])').first();
    await addButton.click();

    // Fill form (adjust selectors based on actual form)
    const testUrl = `https://example.com/point/${Date.now()}`;
    const urlInput = page.locator('input[name="url"], #url, input[type="url"], input[placeholder*="URL"]').first();
    if (await urlInput.isVisible()) {
      await urlInput.fill(testUrl);
    }

    // Submit
    const submitButton = page.locator('[role="dialog"] button[type="submit"], [role="dialog"] button:has-text("Save")');
    await submitButton.click();

    // Wait for response
    await page.waitForLoadState('networkidle');
  });

  test('should edit existing point URL', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const editButton = page.locator('button:has([class*="Pencil"]), button[aria-label*="edit"]').first();

    if (await editButton.isVisible()) {
      await editButton.click();
      await expect(page.locator('[role="dialog"]')).toBeVisible();

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

  test('should toggle permanent status', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const toggleSwitch = page.locator('[role="switch"]').first();

    if (await toggleSwitch.isVisible()) {
      const initialState = await toggleSwitch.getAttribute('data-state');
      await toggleSwitch.click();
      await page.waitForTimeout(500);
      const newState = await toggleSwitch.getAttribute('data-state');
      expect(newState).not.toBe(initialState);
    }
  });
});
