import { Controller, Get, Post, Put, Delete, Patch, Param, Body, HttpCode, HttpStatus } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBearerAuth } from '@nestjs/swagger';
import { CookieService } from './cookie.service';
import { CreateCookieDto, UpdateCookieDto, CookieResponseDto } from './dto/cookie.dto';
import { CurrentUser, CurrentUserPayload } from '../../../common/decorators/current-user.decorator';

@ApiTags('Cookies')
@ApiBearerAuth('JWT-auth')
@Controller('cookies')
export class CookieController {
  constructor(private readonly cookieService: CookieService) {}

  @Get()
  @ApiOperation({ summary: '모든 쿠키 조회' })
  @ApiResponse({ status: 200, description: '쿠키 목록', type: [CookieResponseDto] })
  async findAll(): Promise<CookieResponseDto[]> {
    return this.cookieService.findAll();
  }

  @Get(':id')
  @ApiOperation({ summary: '쿠키 상세 조회' })
  @ApiResponse({ status: 200, description: '쿠키 정보', type: CookieResponseDto })
  @ApiResponse({ status: 404, description: '쿠키를 찾을 수 없음' })
  async findOne(@Param('id') id: string): Promise<CookieResponseDto> {
    return this.cookieService.findOne(Number(id));
  }

  @Post()
  @ApiOperation({ summary: '쿠키 생성' })
  @ApiResponse({ status: 200, description: '생성된 쿠키', type: CookieResponseDto })
  async create(
    @Body() createDto: CreateCookieDto,
    @CurrentUser() user: CurrentUserPayload,
  ): Promise<CookieResponseDto> {
    return this.cookieService.create(user.userId, createDto);
  }

  @Put(':id')
  @ApiOperation({ summary: '쿠키 수정' })
  @ApiResponse({ status: 200, description: '수정된 쿠키', type: CookieResponseDto })
  @ApiResponse({ status: 404, description: '쿠키를 찾을 수 없음' })
  async update(@Param('id') id: string, @Body() updateDto: UpdateCookieDto): Promise<CookieResponseDto> {
    return this.cookieService.update(Number(id), updateDto);
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiOperation({ summary: '쿠키 삭제' })
  @ApiResponse({ status: 204, description: '삭제 성공' })
  @ApiResponse({ status: 404, description: '쿠키를 찾을 수 없음' })
  async delete(@Param('id') id: string): Promise<void> {
    return this.cookieService.delete(Number(id));
  }

  @Patch(':id/toggle-validity')
  @ApiOperation({ summary: '쿠키 유효성 토글' })
  @ApiResponse({ status: 200, description: '토글된 쿠키', type: CookieResponseDto })
  @ApiResponse({ status: 404, description: '쿠키를 찾을 수 없음' })
  async toggleValidity(@Param('id') id: string): Promise<CookieResponseDto> {
    return this.cookieService.toggleValidity(Number(id));
  }
}
