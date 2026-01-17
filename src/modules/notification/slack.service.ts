import { Injectable, Logger } from '@nestjs/common';
import axios from 'axios';

export interface SlackMessageResponse {
  ok: boolean;
  error?: string;
}

@Injectable()
export class SlackService {
  private readonly logger = new Logger(SlackService.name);

  /**
   * Send message to Slack webhook
   */
  async sendMessage(webhookUrl: string, message: string): Promise<SlackMessageResponse> {
    if (!webhookUrl) {
      this.logger.warn('Slack webhook URL is not set');
      return { ok: false, error: 'Webhook URL not set' };
    }

    if (!message) {
      this.logger.warn('Slack message is empty');
      return { ok: false, error: 'Message is empty' };
    }

    try {
      const response = await axios.post(
        webhookUrl,
        { text: message },
        {
          headers: {
            'Content-Type': 'application/json',
          },
          timeout: 10000,
        },
      );

      this.logger.log(`Slack message sent successfully: ${response.status}`);
      return { ok: true };
    } catch (error) {
      const errorMessage = (error as Error).message;
      this.logger.error(`Failed to send Slack message: ${errorMessage}`);
      return { ok: false, error: errorMessage };
    }
  }

  /**
   * Build daily report message
   */
  buildDailyReport(data: {
    urlCount: number;
    successCount: number;
    totalCookieCount: number;
    logoutCookieCount: number;
    amount: number;
    cookieAmounts?: Map<string, number>;
  }): string {
    let message = `
📊 *일일 포인트 수집 리포트*
━━━━━━━━━━━━━━━━━━━━━
• 수집한 URL: ${data.urlCount} 개
• 수집 성공한 URL: ${data.successCount} 개
• 전체 쿠키 수(로그아웃 수): ${data.totalCookieCount} (${data.logoutCookieCount})
• 수집한 금액: ${data.amount}원
━━━━━━━━━━━━━━━━━━━━━`;

    if (data.cookieAmounts && data.cookieAmounts.size > 0) {
      message += '\n\n*쿠키별 적립 금액:*';
      data.cookieAmounts.forEach((amount, cookieName) => {
        message += `\n  • ${cookieName}: ${amount}원`;
      });
    }

    return message;
  }

  /**
   * Send cookie invalidation alert
   */
  async sendCookieInvalidAlert(webhookUrl: string, userName: string): Promise<SlackMessageResponse> {
    const message = `
⚠️ *쿠키 만료 알림*
━━━━━━━━━━━━━━━━━━━━━
사용자: ${userName}
상태: 쿠키가 만료되었습니다.
조치: 새로운 쿠키로 업데이트해주세요.
━━━━━━━━━━━━━━━━━━━━━`;

    return this.sendMessage(webhookUrl, message);
  }
}
