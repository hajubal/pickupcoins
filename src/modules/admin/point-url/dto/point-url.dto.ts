import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsBoolean, IsNotEmpty, IsOptional, IsString } from 'class-validator';
import { PointUrl } from '@prisma/client';

export type PointUrlType = 'NAVER' | 'OFW_NAVER' | 'UNSUPPORT';

export class PointUrlResponseDto {
  @ApiProperty()
  id: string;

  @ApiProperty()
  name: string;

  @ApiProperty()
  url: string;

  @ApiPropertyOptional({ enum: ['NAVER', 'OFW_NAVER', 'UNSUPPORT'] })
  pointUrlType: PointUrlType | null;

  @ApiProperty()
  permanent: boolean;

  @ApiProperty()
  createdDate: Date;

  @ApiProperty()
  modifiedDate: Date;

  static from(pointUrl: PointUrl): PointUrlResponseDto {
    return {
      id: pointUrl.id.toString(),
      name: pointUrl.name,
      url: pointUrl.url,
      pointUrlType: pointUrl.pointUrlType as PointUrlType | null,
      permanent: pointUrl.permanent,
      createdDate: pointUrl.createdDate,
      modifiedDate: pointUrl.modifiedDate,
    };
  }
}

export class CreatePointUrlDto {
  @ApiProperty({ description: 'URL' })
  @IsString()
  @IsNotEmpty()
  url: string;

  @ApiPropertyOptional({ description: '영구 URL 여부', default: false })
  @IsBoolean()
  @IsOptional()
  permanent?: boolean;
}

export class UpdatePointUrlDto {
  @ApiPropertyOptional({ description: 'URL' })
  @IsString()
  @IsOptional()
  url?: string;

  @ApiPropertyOptional({ description: '영구 URL 여부' })
  @IsBoolean()
  @IsOptional()
  permanent?: boolean;
}

// Utility function to classify URL type
export function classifyUrlType(url: string): PointUrlType {
  if (url.includes('campaign2-api.naver.com')) {
    return 'NAVER';
  } else if (url.includes('ofw.adison.co/u/naverpay')) {
    return 'OFW_NAVER';
  }
  return 'UNSUPPORT';
}
