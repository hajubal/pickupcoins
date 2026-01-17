import { Controller, Get, Post, Put, Delete, Param, Body, HttpCode, HttpStatus } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBearerAuth } from '@nestjs/swagger';
import { SiteService } from './site.service';
import { CreateSiteDto, UpdateSiteDto, SiteResponseDto } from './dto/site.dto';

@ApiTags('Sites')
@ApiBearerAuth('JWT-auth')
@Controller('sites')
export class SiteController {
  constructor(private readonly siteService: SiteService) {}

  @Get()
  @ApiOperation({ summary: '모든 사이트 조회' })
  @ApiResponse({ status: 200, description: '사이트 목록', type: [SiteResponseDto] })
  async findAll(): Promise<SiteResponseDto[]> {
    return this.siteService.findAll();
  }

  @Get(':id')
  @ApiOperation({ summary: '사이트 상세 조회' })
  @ApiResponse({ status: 200, description: '사이트 정보', type: SiteResponseDto })
  @ApiResponse({ status: 404, description: '사이트를 찾을 수 없음' })
  async findOne(@Param('id') id: string): Promise<SiteResponseDto> {
    return this.siteService.findOne(BigInt(id));
  }

  @Post()
  @ApiOperation({ summary: '사이트 생성' })
  @ApiResponse({ status: 200, description: '생성된 사이트', type: SiteResponseDto })
  async create(@Body() createDto: CreateSiteDto): Promise<SiteResponseDto> {
    return this.siteService.create(createDto);
  }

  @Put(':id')
  @ApiOperation({ summary: '사이트 수정' })
  @ApiResponse({ status: 200, description: '수정된 사이트', type: SiteResponseDto })
  @ApiResponse({ status: 404, description: '사이트를 찾을 수 없음' })
  async update(@Param('id') id: string, @Body() updateDto: UpdateSiteDto): Promise<SiteResponseDto> {
    return this.siteService.update(BigInt(id), updateDto);
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiOperation({ summary: '사이트 삭제' })
  @ApiResponse({ status: 204, description: '삭제 성공' })
  @ApiResponse({ status: 404, description: '사이트를 찾을 수 없음' })
  async delete(@Param('id') id: string): Promise<void> {
    return this.siteService.delete(BigInt(id));
  }
}
