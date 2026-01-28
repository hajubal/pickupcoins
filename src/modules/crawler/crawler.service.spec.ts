import { Test, TestingModule } from '@nestjs/testing';
import { CrawlerService } from './crawler.service';
import { PrismaService } from '../prisma/prisma.service';
import { ClienCrawler } from './crawlers/clien.crawler';
import { RuliwebCrawler } from './crawlers/ruliweb.crawler';
import { CrawledUrl } from './crawlers/base.crawler';

describe('CrawlerService', () => {
  let service: CrawlerService;
  let prismaService: jest.Mocked<PrismaService>;
  let clienCrawler: jest.Mocked<ClienCrawler>;
  let ruliwebCrawler: jest.Mocked<RuliwebCrawler>;

  const mockSite = {
    id: BigInt(1),
    name: 'clien',
    domain: 'clien.net',
    url: 'https://www.clien.net/service/board/jirum',
    createdDate: new Date(),
    modifiedDate: new Date(),
  };

  const mockCrawledUrls: CrawledUrl[] = [
    { url: 'https://campaign2-api.naver.com/point/123' },
    { url: 'https://ofw.adison.co/u/naverpay/456' },
  ];

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        CrawlerService,
        {
          provide: PrismaService,
          useValue: {
            site: {
              findFirst: jest.fn(),
            },
            pointUrl: {
              findMany: jest.fn(),
              createMany: jest.fn(),
            },
          },
        },
        {
          provide: ClienCrawler,
          useValue: {
            getSiteData: jest.fn().mockReturnValue({
              siteName: 'clien',
              domain: 'clien.net',
            }),
            crawl: jest.fn(),
          },
        },
        {
          provide: RuliwebCrawler,
          useValue: {
            getSiteData: jest.fn().mockReturnValue({
              siteName: 'ruliweb',
              domain: 'ruliweb.com',
            }),
            crawl: jest.fn(),
          },
        },
      ],
    }).compile();

    service = module.get<CrawlerService>(CrawlerService);
    prismaService = module.get(PrismaService);
    clienCrawler = module.get(ClienCrawler);
    ruliwebCrawler = module.get(RuliwebCrawler);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('savingPointUrl', () => {
    it('should crawl all sites and save new URLs', async () => {
      prismaService.site.findFirst = jest.fn().mockResolvedValue(mockSite);
      clienCrawler.crawl = jest.fn().mockResolvedValue(mockCrawledUrls);
      ruliwebCrawler.crawl = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.createMany = jest.fn().mockResolvedValue({ count: 2 });

      const result = await service.savingPointUrl();

      expect(result).toBe(2);
      expect(clienCrawler.crawl).toHaveBeenCalled();
      expect(ruliwebCrawler.crawl).toHaveBeenCalled();
    });

    it('should skip duplicate URLs', async () => {
      prismaService.site.findFirst = jest.fn().mockResolvedValue(mockSite);
      clienCrawler.crawl = jest.fn().mockResolvedValue(mockCrawledUrls);
      ruliwebCrawler.crawl = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.findMany = jest
        .fn()
        .mockResolvedValue([{ url: 'https://campaign2-api.naver.com/point/123' }]);
      prismaService.pointUrl.createMany = jest.fn().mockResolvedValue({ count: 1 });

      const result = await service.savingPointUrl();

      expect(result).toBe(1);
    });

    it('should return 0 when no new URLs found', async () => {
      prismaService.site.findFirst = jest.fn().mockResolvedValue(mockSite);
      clienCrawler.crawl = jest.fn().mockResolvedValue(mockCrawledUrls);
      ruliwebCrawler.crawl = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue(mockCrawledUrls.map((u) => ({ url: u.url })));

      const result = await service.savingPointUrl();

      expect(result).toBe(0);
      expect(prismaService.pointUrl.createMany).not.toHaveBeenCalled();
    });

    it('should continue when site is not found in database', async () => {
      prismaService.site.findFirst = jest.fn().mockResolvedValue(null);
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue([]);

      const result = await service.savingPointUrl();

      expect(result).toBe(0);
      expect(clienCrawler.crawl).not.toHaveBeenCalled();
    });

    it('should handle crawler errors gracefully', async () => {
      prismaService.site.findFirst = jest.fn().mockResolvedValue(mockSite);
      clienCrawler.crawl = jest.fn().mockRejectedValue(new Error('Crawl failed'));
      ruliwebCrawler.crawl = jest.fn().mockResolvedValue(mockCrawledUrls);
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.createMany = jest.fn().mockResolvedValue({ count: 2 });

      const result = await service.savingPointUrl();

      expect(result).toBe(2);
    });

    it('should classify URL types correctly when saving', async () => {
      prismaService.site.findFirst = jest.fn().mockResolvedValue(mockSite);
      clienCrawler.crawl = jest.fn().mockResolvedValue(mockCrawledUrls);
      ruliwebCrawler.crawl = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.createMany = jest.fn().mockResolvedValue({ count: 2 });

      await service.savingPointUrl();

      expect(prismaService.pointUrl.createMany).toHaveBeenCalledWith({
        data: expect.arrayContaining([
          expect.objectContaining({
            url: 'https://campaign2-api.naver.com/point/123',
            pointUrlType: 'NAVER',
          }),
          expect.objectContaining({
            url: 'https://ofw.adison.co/u/naverpay/456',
            pointUrlType: 'OFW_NAVER',
          }),
        ]),
        skipDuplicates: true,
      });
    });
  });

  describe('crawlSite', () => {
    it('should crawl specific site', async () => {
      prismaService.site.findFirst = jest.fn().mockResolvedValue(mockSite);
      clienCrawler.crawl = jest.fn().mockResolvedValue(mockCrawledUrls);

      const result = await service.crawlSite('clien');

      expect(result).toEqual(mockCrawledUrls);
      expect(clienCrawler.crawl).toHaveBeenCalledWith(mockSite.url);
    });

    it('should throw error when crawler not found', async () => {
      await expect(service.crawlSite('unknown')).rejects.toThrow('Crawler not found for site: unknown');
    });

    it('should throw error when site not found in database', async () => {
      prismaService.site.findFirst = jest.fn().mockResolvedValue(null);

      await expect(service.crawlSite('clien')).rejects.toThrow('Site not found in database: clien');
    });
  });

  describe('getCrawlerInfo', () => {
    it('should return list of available crawlers', () => {
      const result = service.getCrawlerInfo();

      expect(result).toHaveLength(2);
      expect(result).toContainEqual({
        siteName: 'clien',
        domain: 'clien.net',
      });
      expect(result).toContainEqual({
        siteName: 'ruliweb',
        domain: 'ruliweb.com',
      });
    });
  });

  describe('URL deduplication', () => {
    it('should remove duplicate URLs within the same crawl', async () => {
      const duplicateUrls: CrawledUrl[] = [
        { url: 'https://campaign2-api.naver.com/point/123' },
        { url: 'https://campaign2-api.naver.com/point/123' },
        { url: 'https://campaign2-api.naver.com/point/456' },
      ];

      prismaService.site.findFirst = jest.fn().mockResolvedValue(mockSite);
      clienCrawler.crawl = jest.fn().mockResolvedValue(duplicateUrls);
      ruliwebCrawler.crawl = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.findMany = jest.fn().mockResolvedValue([]);
      prismaService.pointUrl.createMany = jest.fn().mockResolvedValue({ count: 2 });

      await service.savingPointUrl();

      const createManyMock = prismaService.pointUrl.createMany as jest.Mock;
      const createManyCall = createManyMock.mock.calls[0][0];
      expect(createManyCall.data).toHaveLength(2);
    });
  });
});
