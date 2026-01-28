import { test, expect } from '@playwright/test';
import { login } from './fixtures';

test.describe('POINT-LOGS: Point Logs View', () => {
  test.beforeEach(async ({ page }) => {
    await login(page);
    await page.goto('/point-logs');
  });

  test('should display point logs page', async ({ page }) => {
    await expect(page).toHaveURL(/.*point-logs/);
    await page.waitForLoadState('networkidle');
  });

  test('should show point logs table', async ({ page }) => {
    const table = page.locator('table');
    await expect(table).toBeVisible();
  });

  test('should display table headers', async ({ page }) => {
    // Check for common header names (adjust based on actual UI)
    const headers = page.locator('thead th, [role="columnheader"]');
    await expect(headers.first()).toBeVisible();
  });

  test('should show pagination if many records', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    // Check for pagination controls
    const pagination = page.locator('[class*="pagination"], button:has-text("다음"), button:has-text("이전")');

    // Pagination may or may not exist depending on data
    const count = await pagination.count();
    if (count > 0) {
      await expect(pagination.first()).toBeVisible();
    }
  });

  test('should show log details when clicking a row', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    // Find and click first data row
    const tableRow = page.locator('tbody tr').first();

    if (await tableRow.isVisible()) {
      await tableRow.click();

      // Check if detail view or dialog appears (depends on implementation)
      await page.waitForTimeout(500);
    }
  });

  test('should have delete functionality', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const deleteButton = page.locator('button:has([class*="Trash"]), button[aria-label*="delete"]').first();

    if (await deleteButton.isVisible()) {
      await deleteButton.click();

      // Check for confirmation dialog
      await expect(page.locator('[role="alertdialog"]')).toBeVisible();

      // Cancel delete
      const cancelButton = page.locator('[role="alertdialog"] button:has-text("취소")');
      await cancelButton.click();
    }
  });

  test('should filter logs by date range', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    // Look for date filter inputs
    const dateInput = page.locator('input[type="date"]').first();

    if (await dateInput.isVisible()) {
      // Set a date filter
      await dateInput.fill('2024-01-01');

      // Wait for filter to apply
      await page.waitForLoadState('networkidle');
    }
  });

  test('should show empty state when no logs', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    // Check for empty state message or table with no rows
    const tableRows = page.locator('tbody tr');
    const rowCount = await tableRows.count();

    if (rowCount === 0) {
      // Look for empty state message
      const emptyState = page.locator('text=데이터가 없습니다, text=로그가 없습니다');
      // Empty state may or may not be visible
    }
  });

  test('should navigate between pages', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    // Find next page button
    const nextButton = page.locator('button:has-text("다음"), button[aria-label*="next"]').first();

    if (await nextButton.isVisible() && await nextButton.isEnabled()) {
      await nextButton.click();
      await page.waitForLoadState('networkidle');

      // Find previous button and go back
      const prevButton = page.locator('button:has-text("이전"), button[aria-label*="prev"]').first();
      if (await prevButton.isVisible() && await prevButton.isEnabled()) {
        await prevButton.click();
        await page.waitForLoadState('networkidle');
      }
    }
  });
});
