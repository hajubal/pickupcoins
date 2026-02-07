import { test as base, expect } from '@playwright/test';
export declare const TEST_USER: {
    loginId: string;
    password: string;
};
export declare const API_BASE_URL = "http://localhost:8080/api/v1";
export declare const test: import("@playwright/test").TestType<import("@playwright/test").PlaywrightTestArgs & import("@playwright/test").PlaywrightTestOptions & {
    authenticatedPage: typeof base;
}, import("@playwright/test").PlaywrightWorkerArgs & import("@playwright/test").PlaywrightWorkerOptions>;
export declare function login(page: any): Promise<void>;
export declare function logout(page: any): Promise<void>;
export { expect };
