import { Controller, Get, Delete, Param, Query, HttpCode, HttpStatus } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBearerAuth } from '@nestjs/swagger';
import { SavedPointService } from './saved-point.service';
import { SavedPointResponseDto, SavedPointQueryDto, SavedPointListResponseDto } from './dto/saved-point.dto';

@ApiTags('Point Logs')
@ApiBearerAuth('JWT-auth')
@Controller('point-logs')
export class SavedPointController {
  constructor(private readonly savedPointService: SavedPointService) {}

  @Get()
  @ApiOperation({ summary: '저장된 포인트 목록 조회' })
  @ApiResponse({ status: 200, description: '저장된 포인트 목록', type: SavedPointListResponseDto })
  async findAll(@Query() query: SavedPointQueryDto): Promise<SavedPointListResponseDto> {
    return this.savedPointService.findAll(query);
  }

  @Get(':id')
  @ApiOperation({ summary: '저장된 포인트 상세 조회' })
  @ApiResponse({ status: 200, description: '저장된 포인트 정보', type: SavedPointResponseDto })
  @ApiResponse({ status: 404, description: '저장된 포인트를 찾을 수 없음' })
  async findOne(@Param('id') id: string): Promise<SavedPointResponseDto> {
    return this.savedPointService.findOne(Number(id));
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiOperation({ summary: '저장된 포인트 삭제' })
  @ApiResponse({ status: 204, description: '삭제 성공' })
  @ApiResponse({ status: 404, description: '저장된 포인트를 찾을 수 없음' })
  async delete(@Param('id') id: string): Promise<void> {
    return this.savedPointService.delete(Number(id));
  }
}
