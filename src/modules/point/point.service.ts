import { Injectable, Logger } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { ExchangeService, ExchangeDto } from './exchange.service';

const SITE_NAME_NAVER = 'NAVER';

@Injectable()
export class PointService {
  private readonly logger = new Logger(PointService.name);

  constructor(
    private readonly prisma: PrismaService,
    private readonly exchangeService: ExchangeService,
  ) {}

  /**
   * Process all valid cookies and collect points
   */
  async savePoint(): Promise<{ processed: number; points: number; errors: number }> {
    // Get all valid NAVER cookies
    const cookies = await this.prisma.cookie.findMany({
      where: {
        siteName: SITE_NAME_NAVER,
        isValid: true,
      },
      include: {
        siteUser: true,
      },
    });

    this.logger.debug(`Found ${cookies.length} valid cookies`);

    let totalProcessed = 0;
    let totalPoints = 0;
    let totalErrors = 0;

    for (const cookie of cookies) {
      const exchangeDto: ExchangeDto = {
        cookieId: cookie.id,
        userName: cookie.userName,
        siteName: cookie.siteName,
        cookie: cookie.cookie || '',
        webHookUrl: cookie.siteUser?.slackWebhookUrl,
      };

      // Get uncalled point URLs for this cookie
      const pointUrls = await this.getUnprocessedUrls(cookie.id, cookie.userName);
      this.logger.log(`Not called URL count for ${cookie.userName}: ${pointUrls.length}`);

      for (const pointUrl of pointUrls) {
        try {
          const result = await this.exchangeService.exchange(pointUrl, exchangeDto);
          totalProcessed++;

          if (result.success) {
            totalPoints += result.pointsSaved;
          }

          if (result.invalidCookie) {
            // Cookie is invalid, skip remaining URLs for this cookie
            this.logger.warn(`Cookie invalidated for user: ${cookie.userName}`);
            break;
          }
        } catch (error) {
          this.logger.error(`Failed to process URL ${pointUrl.url}`, error);
          totalErrors++;
        }
      }
    }

    this.logger.log(`Point collection completed. Processed: ${totalProcessed}, Points: ${totalPoints}, Errors: ${totalErrors}`);
    return { processed: totalProcessed, points: totalPoints, errors: totalErrors };
  }

  /**
   * Get point URLs not yet processed for this cookie
   */
  private async getUnprocessedUrls(cookieId: bigint, userName: string) {
    // Get already processed URLs
    const processedUrls = await this.prisma.pointUrlCookie.findMany({
      where: { cookieId },
      select: { pointUrlId: true },
    });
    const processedUrlIds = processedUrls.map((p) => p.pointUrlId);

    // Get unprocessed point URLs
    return this.prisma.pointUrl.findMany({
      where: {
        id: { notIn: processedUrlIds },
        pointUrlType: { in: ['NAVER', 'OFW_NAVER'] },
      },
      orderBy: { createdDate: 'desc' },
    });
  }

  /**
   * Get statistics for today
   */
  async getTodayStats() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    const [savedPoints, pointUrls] = await Promise.all([
      this.prisma.savedPoint.aggregate({
        where: {
          createdDate: { gte: today, lt: tomorrow },
        },
        _sum: { amount: true },
        _count: true,
      }),
      this.prisma.pointUrl.count({
        where: {
          createdDate: { gte: today, lt: tomorrow },
        },
      }),
    ]);

    return {
      totalPointsToday: savedPoints._sum.amount || 0,
      pointTransactionsToday: savedPoints._count,
      newUrlsToday: pointUrls,
    };
  }
}
