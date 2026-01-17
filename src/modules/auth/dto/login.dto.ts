import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsBoolean, IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class LoginRequestDto {
  @ApiProperty({ description: '로그인 ID' })
  @IsString()
  @IsNotEmpty({ message: '로그인 ID는 필수입니다' })
  loginId: string;

  @ApiProperty({ description: '비밀번호' })
  @IsString()
  @IsNotEmpty({ message: '비밀번호는 필수입니다' })
  password: string;

  @ApiPropertyOptional({ description: '로그인 유지 여부 (15일간 자동 로그인)', default: false })
  @IsBoolean()
  @IsOptional()
  rememberMe?: boolean = false;
}

export class LoginResponseDto {
  @ApiProperty({ description: 'JWT 액세스 토큰' })
  accessToken: string;

  @ApiProperty({ description: 'JWT 리프레시 토큰' })
  refreshToken: string;

  @ApiProperty({ description: '사용자 이름' })
  userName: string;

  @ApiProperty({ description: '로그인 ID' })
  loginId: string;
}

export class RefreshTokenRequestDto {
  @ApiProperty({ description: '리프레시 토큰' })
  @IsString()
  @IsNotEmpty({ message: '리프레시 토큰은 필수입니다' })
  refreshToken: string;
}

export class RefreshTokenResponseDto {
  @ApiProperty({ description: '새로운 JWT 액세스 토큰' })
  accessToken: string;

  @ApiProperty({ description: '새로운 JWT 리프레시 토큰' })
  refreshToken: string;
}
