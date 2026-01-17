import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsBoolean, IsNotEmpty, IsOptional, IsString } from 'class-validator';
import { Cookie } from '@prisma/client';

export class CookieResponseDto {
  @ApiProperty()
  id: string;

  @ApiProperty()
  userName: string;

  @ApiProperty()
  siteName: string;

  @ApiPropertyOptional()
  cookie: string | null;

  @ApiProperty()
  isValid: boolean;

  @ApiProperty()
  createdDate: Date;

  @ApiProperty()
  modifiedDate: Date;

  static from(cookie: Cookie): CookieResponseDto {
    return {
      id: cookie.id.toString(),
      userName: cookie.userName,
      siteName: cookie.siteName,
      cookie: cookie.cookie,
      isValid: cookie.isValid,
      createdDate: cookie.createdDate,
      modifiedDate: cookie.modifiedDate,
    };
  }
}

export class CreateCookieDto {
  @ApiProperty({ description: '사용자 이름' })
  @IsString()
  @IsNotEmpty()
  userName: string;

  @ApiProperty({ description: '사이트 이름' })
  @IsString()
  @IsNotEmpty()
  siteName: string;

  @ApiPropertyOptional({ description: '쿠키 값' })
  @IsString()
  @IsOptional()
  cookie?: string;

  @ApiPropertyOptional({ description: '유효 여부', default: true })
  @IsBoolean()
  @IsOptional()
  isValid?: boolean;
}

export class UpdateCookieDto {
  @ApiPropertyOptional({ description: '사용자 이름' })
  @IsString()
  @IsOptional()
  userName?: string;

  @ApiPropertyOptional({ description: '사이트 이름' })
  @IsString()
  @IsOptional()
  siteName?: string;

  @ApiPropertyOptional({ description: '쿠키 값' })
  @IsString()
  @IsOptional()
  cookie?: string;

  @ApiPropertyOptional({ description: '유효 여부' })
  @IsBoolean()
  @IsOptional()
  isValid?: boolean;
}
