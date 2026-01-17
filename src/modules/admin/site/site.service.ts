import { Injectable, NotFoundException, Logger } from '@nestjs/common';
import { PrismaService } from '../../prisma/prisma.service';
import { CreateSiteDto, UpdateSiteDto, SiteResponseDto } from './dto/site.dto';

@Injectable()
export class SiteService {
  private readonly logger = new Logger(SiteService.name);

  constructor(private readonly prisma: PrismaService) {}

  async findAll(): Promise<SiteResponseDto[]> {
    this.logger.log('Getting all sites');
    const sites = await this.prisma.site.findMany({
      orderBy: { createdDate: 'desc' },
    });
    return sites.map(SiteResponseDto.from);
  }

  async findOne(id: bigint): Promise<SiteResponseDto> {
    this.logger.log(`Getting site: ${id}`);
    const site = await this.prisma.site.findUnique({
      where: { id },
    });
    if (!site) {
      throw new NotFoundException(`Site with ID ${id} not found`);
    }
    return SiteResponseDto.from(site);
  }

  async findByName(name: string) {
    return this.prisma.site.findFirst({
      where: { name },
    });
  }

  async create(dto: CreateSiteDto): Promise<SiteResponseDto> {
    this.logger.log(`Creating site: ${dto.name}`);
    const site = await this.prisma.site.create({
      data: {
        name: dto.name,
        domain: dto.domain,
        url: dto.url,
      },
    });
    this.logger.log(`Site created successfully: ${site.id}`);
    return SiteResponseDto.from(site);
  }

  async update(id: bigint, dto: UpdateSiteDto): Promise<SiteResponseDto> {
    this.logger.log(`Updating site: ${id}`);

    const existing = await this.prisma.site.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`Site with ID ${id} not found`);
    }

    const site = await this.prisma.site.update({
      where: { id },
      data: {
        name: dto.name ?? existing.name,
        domain: dto.domain ?? existing.domain,
        url: dto.url ?? existing.url,
      },
    });

    this.logger.log(`Site updated successfully: ${id}`);
    return SiteResponseDto.from(site);
  }

  async delete(id: bigint): Promise<void> {
    this.logger.log(`Deleting site: ${id}`);

    const existing = await this.prisma.site.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`Site with ID ${id} not found`);
    }

    await this.prisma.site.delete({ where: { id } });
    this.logger.log(`Site deleted successfully: ${id}`);
  }
}
