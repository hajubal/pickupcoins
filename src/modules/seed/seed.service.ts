import { Injectable, Logger, OnApplicationBootstrap } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import * as bcrypt from 'bcrypt';

/**
 * 초기 데이터 시딩 서비스
 *
 * 어플리케이션 시작 시 필수 데이터가 없으면 자동으로 생성합니다:
 * - 관리자 계정 (admin/admin123)
 * - 기본 사이트 설정 (네이버 클리앙)
 */
@Injectable()
export class SeedService implements OnApplicationBootstrap {
  private readonly logger = new Logger(SeedService.name);

  constructor(private readonly prisma: PrismaService) {}

  async onApplicationBootstrap() {
    this.logger.log('Checking initial data...');
    await this.seedAdminUser();
    await this.seedDefaultSites();
    this.logger.log('Initial data check completed');
  }

  /**
   * 관리자 계정 시딩
   * admin 계정이 없으면 기본 관리자 계정 생성
   */
  private async seedAdminUser() {
    const existingAdmin = await this.prisma.siteUser.findUnique({
      where: { loginId: 'admin' },
    });

    if (!existingAdmin) {
      const hashedPassword = await bcrypt.hash('admin123', 10);

      await this.prisma.siteUser.create({
        data: {
          loginId: 'admin',
          userName: 'Administrator',
          password: hashedPassword,
          active: true,
          modifiedDate: new Date(),
        },
      });

      this.logger.log('Created default admin user (admin/admin123)');
    } else {
      this.logger.log('Admin user already exists, skipping...');
    }
  }

  /**
   * 기본 사이트 시딩
   * 사이트가 하나도 없으면 기본 사이트 설정 생성
   */
  private async seedDefaultSites() {
    const siteCount = await this.prisma.site.count();

    if (siteCount === 0) {
      const defaultSites = [
        {
          name: '클리앙',
          url: 'https://www.clien.net/service/board/jirum',
        },
        {
          name: '루리웹',
          url: 'https://bbs.ruliweb.com/ps/board/1020',
        },
      ];

      for (const site of defaultSites) {
        await this.prisma.site.create({ data: site });
        this.logger.log(`Created default site: ${site.name}`);
      }
    } else {
      this.logger.log(`${siteCount} site(s) already exist, skipping...`);
    }
  }
}
