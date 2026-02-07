import { Controller, Get, Post, Put, Delete, Patch, Param, Body, HttpCode, HttpStatus } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBearerAuth } from '@nestjs/swagger';
import { PointUrlService } from './point-url.service';
import { CreatePointUrlDto, UpdatePointUrlDto, PointUrlResponseDto } from './dto/point-url.dto';

@ApiTags('Point URLs')
@ApiBearerAuth('JWT-auth')
@Controller('point-urls')
export class PointUrlController {
  constructor(private readonly pointUrlService: PointUrlService) {}

  @Get()
  @ApiOperation({ summary: '모든 포인트 URL 조회' })
  @ApiResponse({ status: 200, description: '포인트 URL 목록', type: [PointUrlResponseDto] })
  async findAll(): Promise<PointUrlResponseDto[]> {
    return this.pointUrlService.findAll();
  }

  @Get(':id')
  @ApiOperation({ summary: '포인트 URL 상세 조회' })
  @ApiResponse({ status: 200, description: '포인트 URL 정보', type: PointUrlResponseDto })
  @ApiResponse({ status: 404, description: '포인트 URL을 찾을 수 없음' })
  async findOne(@Param('id') id: string): Promise<PointUrlResponseDto> {
    return this.pointUrlService.findOne(Number(id));
  }

  @Post()
  @ApiOperation({ summary: '포인트 URL 생성' })
  @ApiResponse({ status: 200, description: '생성된 포인트 URL', type: PointUrlResponseDto })
  async create(@Body() createDto: CreatePointUrlDto): Promise<PointUrlResponseDto> {
    return this.pointUrlService.create(createDto);
  }

  @Put(':id')
  @ApiOperation({ summary: '포인트 URL 수정' })
  @ApiResponse({ status: 200, description: '수정된 포인트 URL', type: PointUrlResponseDto })
  @ApiResponse({ status: 404, description: '포인트 URL을 찾을 수 없음' })
  async update(@Param('id') id: string, @Body() updateDto: UpdatePointUrlDto): Promise<PointUrlResponseDto> {
    return this.pointUrlService.update(Number(id), updateDto);
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiOperation({ summary: '포인트 URL 삭제' })
  @ApiResponse({ status: 204, description: '삭제 성공' })
  @ApiResponse({ status: 404, description: '포인트 URL을 찾을 수 없음' })
  async delete(@Param('id') id: string): Promise<void> {
    return this.pointUrlService.delete(Number(id));
  }

  @Patch(':id/toggle-permanent')
  @ApiOperation({ summary: '포인트 URL 영구 상태 토글' })
  @ApiResponse({ status: 200, description: '토글된 포인트 URL', type: PointUrlResponseDto })
  @ApiResponse({ status: 404, description: '포인트 URL을 찾을 수 없음' })
  async togglePermanent(@Param('id') id: string): Promise<PointUrlResponseDto> {
    return this.pointUrlService.togglePermanent(Number(id));
  }
}
