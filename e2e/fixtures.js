"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.expect = exports.test = exports.API_BASE_URL = exports.TEST_USER = void 0;
exports.login = login;
exports.logout = logout;
const test_1 = require("@playwright/test");
Object.defineProperty(exports, "expect", { enumerable: true, get: function () { return test_1.expect; } });
exports.TEST_USER = {
    loginId: 'admin',
    password: 'admin123',
};
exports.API_BASE_URL = 'http://localhost:8080/api/v1';
exports.test = test_1.test.extend({
    authenticatedPage: async ({ page }, use) => {
        await page.goto('/login');
        await page.fill('#loginId', exports.TEST_USER.loginId);
        await page.fill('#password', exports.TEST_USER.password);
        await page.click('button[type="submit"]');
        await page.waitForURL('**/dashboard');
        await use(test_1.test);
    },
});
async function login(page) {
    await page.goto('/login');
    await page.fill('#loginId', exports.TEST_USER.loginId);
    await page.fill('#password', exports.TEST_USER.password);
    await page.click('button[type="submit"]');
    await page.waitForURL('**/dashboard');
}
async function logout(page) {
    await page.evaluate(() => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userName');
        localStorage.removeItem('loginId');
    });
    await page.goto('/login');
}
//# sourceMappingURL=fixtures.js.map