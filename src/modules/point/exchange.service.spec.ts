import { Test, TestingModule } from '@nestjs/testing';
import { ConfigService } from '@nestjs/config';
import axios from 'axios';
import { ExchangeService, ExchangeDto } from './exchange.service';
import { PrismaService } from '../prisma/prisma.service';
import { PointUrl } from '@prisma/client';
import { PointUrlType } from '../admin/point-url/dto/point-url.dto';

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('ExchangeService', () => {
  let service: ExchangeService;
  let prismaService: jest.Mocked<PrismaService>;

  const mockPointUrl: PointUrl = {
    id: 1,
    name: 'NAVER',
    url: 'https://campaign2-api.naver.com/point/123',
    pointUrlType: 'NAVER' as PointUrlType,
    permanent: false,
    createdDate: new Date(),
    modifiedDate: new Date(),
    createdBy: null,
    lastModifiedBy: null,
  };

  const mockExchangeDto: ExchangeDto = {
    cookieId: 1,
    userName: 'testuser',
    siteName: 'naver',
    cookie: 'NID=123;JSESSIONID=abc',
    webHookUrl: null,
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ExchangeService,
        {
          provide: PrismaService,
          useValue: {
            cookie: {
              update: jest.fn(),
            },
            savedPoint: {
              create: jest.fn(),
            },
            pointUrlCookie: {
              create: jest.fn(),
            },
            pointUrlCallLog: {
              create: jest.fn(),
            },
          },
        },
        {
          provide: ConfigService,
          useValue: {
            get: jest.fn().mockImplementation((key: string) => {
              const config: Record<string, string> = {
                'naver.saveKeyword': '적립',
                'naver.invalidCookieKeyword': '로그인이 필요',
                'naver.amountPattern': '\\s\\d+원이 적립 됩니다.',
                'naver.userAgent': 'Mozilla/5.0',
              };
              return config[key];
            }),
          },
        },
      ],
    }).compile();

    service = module.get<ExchangeService>(ExchangeService);
    prismaService = module.get(PrismaService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('exchange', () => {
    it('should return success when point is saved', async () => {
      const responseBody = '축하합니다! 10원이 적립 됩니다.';
      mockedAxios.get.mockResolvedValue({
        data: responseBody,
        status: 200,
        headers: {},
      });

      prismaService.cookie.update = jest.fn().mockResolvedValue({});
      prismaService.savedPoint.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCookie.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCallLog.create = jest.fn().mockResolvedValue({});

      const result = await service.exchange(mockPointUrl, mockExchangeDto);

      expect(result.success).toBe(true);
      expect(result.pointsSaved).toBe(10);
      expect(result.invalidCookie).toBe(false);
    });

    it('should extract correct amount from response', async () => {
      // Pattern expects space before number: '\s\d+원이 적립 됩니다.'
      const responseBody = '축하합니다! 100원이 적립 됩니다. 오늘의 포인트!';
      mockedAxios.get.mockResolvedValue({
        data: responseBody,
        status: 200,
        headers: {},
      });

      prismaService.savedPoint.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCookie.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCallLog.create = jest.fn().mockResolvedValue({});

      const result = await service.exchange(mockPointUrl, mockExchangeDto);

      expect(result.pointsSaved).toBe(100);
    });

    it('should invalidate cookie when login required message is present', async () => {
      const responseBody = '로그인이 필요합니다. 다시 로그인해주세요.';
      mockedAxios.get.mockResolvedValue({
        data: responseBody,
        status: 200,
        headers: {},
      });

      prismaService.cookie.update = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCookie.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCallLog.create = jest.fn().mockResolvedValue({});

      const result = await service.exchange(mockPointUrl, mockExchangeDto);

      expect(result.success).toBe(false);
      expect(result.invalidCookie).toBe(true);
      expect(prismaService.cookie.update).toHaveBeenCalledWith({
        where: { id: mockExchangeDto.cookieId },
        data: { isValid: false },
      });
    });

    it('should return failure when response is empty', async () => {
      mockedAxios.get.mockResolvedValue({
        data: '',
        status: 200,
        headers: {},
      });

      const result = await service.exchange(mockPointUrl, mockExchangeDto);

      expect(result.success).toBe(false);
      expect(result.error).toBe('Empty response');
    });

    it('should return failure on network error', async () => {
      mockedAxios.get.mockRejectedValue(new Error('Network Error'));

      const result = await service.exchange(mockPointUrl, mockExchangeDto);

      expect(result.success).toBe(false);
      expect(result.error).toBe('Network Error');
    });

    it('should update cookie when Set-Cookie header is present', async () => {
      const responseBody = '50원이 적립 됩니다.';
      mockedAxios.get.mockResolvedValue({
        data: responseBody,
        status: 200,
        headers: {
          'set-cookie': ['NID=newvalue', 'JSESSIONID=newid'],
        },
      });

      prismaService.cookie.update = jest.fn().mockResolvedValue({});
      prismaService.savedPoint.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCookie.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCallLog.create = jest.fn().mockResolvedValue({});

      await service.exchange(mockPointUrl, mockExchangeDto);

      expect(prismaService.cookie.update).toHaveBeenCalledWith({
        where: { id: mockExchangeDto.cookieId },
        data: { cookie: 'NID=newvalue; JSESSIONID=newid' },
      });
    });

    it('should return no point action when save keyword is absent', async () => {
      const responseBody = '이미 참여하셨습니다.';
      mockedAxios.get.mockResolvedValue({
        data: responseBody,
        status: 200,
        headers: {},
      });

      prismaService.pointUrlCookie.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCallLog.create = jest.fn().mockResolvedValue({});

      const result = await service.exchange(mockPointUrl, mockExchangeDto);

      expect(result.success).toBe(false);
      expect(result.pointsSaved).toBe(0);
      expect(result.invalidCookie).toBe(false);
    });

    it('should save point URL cookie relationship', async () => {
      const responseBody = '이미 참여하셨습니다.';
      mockedAxios.get.mockResolvedValue({
        data: responseBody,
        status: 200,
        headers: {},
      });

      prismaService.pointUrlCookie.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCallLog.create = jest.fn().mockResolvedValue({});

      await service.exchange(mockPointUrl, mockExchangeDto);

      expect(prismaService.pointUrlCookie.create).toHaveBeenCalledWith({
        data: {
          pointUrlId: mockPointUrl.id,
          cookieId: mockExchangeDto.cookieId,
        },
      });
    });

    it('should save call log with response details', async () => {
      const responseBody = '이미 참여하셨습니다.';
      mockedAxios.get.mockResolvedValue({
        data: responseBody,
        status: 200,
        headers: { 'content-type': 'text/html' },
      });

      prismaService.pointUrlCookie.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCallLog.create = jest.fn().mockResolvedValue({});

      await service.exchange(mockPointUrl, mockExchangeDto);

      expect(prismaService.pointUrlCallLog.create).toHaveBeenCalledWith({
        data: {
          pointUrl: mockPointUrl.url,
          siteName: mockExchangeDto.siteName,
          userName: mockExchangeDto.userName,
          responseBody: responseBody,
          responseHeader: JSON.stringify({ 'content-type': 'text/html' }),
          cookie: mockExchangeDto.cookie,
          responseStatusCode: 200,
        },
      });
    });

    it('should return 0 points when amount pattern does not match', async () => {
      const responseBody = '적립되었습니다'; // No amount specified
      mockedAxios.get.mockResolvedValue({
        data: responseBody,
        status: 200,
        headers: {},
      });

      prismaService.savedPoint.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCookie.create = jest.fn().mockResolvedValue({});
      prismaService.pointUrlCallLog.create = jest.fn().mockResolvedValue({});

      const result = await service.exchange(mockPointUrl, mockExchangeDto);

      expect(result.pointsSaved).toBe(0);
    });
  });
});
