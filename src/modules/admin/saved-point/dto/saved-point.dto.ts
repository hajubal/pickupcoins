import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsOptional, IsString } from 'class-validator';
import { SavedPoint, Cookie } from '@prisma/client';
import { PaginationDto } from '../../../../common/dto/pagination.dto';

export class SavedPointResponseDto {
  @ApiProperty()
  id: string;

  @ApiProperty()
  cookieId: string;

  @ApiProperty()
  amount: number;

  @ApiPropertyOptional()
  responseBody: string | null;

  @ApiProperty()
  createdDate: Date;

  @ApiProperty()
  modifiedDate: Date;

  @ApiPropertyOptional()
  userName?: string;

  @ApiPropertyOptional()
  siteName?: string;

  static from(savedPoint: SavedPoint & { cookie?: Cookie }): SavedPointResponseDto {
    return {
      id: savedPoint.id.toString(),
      cookieId: savedPoint.cookieId.toString(),
      amount: savedPoint.amount,
      responseBody: savedPoint.responseBody,
      createdDate: savedPoint.createdDate,
      modifiedDate: savedPoint.modifiedDate,
      userName: savedPoint.cookie?.userName,
      siteName: savedPoint.cookie?.siteName,
    };
  }
}

export class SavedPointQueryDto extends PaginationDto {
  @ApiPropertyOptional({ description: '시작 날짜 (YYYY-MM-DD)' })
  @IsString()
  @IsOptional()
  startDate?: string;

  @ApiPropertyOptional({ description: '종료 날짜 (YYYY-MM-DD)' })
  @IsString()
  @IsOptional()
  endDate?: string;
}

export class SavedPointListResponseDto {
  @ApiProperty({ type: [SavedPointResponseDto] })
  content: SavedPointResponseDto[];

  @ApiProperty()
  currentPage: number;

  @ApiProperty()
  totalItems: number;

  @ApiProperty()
  totalPages: number;
}
