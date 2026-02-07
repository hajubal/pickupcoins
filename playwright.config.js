"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const test_1 = require("@playwright/test");
exports.default = (0, test_1.defineConfig)({
    testDir: './e2e',
    fullyParallel: false,
    forbidOnly: !!process.env.CI,
    retries: process.env.CI ? 2 : 0,
    workers: 1,
    reporter: [
        ['html', { outputFolder: 'playwright-report' }],
        ['list'],
    ],
    use: {
        baseURL: 'http://localhost:5173',
        trace: 'on-first-retry',
        screenshot: 'only-on-failure',
        video: 'retain-on-failure',
    },
    projects: [
        {
            name: 'chromium',
            use: { ...test_1.devices['Desktop Chrome'] },
        },
    ],
    webServer: [
        {
            command: 'npx prisma db push --skip-generate && npm run start:prod',
            url: 'http://localhost:8080/api/v1/health',
            reuseExistingServer: !process.env.CI,
            timeout: 120000,
            env: {
                DATABASE_URL: 'file:./prisma/e2e.db',
            },
        },
        {
            command: 'cd admin && npm run dev',
            url: 'http://localhost:5173',
            reuseExistingServer: !process.env.CI,
            timeout: 120000,
        },
    ],
});
//# sourceMappingURL=playwright.config.js.map