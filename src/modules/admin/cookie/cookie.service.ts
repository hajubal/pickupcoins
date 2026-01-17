import { Injectable, NotFoundException, Logger } from '@nestjs/common';
import { PrismaService } from '../../prisma/prisma.service';
import { CreateCookieDto, UpdateCookieDto, CookieResponseDto } from './dto/cookie.dto';

@Injectable()
export class CookieService {
  private readonly logger = new Logger(CookieService.name);

  constructor(private readonly prisma: PrismaService) {}

  async findAll(): Promise<CookieResponseDto[]> {
    this.logger.log('Getting all cookies');
    const cookies = await this.prisma.cookie.findMany({
      orderBy: { createdDate: 'desc' },
    });
    return cookies.map(CookieResponseDto.from);
  }

  async findOne(id: bigint): Promise<CookieResponseDto> {
    this.logger.log(`Getting cookie: ${id}`);
    const cookie = await this.prisma.cookie.findUnique({
      where: { id },
    });
    if (!cookie) {
      throw new NotFoundException(`Cookie with ID ${id} not found`);
    }
    return CookieResponseDto.from(cookie);
  }

  async create(siteUserId: bigint, dto: CreateCookieDto): Promise<CookieResponseDto> {
    this.logger.log(`Creating cookie for site: ${dto.siteName}`);
    const cookie = await this.prisma.cookie.create({
      data: {
        userName: dto.userName,
        siteName: dto.siteName,
        cookie: dto.cookie,
        isValid: dto.isValid ?? true,
        siteUserId,
      },
    });
    this.logger.log(`Cookie created successfully: ${cookie.id}`);
    return CookieResponseDto.from(cookie);
  }

  async update(id: bigint, dto: UpdateCookieDto): Promise<CookieResponseDto> {
    this.logger.log(`Updating cookie: ${id}`);

    const existing = await this.prisma.cookie.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`Cookie with ID ${id} not found`);
    }

    const cookie = await this.prisma.cookie.update({
      where: { id },
      data: {
        userName: dto.userName ?? existing.userName,
        siteName: dto.siteName ?? existing.siteName,
        cookie: dto.cookie ?? existing.cookie,
        isValid: dto.isValid ?? existing.isValid,
      },
    });

    this.logger.log(`Cookie updated successfully: ${id}`);
    return CookieResponseDto.from(cookie);
  }

  async delete(id: bigint): Promise<void> {
    this.logger.log(`Deleting cookie: ${id}`);

    const existing = await this.prisma.cookie.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`Cookie with ID ${id} not found`);
    }

    await this.prisma.cookie.delete({ where: { id } });
    this.logger.log(`Cookie deleted successfully: ${id}`);
  }

  async toggleValidity(id: bigint): Promise<CookieResponseDto> {
    this.logger.log(`Toggling validity for cookie: ${id}`);

    const existing = await this.prisma.cookie.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`Cookie with ID ${id} not found`);
    }

    const cookie = await this.prisma.cookie.update({
      where: { id },
      data: {
        isValid: !existing.isValid,
      },
    });

    this.logger.log(`Cookie validity toggled: ${id} -> ${cookie.isValid}`);
    return CookieResponseDto.from(cookie);
  }

  async findValidCookies(): Promise<CookieResponseDto[]> {
    const cookies = await this.prisma.cookie.findMany({
      where: { isValid: true },
      orderBy: { createdDate: 'desc' },
    });
    return cookies.map(CookieResponseDto.from);
  }
}
