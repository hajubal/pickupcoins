import { Test, TestingModule } from '@nestjs/testing';
import { NotFoundException } from '@nestjs/common';
import { PointUrlService } from './point-url.service';
import { PrismaService } from '../../prisma/prisma.service';
import { PointUrl, PointUrlType } from '@prisma/client';
import { classifyUrlType } from './dto/point-url.dto';

describe('PointUrlService', () => {
  let service: PointUrlService;
  let prismaService: jest.Mocked<PrismaService>;

  const mockPointUrl: PointUrl = {
    id: BigInt(1),
    name: 'NAVER',
    url: 'https://campaign2-api.naver.com/point/123',
    pointUrlType: 'NAVER' as PointUrlType,
    permanent: false,
    createdDate: new Date(),
    modifiedDate: new Date(),
    createdBy: null,
    lastModifiedBy: null,
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        PointUrlService,
        {
          provide: PrismaService,
          useValue: {
            pointUrl: {
              findMany: jest.fn(),
              findUnique: jest.fn(),
              findFirst: jest.fn(),
              create: jest.fn(),
              createMany: jest.fn(),
              update: jest.fn(),
              delete: jest.fn(),
            },
            pointUrlCookie: {
              findMany: jest.fn(),
            },
          },
        },
      ],
    }).compile();

    service = module.get<PointUrlService>(PointUrlService);
    prismaService = module.get(PrismaService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('classifyUrlType', () => {
    it('should classify NAVER URL correctly', () => {
      const url = 'https://campaign2-api.naver.com/point/123';
      expect(classifyUrlType(url)).toBe('NAVER');
    });

    it('should classify OFW_NAVER URL correctly', () => {
      const url = 'https://ofw.adison.co/u/naverpay/456';
      expect(classifyUrlType(url)).toBe('OFW_NAVER');
    });

    it('should classify unknown URL as UNSUPPORT', () => {
      const url = 'https://example.com/unknown';
      expect(classifyUrlType(url)).toBe('UNSUPPORT');
    });
  });

  describe('findAll', () => {
    it('should return all point URLs', async () => {
      const mockPointUrls = [mockPointUrl, { ...mockPointUrl, id: BigInt(2) }];
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue(mockPointUrls);

      const result = await service.findAll();

      expect(result).toHaveLength(2);
      expect(prismaService.pointUrl.findMany).toHaveBeenCalledWith({
        orderBy: { createdDate: 'desc' },
      });
    });
  });

  describe('findOne', () => {
    it('should return a point URL by id', async () => {
      prismaService.pointUrl.findUnique = jest.fn().mockResolvedValue(mockPointUrl);

      const result = await service.findOne(BigInt(1));

      expect(result.id).toBe(mockPointUrl.id.toString());
      expect(result.url).toBe(mockPointUrl.url);
    });

    it('should throw NotFoundException when point URL not found', async () => {
      prismaService.pointUrl.findUnique = jest.fn().mockResolvedValue(null);

      await expect(service.findOne(BigInt(999))).rejects.toThrow(NotFoundException);
    });
  });

  describe('findByUrl', () => {
    it('should return point URL by URL string', async () => {
      prismaService.pointUrl.findFirst = jest.fn().mockResolvedValue(mockPointUrl);

      const result = await service.findByUrl(mockPointUrl.url);

      expect(result).toEqual(mockPointUrl);
    });

    it('should return null when URL not found', async () => {
      prismaService.pointUrl.findFirst = jest.fn().mockResolvedValue(null);

      const result = await service.findByUrl('https://unknown.com');

      expect(result).toBeNull();
    });
  });

  describe('create', () => {
    it('should create a new point URL with auto-classified type', async () => {
      prismaService.pointUrl.create = jest.fn().mockResolvedValue(mockPointUrl);

      const result = await service.create({
        url: 'https://campaign2-api.naver.com/point/123',
      });

      expect(result.pointUrlType).toBe('NAVER');
      expect(prismaService.pointUrl.create).toHaveBeenCalledWith({
        data: {
          url: 'https://campaign2-api.naver.com/point/123',
          name: 'NAVER',
          pointUrlType: 'NAVER',
          permanent: false,
        },
      });
    });

    it('should create point URL with permanent flag', async () => {
      const permanentUrl = { ...mockPointUrl, permanent: true };
      prismaService.pointUrl.create = jest.fn().mockResolvedValue(permanentUrl);

      await service.create({
        url: 'https://campaign2-api.naver.com/point/123',
        permanent: true,
      });

      expect(prismaService.pointUrl.create).toHaveBeenCalledWith({
        data: expect.objectContaining({
          permanent: true,
        }),
      });
    });
  });

  describe('createMany', () => {
    it('should create multiple URLs skipping duplicates', async () => {
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.createMany = jest.fn().mockResolvedValue({ count: 2 });

      const urls = ['https://campaign2-api.naver.com/point/123', 'https://ofw.adison.co/u/naverpay/456'];

      const result = await service.createMany(urls);

      expect(result).toBe(2);
    });

    it('should skip existing URLs', async () => {
      prismaService.pointUrl.findMany = jest
        .fn()
        .mockResolvedValue([{ url: 'https://campaign2-api.naver.com/point/123' }]);
      prismaService.pointUrl.createMany = jest.fn().mockResolvedValue({ count: 1 });

      const urls = ['https://campaign2-api.naver.com/point/123', 'https://ofw.adison.co/u/naverpay/456'];

      const result = await service.createMany(urls);

      expect(result).toBe(1);
    });

    it('should return 0 when all URLs already exist', async () => {
      prismaService.pointUrl.findMany = jest
        .fn()
        .mockResolvedValue([{ url: 'https://campaign2-api.naver.com/point/123' }]);

      const result = await service.createMany(['https://campaign2-api.naver.com/point/123']);

      expect(result).toBe(0);
      expect(prismaService.pointUrl.createMany).not.toHaveBeenCalled();
    });
  });

  describe('update', () => {
    it('should update an existing point URL', async () => {
      prismaService.pointUrl.findUnique = jest.fn().mockResolvedValue(mockPointUrl);
      const updatedUrl = {
        ...mockPointUrl,
        url: 'https://campaign2-api.naver.com/point/999',
      };
      prismaService.pointUrl.update = jest.fn().mockResolvedValue(updatedUrl);

      const result = await service.update(BigInt(1), {
        url: 'https://campaign2-api.naver.com/point/999',
      });

      expect(result.url).toBe('https://campaign2-api.naver.com/point/999');
    });

    it('should throw NotFoundException when point URL not found', async () => {
      prismaService.pointUrl.findUnique = jest.fn().mockResolvedValue(null);

      await expect(service.update(BigInt(999), { url: 'test' })).rejects.toThrow(NotFoundException);
    });

    it('should reclassify URL type when URL is updated', async () => {
      prismaService.pointUrl.findUnique = jest.fn().mockResolvedValue(mockPointUrl);
      const updatedUrl = {
        ...mockPointUrl,
        url: 'https://ofw.adison.co/u/naverpay/456',
        pointUrlType: 'OFW_NAVER' as PointUrlType,
      };
      prismaService.pointUrl.update = jest.fn().mockResolvedValue(updatedUrl);

      await service.update(BigInt(1), {
        url: 'https://ofw.adison.co/u/naverpay/456',
      });

      expect(prismaService.pointUrl.update).toHaveBeenCalledWith({
        where: { id: BigInt(1) },
        data: expect.objectContaining({
          pointUrlType: 'OFW_NAVER',
        }),
      });
    });
  });

  describe('delete', () => {
    it('should delete a point URL', async () => {
      prismaService.pointUrl.findUnique = jest.fn().mockResolvedValue(mockPointUrl);
      prismaService.pointUrl.delete = jest.fn().mockResolvedValue(mockPointUrl);

      await service.delete(BigInt(1));

      expect(prismaService.pointUrl.delete).toHaveBeenCalledWith({
        where: { id: BigInt(1) },
      });
    });

    it('should throw NotFoundException when point URL not found', async () => {
      prismaService.pointUrl.findUnique = jest.fn().mockResolvedValue(null);

      await expect(service.delete(BigInt(999))).rejects.toThrow(NotFoundException);
    });
  });

  describe('togglePermanent', () => {
    it('should toggle permanent from false to true', async () => {
      prismaService.pointUrl.findUnique = jest.fn().mockResolvedValue(mockPointUrl);
      const toggledUrl = { ...mockPointUrl, permanent: true };
      prismaService.pointUrl.update = jest.fn().mockResolvedValue(toggledUrl);

      const result = await service.togglePermanent(BigInt(1));

      expect(result.permanent).toBe(true);
    });

    it('should toggle permanent from true to false', async () => {
      const permanentUrl = { ...mockPointUrl, permanent: true };
      prismaService.pointUrl.findUnique = jest.fn().mockResolvedValue(permanentUrl);
      const toggledUrl = { ...mockPointUrl, permanent: false };
      prismaService.pointUrl.update = jest.fn().mockResolvedValue(toggledUrl);

      const result = await service.togglePermanent(BigInt(1));

      expect(result.permanent).toBe(false);
    });

    it('should throw NotFoundException when point URL not found', async () => {
      prismaService.pointUrl.findUnique = jest.fn().mockResolvedValue(null);

      await expect(service.togglePermanent(BigInt(999))).rejects.toThrow(NotFoundException);
    });
  });

  describe('findUnprocessedUrls', () => {
    it('should return URLs not processed for given cookie', async () => {
      prismaService.pointUrlCookie.findMany = jest.fn().mockResolvedValue([{ pointUrlId: BigInt(1) }]);
      const unprocessedUrls = [{ ...mockPointUrl, id: BigInt(2) }];
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue(unprocessedUrls);

      const result = await service.findUnprocessedUrls(BigInt(1));

      expect(result).toHaveLength(1);
      expect(prismaService.pointUrl.findMany).toHaveBeenCalledWith({
        where: {
          id: { notIn: [BigInt(1)] },
          pointUrlType: { in: ['NAVER', 'OFW_NAVER'] },
        },
        orderBy: { createdDate: 'desc' },
      });
    });

    it('should return all supported URLs when no URLs processed', async () => {
      prismaService.pointUrlCookie.findMany = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue([mockPointUrl]);

      const result = await service.findUnprocessedUrls(BigInt(1));

      expect(result).toHaveLength(1);
      expect(prismaService.pointUrl.findMany).toHaveBeenCalledWith({
        where: {
          id: { notIn: [] },
          pointUrlType: { in: ['NAVER', 'OFW_NAVER'] },
        },
        orderBy: { createdDate: 'desc' },
      });
    });
  });

  describe('findByCreatedDateBetween', () => {
    it('should return URLs created within date range', async () => {
      const start = new Date('2024-01-01');
      const end = new Date('2024-01-31');
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue([mockPointUrl]);

      const result = await service.findByCreatedDateBetween(start, end);

      expect(prismaService.pointUrl.findMany).toHaveBeenCalledWith({
        where: {
          createdDate: {
            gte: start,
            lte: end,
          },
        },
      });
      expect(result).toHaveLength(1);
    });
  });
});
