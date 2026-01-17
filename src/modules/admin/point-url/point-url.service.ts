import { Injectable, NotFoundException, Logger } from '@nestjs/common';
import { PrismaService } from '../../prisma/prisma.service';
import { CreatePointUrlDto, UpdatePointUrlDto, PointUrlResponseDto, classifyUrlType } from './dto/point-url.dto';

@Injectable()
export class PointUrlService {
  private readonly logger = new Logger(PointUrlService.name);

  constructor(private readonly prisma: PrismaService) {}

  async findAll(): Promise<PointUrlResponseDto[]> {
    this.logger.log('Getting all point URLs');
    const pointUrls = await this.prisma.pointUrl.findMany({
      orderBy: { createdDate: 'desc' },
    });
    return pointUrls.map(PointUrlResponseDto.from);
  }

  async findOne(id: bigint): Promise<PointUrlResponseDto> {
    this.logger.log(`Getting point URL: ${id}`);
    const pointUrl = await this.prisma.pointUrl.findUnique({
      where: { id },
    });
    if (!pointUrl) {
      throw new NotFoundException(`PointUrl with ID ${id} not found`);
    }
    return PointUrlResponseDto.from(pointUrl);
  }

  async findByUrl(url: string) {
    return this.prisma.pointUrl.findFirst({
      where: { url },
    });
  }

  async create(dto: CreatePointUrlDto): Promise<PointUrlResponseDto> {
    this.logger.log(`Creating point URL: ${dto.url}`);

    const pointUrlType = classifyUrlType(dto.url);

    const pointUrl = await this.prisma.pointUrl.create({
      data: {
        url: dto.url,
        name: pointUrlType,
        pointUrlType,
        permanent: dto.permanent ?? false,
      },
    });

    this.logger.log(`Point URL created successfully: ${pointUrl.id}`);
    return PointUrlResponseDto.from(pointUrl);
  }

  async createMany(urls: string[]): Promise<number> {
    const existingUrls = await this.prisma.pointUrl.findMany({
      where: { url: { in: urls } },
      select: { url: true },
    });

    const existingUrlSet = new Set(existingUrls.map((p) => p.url));
    const newUrls = urls.filter((url) => !existingUrlSet.has(url));

    if (newUrls.length === 0) {
      return 0;
    }

    const data = newUrls.map((url) => {
      const pointUrlType = classifyUrlType(url);
      return {
        url,
        name: pointUrlType,
        pointUrlType,
        permanent: false,
      };
    });

    const result = await this.prisma.pointUrl.createMany({
      data,
      skipDuplicates: true,
    });

    this.logger.log(`Created ${result.count} new point URLs`);
    return result.count;
  }

  async update(id: bigint, dto: UpdatePointUrlDto): Promise<PointUrlResponseDto> {
    this.logger.log(`Updating point URL: ${id}`);

    const existing = await this.prisma.pointUrl.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`PointUrl with ID ${id} not found`);
    }

    const url = dto.url ?? existing.url;
    const pointUrlType = dto.url ? classifyUrlType(dto.url) : existing.pointUrlType;

    const pointUrl = await this.prisma.pointUrl.update({
      where: { id },
      data: {
        url,
        name: pointUrlType ?? existing.name,
        pointUrlType,
        permanent: dto.permanent ?? existing.permanent,
      },
    });

    this.logger.log(`Point URL updated successfully: ${id}`);
    return PointUrlResponseDto.from(pointUrl);
  }

  async delete(id: bigint): Promise<void> {
    this.logger.log(`Deleting point URL: ${id}`);

    const existing = await this.prisma.pointUrl.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`PointUrl with ID ${id} not found`);
    }

    await this.prisma.pointUrl.delete({ where: { id } });
    this.logger.log(`Point URL deleted successfully: ${id}`);
  }

  async togglePermanent(id: bigint): Promise<PointUrlResponseDto> {
    this.logger.log(`Toggling permanent status for point URL: ${id}`);

    const existing = await this.prisma.pointUrl.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`PointUrl with ID ${id} not found`);
    }

    const pointUrl = await this.prisma.pointUrl.update({
      where: { id },
      data: {
        permanent: !existing.permanent,
      },
    });

    this.logger.log(`Point URL permanent status toggled: ${id} -> ${pointUrl.permanent}`);
    return PointUrlResponseDto.from(pointUrl);
  }

  async findUnprocessedUrls(cookieId: bigint) {
    // Find point URLs that haven't been processed for this cookie
    const processedUrls = await this.prisma.pointUrlCookie.findMany({
      where: { cookieId },
      select: { pointUrlId: true },
    });

    const processedUrlIds = processedUrls.map((p) => p.pointUrlId);

    return this.prisma.pointUrl.findMany({
      where: {
        id: { notIn: processedUrlIds },
        pointUrlType: { in: ['NAVER', 'OFW_NAVER'] },
      },
      orderBy: { createdDate: 'desc' },
    });
  }

  async findByCreatedDateBetween(start: Date, end: Date) {
    return this.prisma.pointUrl.findMany({
      where: {
        createdDate: {
          gte: start,
          lte: end,
        },
      },
    });
  }
}
