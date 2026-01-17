export default () => ({
  port: parseInt(process.env.PORT || '8080', 10),
  nodeEnv: process.env.NODE_ENV || 'development',

  database: {
    url: process.env.DATABASE_URL,
  },

  jwt: {
    secret: process.env.JWT_SECRET,
    accessTokenValidity: parseInt(process.env.JWT_ACCESS_TOKEN_VALIDITY || '900000', 10),
    refreshTokenValidity: parseInt(process.env.JWT_REFRESH_TOKEN_VALIDITY || '604800000', 10),
    rememberMeTokenValidity: parseInt(process.env.JWT_REMEMBER_ME_TOKEN_VALIDITY || '1296000000', 10),
  },

  cors: {
    origins: (process.env.CORS_ORIGINS || 'http://localhost:3000').split(','),
  },

  naver: {
    saveKeyword: process.env.NAVER_SAVE_KEYWORD || '적립',
    invalidCookieKeyword: process.env.NAVER_INVALID_COOKIE_KEYWORD || '로그인이 필요',
    amountPattern: process.env.NAVER_AMOUNT_PATTERN || '\\s\\d+원이 적립 됩니다.',
    userAgent:
      process.env.NAVER_USER_AGENT ||
      'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
  },

  crawler: {
    timeout: parseInt(process.env.CRAWLER_TIMEOUT || '10000', 10),
    retryCount: parseInt(process.env.CRAWLER_RETRY_COUNT || '3', 10),
  },

  schedule: {
    crawlerFixedDelay: parseInt(process.env.SCHEDULE_CRAWLER_FIXED_DELAY || '300000', 10),
    pointFixedDelay: parseInt(process.env.SCHEDULE_POINT_FIXED_DELAY || '300000', 10),
    dailyReportCron: process.env.SCHEDULE_DAILY_REPORT_CRON || '0 0 7 * * *',
  },

  slack: {
    webhookUrl: process.env.SLACK_WEBHOOK_URL || '',
  },
});
