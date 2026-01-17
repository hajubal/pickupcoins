import { Injectable, NotFoundException, Logger } from '@nestjs/common';
import { PrismaService } from '../../prisma/prisma.service';
import { SavedPointResponseDto, SavedPointQueryDto, SavedPointListResponseDto } from './dto/saved-point.dto';

@Injectable()
export class SavedPointService {
  private readonly logger = new Logger(SavedPointService.name);

  constructor(private readonly prisma: PrismaService) {}

  async findAll(query: SavedPointQueryDto): Promise<SavedPointListResponseDto> {
    const { page = 1, limit = 20, startDate, endDate } = query;
    const skip = (page - 1) * limit;

    this.logger.log(`Getting saved points: page=${page}, limit=${limit}, startDate=${startDate}, endDate=${endDate}`);

    const where: Record<string, unknown> = {};

    if (startDate && endDate) {
      where.createdDate = {
        gte: new Date(`${startDate}T00:00:00`),
        lte: new Date(`${endDate}T23:59:59`),
      };
    }

    const [savedPoints, total] = await Promise.all([
      this.prisma.savedPoint.findMany({
        where,
        include: { cookie: true },
        orderBy: { createdDate: 'desc' },
        skip,
        take: limit,
      }),
      this.prisma.savedPoint.count({ where }),
    ]);

    return {
      content: savedPoints.map(SavedPointResponseDto.from),
      currentPage: page,
      totalItems: total,
      totalPages: Math.ceil(total / limit),
    };
  }

  async findOne(id: bigint): Promise<SavedPointResponseDto> {
    this.logger.log(`Getting saved point: ${id}`);
    const savedPoint = await this.prisma.savedPoint.findUnique({
      where: { id },
      include: { cookie: true },
    });
    if (!savedPoint) {
      throw new NotFoundException(`SavedPoint with ID ${id} not found`);
    }
    return SavedPointResponseDto.from(savedPoint);
  }

  async delete(id: bigint): Promise<void> {
    this.logger.log(`Deleting saved point: ${id}`);

    const existing = await this.prisma.savedPoint.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`SavedPoint with ID ${id} not found`);
    }

    await this.prisma.savedPoint.delete({ where: { id } });
    this.logger.log(`Saved point deleted successfully: ${id}`);
  }

  async create(cookieId: bigint, amount: number, responseBody?: string): Promise<SavedPointResponseDto> {
    const savedPoint = await this.prisma.savedPoint.create({
      data: {
        cookieId,
        amount,
        responseBody,
      },
      include: { cookie: true },
    });
    return SavedPointResponseDto.from(savedPoint);
  }

  async findByCreatedDateBetween(start: Date, end: Date) {
    return this.prisma.savedPoint.findMany({
      where: {
        createdDate: {
          gte: start,
          lte: end,
        },
      },
      include: { cookie: true },
    });
  }

  async getTotalAmountByDateRange(start: Date, end: Date): Promise<number> {
    const result = await this.prisma.savedPoint.aggregate({
      where: {
        createdDate: {
          gte: start,
          lte: end,
        },
      },
      _sum: {
        amount: true,
      },
    });
    return result._sum.amount || 0;
  }
}
