import { Test, TestingModule } from '@nestjs/testing';
import { DashboardService } from './dashboard.service';
import { PrismaService } from '../../prisma/prisma.service';

describe('DashboardService', () => {
  let service: DashboardService;
  let prismaService: jest.Mocked<PrismaService>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        DashboardService,
        {
          provide: PrismaService,
          useValue: {
            savedPoint: {
              aggregate: jest.fn(),
            },
            pointUrl: {
              count: jest.fn(),
            },
          },
        },
      ],
    }).compile();

    service = module.get<DashboardService>(DashboardService);
    prismaService = module.get(PrismaService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('getStats', () => {
    it('should return dashboard statistics', async () => {
      // Mock today's points
      prismaService.savedPoint.aggregate = jest
        .fn()
        .mockResolvedValueOnce({ _sum: { amount: 100 } }) // Today
        .mockResolvedValueOnce({ _sum: { amount: 50 } }) // Yesterday
        .mockResolvedValueOnce({ _sum: { amount: 500 } }) // This week
        .mockResolvedValueOnce({ _sum: { amount: 400 } }); // Last week

      prismaService.pointUrl.count = jest
        .fn()
        .mockResolvedValueOnce(10) // Today's URLs
        .mockResolvedValueOnce(50); // This week's URLs

      const result = await service.getStats();

      expect(result.savedDayPoint).toBe(100);
      expect(result.savedWeekPoint).toBe(500);
      expect(result.pointUrlDayCnt).toBe(10);
      expect(result.pointUrlWeekCnt).toBe(50);
    });

    it('should calculate day-over-day ratio correctly', async () => {
      prismaService.savedPoint.aggregate = jest
        .fn()
        .mockResolvedValueOnce({ _sum: { amount: 100 } }) // Today
        .mockResolvedValueOnce({ _sum: { amount: 50 } }) // Yesterday
        .mockResolvedValueOnce({ _sum: { amount: 500 } }) // This week
        .mockResolvedValueOnce({ _sum: { amount: 400 } }); // Last week

      prismaService.pointUrl.count = jest.fn().mockResolvedValue(0);

      const result = await service.getStats();

      // (100 - 50) / 50 * 100 = 100%
      expect(result.savedDayPointRatioDayBefore).toBe(100);
    });

    it('should calculate week-over-week ratio correctly', async () => {
      prismaService.savedPoint.aggregate = jest
        .fn()
        .mockResolvedValueOnce({ _sum: { amount: 100 } }) // Today
        .mockResolvedValueOnce({ _sum: { amount: 100 } }) // Yesterday
        .mockResolvedValueOnce({ _sum: { amount: 600 } }) // This week
        .mockResolvedValueOnce({ _sum: { amount: 400 } }); // Last week

      prismaService.pointUrl.count = jest.fn().mockResolvedValue(0);

      const result = await service.getStats();

      // (600 - 400) / 400 * 100 = 50%
      expect(result.savedWeekPointRatioWeekBefore).toBe(50);
    });

    it('should return 100% ratio when previous period has zero points but current has points', async () => {
      prismaService.savedPoint.aggregate = jest
        .fn()
        .mockResolvedValueOnce({ _sum: { amount: 100 } }) // Today
        .mockResolvedValueOnce({ _sum: { amount: 0 } }) // Yesterday (no points)
        .mockResolvedValueOnce({ _sum: { amount: 500 } }) // This week
        .mockResolvedValueOnce({ _sum: { amount: 0 } }); // Last week (no points)

      prismaService.pointUrl.count = jest.fn().mockResolvedValue(0);

      const result = await service.getStats();

      expect(result.savedDayPointRatioDayBefore).toBe(100);
      expect(result.savedWeekPointRatioWeekBefore).toBe(100);
    });

    it('should return 0% ratio when both periods have zero points', async () => {
      prismaService.savedPoint.aggregate = jest
        .fn()
        .mockResolvedValueOnce({ _sum: { amount: 0 } }) // Today
        .mockResolvedValueOnce({ _sum: { amount: 0 } }) // Yesterday
        .mockResolvedValueOnce({ _sum: { amount: 0 } }) // This week
        .mockResolvedValueOnce({ _sum: { amount: 0 } }); // Last week

      prismaService.pointUrl.count = jest.fn().mockResolvedValue(0);

      const result = await service.getStats();

      expect(result.savedDayPointRatioDayBefore).toBe(0);
      expect(result.savedWeekPointRatioWeekBefore).toBe(0);
    });

    it('should handle null sums as zero', async () => {
      prismaService.savedPoint.aggregate = jest
        .fn()
        .mockResolvedValueOnce({ _sum: { amount: null } }) // Today
        .mockResolvedValueOnce({ _sum: { amount: null } }) // Yesterday
        .mockResolvedValueOnce({ _sum: { amount: null } }) // This week
        .mockResolvedValueOnce({ _sum: { amount: null } }); // Last week

      prismaService.pointUrl.count = jest.fn().mockResolvedValue(0);

      const result = await service.getStats();

      expect(result.savedDayPoint).toBe(0);
      expect(result.savedWeekPoint).toBe(0);
    });

    it('should round ratio to one decimal place', async () => {
      prismaService.savedPoint.aggregate = jest
        .fn()
        .mockResolvedValueOnce({ _sum: { amount: 100 } }) // Today
        .mockResolvedValueOnce({ _sum: { amount: 30 } }) // Yesterday
        .mockResolvedValueOnce({ _sum: { amount: 500 } }) // This week
        .mockResolvedValueOnce({ _sum: { amount: 300 } }); // Last week

      prismaService.pointUrl.count = jest.fn().mockResolvedValue(0);

      const result = await service.getStats();

      // (100 - 30) / 30 * 100 = 233.333... -> 233.3
      expect(result.savedDayPointRatioDayBefore).toBe(233.3);
      // (500 - 300) / 300 * 100 = 66.666... -> 66.7
      expect(result.savedWeekPointRatioWeekBefore).toBe(66.7);
    });

    it('should handle negative ratios correctly', async () => {
      prismaService.savedPoint.aggregate = jest
        .fn()
        .mockResolvedValueOnce({ _sum: { amount: 50 } }) // Today
        .mockResolvedValueOnce({ _sum: { amount: 100 } }) // Yesterday
        .mockResolvedValueOnce({ _sum: { amount: 200 } }) // This week
        .mockResolvedValueOnce({ _sum: { amount: 400 } }); // Last week

      prismaService.pointUrl.count = jest.fn().mockResolvedValue(0);

      const result = await service.getStats();

      // (50 - 100) / 100 * 100 = -50%
      expect(result.savedDayPointRatioDayBefore).toBe(-50);
      // (200 - 400) / 400 * 100 = -50%
      expect(result.savedWeekPointRatioWeekBefore).toBe(-50);
    });
  });

  describe('date calculations', () => {
    it('should query with correct date ranges', async () => {
      // Fix the current date for predictable testing
      const mockDate = new Date('2024-03-15T10:00:00Z'); // Friday
      jest.useFakeTimers().setSystemTime(mockDate);

      prismaService.savedPoint.aggregate = jest.fn().mockResolvedValue({ _sum: { amount: 0 } });
      prismaService.pointUrl.count = jest.fn().mockResolvedValue(0);

      await service.getStats();

      // Verify the aggregate calls
      expect(prismaService.savedPoint.aggregate).toHaveBeenCalledTimes(4);
      expect(prismaService.pointUrl.count).toHaveBeenCalledTimes(2);

      jest.useRealTimers();
    });

    it('should handle Monday correctly for week calculation', async () => {
      // Test on a Monday
      const mockDate = new Date('2024-03-11T10:00:00Z'); // Monday
      jest.useFakeTimers().setSystemTime(mockDate);

      prismaService.savedPoint.aggregate = jest.fn().mockResolvedValue({ _sum: { amount: 100 } });
      prismaService.pointUrl.count = jest.fn().mockResolvedValue(5);

      const result = await service.getStats();

      expect(result.savedDayPoint).toBe(100);
      expect(prismaService.savedPoint.aggregate).toHaveBeenCalled();

      jest.useRealTimers();
    });

    it('should handle Sunday correctly for week calculation', async () => {
      // Test on a Sunday
      const mockDate = new Date('2024-03-17T10:00:00Z'); // Sunday
      jest.useFakeTimers().setSystemTime(mockDate);

      prismaService.savedPoint.aggregate = jest.fn().mockResolvedValue({ _sum: { amount: 100 } });
      prismaService.pointUrl.count = jest.fn().mockResolvedValue(5);

      const result = await service.getStats();

      expect(result.savedDayPoint).toBe(100);
      expect(prismaService.savedPoint.aggregate).toHaveBeenCalled();

      jest.useRealTimers();
    });
  });

  describe('aggregate queries', () => {
    it('should call aggregate with correct parameters for today', async () => {
      const mockDate = new Date('2024-03-15T10:00:00Z');
      jest.useFakeTimers().setSystemTime(mockDate);

      prismaService.savedPoint.aggregate = jest.fn().mockResolvedValue({ _sum: { amount: 0 } });
      prismaService.pointUrl.count = jest.fn().mockResolvedValue(0);

      await service.getStats();

      // First call should be for today's points
      const aggregateMock = prismaService.savedPoint.aggregate as jest.Mock;
      const todayCall = aggregateMock.mock.calls[0][0];
      expect(todayCall.where.createdDate.gte).toBeInstanceOf(Date);
      expect(todayCall.where.createdDate.lt).toBeInstanceOf(Date);
      expect(todayCall._sum.amount).toBe(true);

      jest.useRealTimers();
    });

    it('should call count with correct parameters for today URLs', async () => {
      const mockDate = new Date('2024-03-15T10:00:00Z');
      jest.useFakeTimers().setSystemTime(mockDate);

      prismaService.savedPoint.aggregate = jest.fn().mockResolvedValue({ _sum: { amount: 0 } });
      prismaService.pointUrl.count = jest.fn().mockResolvedValue(0);

      await service.getStats();

      const countMock = prismaService.pointUrl.count as jest.Mock;
      const todayUrlCall = countMock.mock.calls[0][0];
      expect(todayUrlCall.where.createdDate.gte).toBeInstanceOf(Date);
      expect(todayUrlCall.where.createdDate.lt).toBeInstanceOf(Date);

      jest.useRealTimers();
    });
  });
});
