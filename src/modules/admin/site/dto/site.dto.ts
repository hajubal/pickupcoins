import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsNotEmpty, IsOptional, IsString } from 'class-validator';
import { Site } from '@prisma/client';

export class SiteResponseDto {
  @ApiProperty()
  id: string;

  @ApiProperty()
  name: string;

  @ApiProperty()
  url: string;

  @ApiProperty()
  createdDate: Date;

  @ApiProperty()
  modifiedDate: Date;

  static from(site: Site): SiteResponseDto {
    return {
      id: site.id.toString(),
      name: site.name,
      url: site.url,
      createdDate: site.createdDate,
      modifiedDate: site.modifiedDate,
    };
  }
}

export class CreateSiteDto {
  @ApiProperty({ description: '사이트 이름' })
  @IsString()
  @IsNotEmpty()
  name: string;

  @ApiProperty({ description: 'URL' })
  @IsString()
  @IsNotEmpty()
  url: string;
}

export class UpdateSiteDto {
  @ApiPropertyOptional({ description: '사이트 이름' })
  @IsString()
  @IsOptional()
  name?: string;

  @ApiPropertyOptional({ description: 'URL' })
  @IsString()
  @IsOptional()
  url?: string;
}
