import { Injectable, Logger } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { SlackService } from '../notification/slack.service';

@Injectable()
export class ReportService {
  private readonly logger = new Logger(ReportService.name);

  constructor(
    private readonly prisma: PrismaService,
    private readonly slackService: SlackService,
  ) {}

  /**
   * Generate and send daily report to all active users
   */
  async report(): Promise<void> {
    // Get yesterday's date range
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    yesterday.setHours(0, 0, 0, 0);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    // Get new point URLs from yesterday
    const pointUrls = await this.prisma.pointUrl.findMany({
      where: {
        createdDate: {
          gte: yesterday,
          lt: today,
        },
      },
    });

    this.logger.log(`Point URLs collected: ${pointUrls.length}`);

    // Get all active users
    const activeSiteUsers = await this.prisma.siteUser.findMany({
      where: { active: true },
    });

    this.logger.log(`Active site users: ${activeSiteUsers.length}`);

    for (const siteUser of activeSiteUsers) {
      try {
        await this.sendUserReport(siteUser, pointUrls.length, yesterday, today);
      } catch (error) {
        this.logger.error(`Failed to send report for user: ${siteUser.loginId}`, error);
      }
    }
  }

  /**
   * Send report for a specific user
   */
  private async sendUserReport(
    siteUser: { id: number; loginId: string; userName: string; slackWebhookUrl: string | null },
    urlCount: number,
    startDate: Date,
    endDate: Date,
  ): Promise<void> {
    // Get cookies for this user
    const cookies = await this.prisma.cookie.findMany({
      where: { siteUserId: siteUser.id },
    });

    const cookieIds = cookies.map((c) => c.id);
    const invalidCookieCount = cookies.filter((c) => !c.isValid).length;

    // Get saved points for yesterday
    const savedPoints = await this.prisma.savedPoint.findMany({
      where: {
        cookieId: { in: cookieIds },
        createdDate: {
          gte: startDate,
          lt: endDate,
        },
      },
      include: { cookie: true },
    });

    const successCount = savedPoints.length;
    const dayAmount = savedPoints.reduce((sum, sp) => sum + sp.amount, 0);

    // Group points by cookie
    const cookieAmounts = new Map<string, number>();
    savedPoints.forEach((sp) => {
      if (sp.cookie) {
        const current = cookieAmounts.get(sp.cookie.userName) || 0;
        cookieAmounts.set(sp.cookie.userName, current + sp.amount);
      }
    });

    // Build and send message
    const message = this.slackService.buildDailyReport({
      urlCount,
      successCount,
      totalCookieCount: cookies.length,
      logoutCookieCount: invalidCookieCount,
      amount: dayAmount,
      cookieAmounts,
    });

    this.logger.log(`Sending report to user: ${siteUser.loginId}`);

    if (siteUser.slackWebhookUrl) {
      await this.slackService.sendMessage(siteUser.slackWebhookUrl, message);
    } else {
      this.logger.warn(`No Slack webhook URL for user: ${siteUser.loginId}`);
    }
  }
}
